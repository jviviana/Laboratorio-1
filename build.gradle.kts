plugins {
    id("java")
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://www.jetbrains.com/intellij-repository/releases")
    maven("https://cache-redirector.jetbrains.com/intellij-dependencies")
}

val compiladorFormularios: Configuration by configurations.creating

dependencies {
    implementation("org.json:json:20240303")

    implementation("com.jetbrains.intellij.java:java-gui-forms-rt:233.15619.17")
    compiladorFormularios("com.jetbrains.intellij.java:java-compiler-ant-tasks:233.15619.17")

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("yugioh.Main")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.named<JavaCompile>("compileJava") {
    inputs.files(fileTree("src/main/java") { include("**/*.form") })
    doLast {
        ant.withGroovyBuilder {
            "taskdef"(
                "name" to "instrumentIdeaExtensions",
                "classname" to "com.intellij.ant.InstrumentIdeaExtensions",
                "classpath" to compiladorFormularios.asPath
            )
            "instrumentIdeaExtensions"(
                "srcdir" to file("src/main/java"),
                "destdir" to destinationDirectory.get().asFile,
                "classpath" to (classpath.asPath + File.pathSeparator + destinationDirectory.get().asFile),
                "includeantruntime" to false,
                "instrumentNotNull" to false
            )
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
