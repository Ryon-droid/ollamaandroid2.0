buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.2")
    }
}

// 定义全局版本（Kotlin DSL 风格）
object Versions {
    const val compileSdk = 34
    const val minSdk = 30
    const val targetSdk = 34
    const val appCompat = "1.6.1"
    const val recyclerView = "1.3.2"
    const val securityCrypto = "1.1.0-alpha03"
    const val httpclient5 = "5.3"
    const val jacksonDatabind = "2.17.0"
}