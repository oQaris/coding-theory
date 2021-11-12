import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    application
}

group = "me.oqaris"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Парсер командной строки
    implementation("info.picocli:picocli:4.6.1")
    // Библиотека для работы с матрицами
    implementation("org.ejml:ejml-all:0.41")
    // Комбинаторика
    implementation("com.github.shiguruikai:combinatoricskt:1.6.0")

    implementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}
