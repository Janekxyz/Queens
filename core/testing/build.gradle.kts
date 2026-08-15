plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.jaxjack.queens.core.testing"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 31
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE.md,LICENSE-notice.md}"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    api(platform(libs.junit5.bom))
    api(libs.junit5.jupiter)
    api(libs.turbine)
    api(libs.kotlinx.coroutines.test)
    api(project(":core:time"))
    api(project(":features:gameresult:api"))
}
