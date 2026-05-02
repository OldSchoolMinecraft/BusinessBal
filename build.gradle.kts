plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("libs/craftbukkit.jar"))
    implementation(files("libs/Vault.jar"))

    implementation("com.google.code.gson:gson:2.14.0")
}