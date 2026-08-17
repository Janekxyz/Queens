# Queens

An N-Queens puzzle game for Android. Pick a board size and a queen color, then place `n` queens so
that none of them attack each other. Solved games are timed and stored, and the leaderboard shows
your best time for each board size.

## Build, run and test

Requires JDK 21 and the Android SDK with platform **37** installed. Everything below runs from the
project root; no local configuration beyond `local.properties` pointing at your SDK.

```bash
./gradlew :app:installDebug          # build and install on a connected device or emulator
./gradlew testDebugUnitTest          # 64 JVM unit tests
./gradlew connectedDebugAndroidTest  # 38 instrumented tests (needs a device or emulator)
./gradlew build                      # full build including lint
```

There is an apk file named `game.apk` available in the sources so you can download it and put it on the device
in order to test the application without need of building it.

## Architecture

### Module structure

| Module | Contains                                                               |
| --- |------------------------------------------------------------------------|
| `:app` | `MainActivity`, the navigation graph, the Hilt composition root        |
| `:features:queengame` | Board configuration and gameplay screens                               |
| `:features:leaderboard:{api,impl}` | Leaderboard screen to display the best results based on the board size |
| `:board` | Board model and the reusable `BoardContent` renderer                   |
| `:gameresult:{api,impl}` | Game-result domain model, repository contract and implementation       |
| `:database` | Room database, entities, DAOs and their Hilt bindings                  |
| `:core:time` | `TimeProvider` abstraction over the system clocks                      |
| `:core:testing` | Shared test rules and fakes                                            |
| `:styleguide` | Theme, colors, typography, shared drawables and strings               |

Dependencies only ever point downwards, and no feature depends on another feature. The game writes
results and the leaderboard reads them, but neither knows the other exists — they share
`:gameresult:api`, which holds the domain model and the repository interface and nothing else.

### api / impl splits

`:gameresult` and `:features:leaderboard` are split into `api` and `impl` because more than one
module needs the contract while only `:app` should see the implementation. `:app` is the single
place that depends on an `impl` module, which makes it the composition root.

`:board` is deliberately **not** split. It has one implementation and one consumer group, so Kotlin
`internal` gives the same encapsulation without a second Gradle module.

### Navigation

Navigation 3 is used. The back stack is ordinary state that `:app` owns — navigating is `backStack.add(key)`
or `backStack.removeLastOrNull()`

Each feature owns its route key and exposes an *entry function* rather than its composables:

```kotlin
fun EntryProviderScope<NavKey>.gameConfigurationEntry(
    onPlayClick: (Int, QueenColor) -> Unit,
    onLeaderboardClick: () -> Unit,
)
```

The screens themselves are `internal`. A feature can therefore change its UI freely, and the
callbacks in the entry signature are the seam that stops features from importing one another —
`gameConfigurationEntry` says "someone will handle Play with this board size" without knowing that
`QueenGameKey` exists.

Route keys are `@Serializable`, so the whole back stack — including the chosen board size and queen
color — survives process death without any extra saved-state handling.

### Unidirectional data flow

Every screen follows the same shape:

```
ViewModel  →  ViewState  →  @Composable mapper  →  Params  →  stateless content
```

The ViewModel exposes a `StateFlow<ViewState>` of domain values. A `@Composable` mapper turns that
into a `Params` object holding resolved strings and colors, and the content composables render
params and emit callbacks. Nothing below the mapper touches resources, and nothing above it touches
Compose.

Params objects group values that change *together*. The game timer is deliberately a separate
`StateFlow` read through a lambda, because folding a per-second value into the board's params would
rebuild the whole tile map every second and defeat per-tile recomposition skipping.

### Game logic

Conflict detection is `QueenAttackMap`: four counter arrays for rows, columns, diagonals and
anti-diagonals. Testing whether a square is attacked or conflicted is O(1), and the map is rebuilt
from the queen set on every change rather than updated incrementally — removing a queen cannot
subtract its rays safely when another queen covers the same line, so a full rebuild is both simpler
and impossible to desynchronise.

Derived state stays derived: `isSolved` is a computed property of the view state rather than a flag
that has to be maintained.

### Persistence

Room lives entirely in `:database`, which owns the schema, the DAOs and the exported schema JSON.
Feature modules never see an entity — `:gameresult:impl` maps entities to domain models in its
repository, and that mapping is the only place the two vocabularies meet.

Writes and reads use different types. `GameResultDraft` carries only what the game measures
(duration and board size); `GameResult` adds the `id` that exists once the row does. This means no
caller has to invent an identity for something that has not been stored yet.

The leaderboard query returns the best result per board size, computed in SQL rather than in Kotlin,
and returns a `Flow` — so finishing a game updates the leaderboard with no refresh logic and no
cross-feature events.

## Testing

**64 unit tests** on the JVM (JUnit 5, Turbine, coroutines-test) and **38 instrumented tests**
(JUnit 4 with `AndroidJUnit4`, Compose UI test).

| Suite | Covers |
| --- | --- |
| `QueenAttackMapTest` | Attack and conflict rules, every line and both board sizes |
| `QueenGameViewModelTest` | Placement, removal, the queen limit, solving, timing, restart |
| `GameConfigurationViewModelTest` | Board-size bounds, screen maximum, color selection |
| `LeaderboardViewModelTest` | Loading, content and error states |
| `GameResultRepositoryImplTest` | Entity mapping, timestamping, ordering |
| `GameResultDaoTest` | Real SQLite: inserts, generated ids, best-per-size query, `Flow` updates |
| `BoardContentTest` | The custom `Layout` — cell count, grid positions, even division |
| `QueenGameScreenTest` | Placing and removing on screen, the success overlay, restart |
| `GameConfigurationScreenTest` | Stepper bounds, color selection, what Play reports |
| `LeaderboardScreenTest` | Empty, content and error rendering |

Unit tests construct ViewModels directly with fakes. UI tests select nodes by text and content description rather than test
tags, so they assert what a user perceives and keep accessibility honest; the only test tag is on
board cells, which need a stable identity because their description changes as the game progresses.



Built with Kotlin, Jetpack Compose, Navigation 3, Hilt and Room.
