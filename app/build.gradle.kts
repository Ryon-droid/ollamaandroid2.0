plugins {
    id("com.android.application")
}

object Versions {
    const val compileSdk = 34
    const val minSdk = 30
    const val targetSdk = 34
    const val appCompat = "1.6.1"
    const val recyclerView = "1.3.2"
    const val securityCrypto = "1.1.0-alpha03"
    const val httpclient5 = "5.3"
    const val jacksonDatabind = "2.17.0" // 修正为有效版本
}

android {
    namespace = "com.example.ollamaandroid" // 添加命名空间
    compileSdk = Versions.compileSdk

    defaultConfig {
        applicationId = "com.example.ollamaandroid"
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // 解决 META-INF/DEPENDENCIES 冲突
    packagingOptions {
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/LICENSE")
        resources.excludes.add("META-INF/LICENSE.txt")
        resources.excludes.add("META-INF/license.txt")
        resources.excludes.add("META-INF/NOTICE")
        resources.excludes.add("META-INF/NOTICE.txt")
        resources.excludes.add("META-INF/notice.txt")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:${Versions.appCompat}")
    implementation("androidx.recyclerview:recyclerview:${Versions.recyclerView}")
    implementation("androidx.security:security-crypto:${Versions.securityCrypto}")
    implementation("org.apache.httpcomponents.client5:httpclient5:${Versions.httpclient5}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${Versions.jacksonDatabind}")
    implementation("org.apache.httpcomponents.core5:httpcore5:5.1.3")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.1.3")

    // 测试依赖
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}