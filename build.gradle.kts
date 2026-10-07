plugins {
    id("java")
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    // aqui estan las librerias de JetBrains que leen los archivos .form (GUI Designer de IntelliJ)
    maven("https://www.jetbrains.com/intellij-repository/releases")
    maven("https://cache-redirector.jetbrains.com/intellij-dependencies")
}

// Configuracion aparte para la herramienta que "compila" los .form (no se incluye en el programa)
val compiladorFormularios: Configuration by configurations.creating

dependencies {
    // libreria para leer el JSON de la API
    implementation("org.json:json:20240303")

    // libreria que necesitan los .form al ejecutarse (GridLayoutManager de IntelliJ)
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
    options.encoding = "UTF-8" //para que las tildes y la ñ se vean bien
}

// Gradle no entiende los archivos .form por si solo. Despues de compilar, esta tarea
// lee cada .form y agrega a la clase el codigo que arma la ventana (igual que hace IntelliJ).
tasks.named<JavaCompile>("compileJava") {
    // si solo cambias un .form (en el diseñador), igual se vuelve a compilar
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
