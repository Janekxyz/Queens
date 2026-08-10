plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.jaxjack.queengambit.board.impl"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 31

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // `api` so a consumer wiring up :board:impl also sees the contract types it returns.
    api(project(":board:api"))
    testImplementation(libs.junit)
}
