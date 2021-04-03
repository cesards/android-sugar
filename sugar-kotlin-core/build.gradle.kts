import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    //kotlin("jvm") version "1.4.31"
    id("java-library")
    //id("kotlin") version "1.4.31"
    id("kotlin")
}

dependencies {
    //implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.31")
    //testRuntime("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    //testRuntime("org.junit.platform:junit-platform-runner:1.5.2")

    //testImplementation(platform("org.junit:junit-bom:5.7.1"))
    //testImplementation("org.junit.jupiter:junit-jupiter")

    // Unit Testing
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    //testImplementation("org.mockito:mockito-junit-jupiter:2.23.0")

    //testImplementation 'org.junit.jupiter:junit-jupiter-api:5.3.1'
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.23.1")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

// config JVM target to 1.8 for kotlin compilation tasks
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        //languageVersion = "1.5"
    }
}


/*
plugins {
    id("java-library")
    id("kotlin")
    //kotlin("jvm")
    //kotlin("kapt")
}
*/



/*
plugins {
    id 'java-library'
    id 'kotlin'
}

// enable library development (public sdk)

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

*//*test { // NOT NEEDED YET?
  useJunitPlatform()
}*//*

dependencies {
    // Unit Testing
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.5.0'
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.5.0'
    testImplementation 'org.mockito:mockito-junit-jupiter:2.23.0'
}*/
