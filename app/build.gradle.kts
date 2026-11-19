plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

fun quoted(value: String) = "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""

val configuredUrl = (project.findProperty("websiteUrl") as String?) ?: "https://example.com"
val configuredName = (project.findProperty("appName") as String?) ?: "My TV Website"

android {
    namespace = "com.skkaushal.websitetv"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.skkaushal.websitetv"
        minSdk = 23
        targetSdk = 28
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "WEBSITE_URL", quoted(configuredUrl))
        resValue("string", "app_name", configuredName)
    }

    buildFeatures { buildConfig = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
