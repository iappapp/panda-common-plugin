plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.9.25"
  id("org.jetbrains.intellij") version "1.17.4"
  kotlin("plugin.lombok") version "1.9.0"
}

group = "com.github.iappapp.panda.common"
version = "1.3.0"

repositories {
  mavenLocal()
  mavenCentral()
}

dependencies {
  implementation("org.freemarker:freemarker:2.3.31")
  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("com.alibaba:druid:1.2.16")
  compileOnly("org.projectlombok:lombok:1.18.30")
  annotationProcessor("org.projectlombok:lombok:1.18.30")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
  testImplementation("org.mockito:mockito-core:5.12.0")
  implementation(kotlin("stdlib"))
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set("2024.1.7")
  type.set("IC") // Target IDE Platform

  plugins.set(listOf("com.intellij.java"))
}

sourceSets {
  main {
    java{
      srcDirs("src/main/java")
    }
    kotlin {
      srcDirs("src/main/kotlin")
    }
    resources {
      srcDirs("src/main/resources")
      include("**/*.ftl", "**/*.xml", "**/*.svg", "META-INF/**")
    }
  }
}

tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
  }

  withType<Test> {
    useJUnitPlatform()
  }

  patchPluginXml {
    sinceBuild.set("241")
    untilBuild.set("253.*")
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }

  processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  }
}
