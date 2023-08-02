plugins {
    id("java")
    application
}

group = "com.newrelic.repro"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.newrelic.agent.java:newrelic-api:8.5.0")
}

tasks.test {
    useJUnitPlatform()
}

application {
    // Define the main class for the application.
    mainClass.set("com.newrelic.repro.SecurityAgentTraceAnnotationRepro")
    applicationDefaultJvmArgs = listOf(
            "-javaagent:./newrelic/newrelic.jar",
            "-Dnewrelic.config.file=./newrelic/newrelic-with-security-config.yml",
            "-Dnewrelic.config.license_key=NR_LICENSE_KEY",
            "-Xdebug"
    )
}
