import com.google.protobuf.gradle.ExecutableLocator
import com.google.protobuf.gradle.GenerateProtoTask
import com.google.protobuf.gradle.ProtobufConfigurator
import org.gradle.api.internal.tasks.compile.CleaningGroovyCompiler
import org.gradle.internal.impldep.org.eclipse.jgit.api.CleanCommand
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.51"
    id("com.google.protobuf") version "0.8.6"
    id("org.springframework.boot") version "2.0.5.RELEASE"
    jacoco
}

group = "com.njkim"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<JacocoReport> {
    reports {
        xml.isEnabled = true
    }
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    // kotlin reflect
    compile(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = "1.2.71")

    // Bouncy castle for crypto
    compile("org.bouncycastle:bcprov-jdk15on:1.60")
    compile("org.bouncycastle:bcpkix-jdk15on:1.60")

    // functional programming libs
    compile("io.vavr:vavr:0.9.2")

    // react rxKotlin(rxJava)
    compile(group = "io.reactivex.rxjava2", name = "rxkotlin", version = "2.3.0")

    // grpc lib (armeria)
    compile(group = "com.linecorp.armeria", name = "armeria-grpc", version = "0.73.0")

    compile(group = "javax.xml.bind", name = "jaxb-api", version = "2.3.1")

    // spring boot
    compile("org.springframework.boot:spring-boot-starter:2.0.5.RELEASE")
    compile(group = "org.springframework.boot", name = "spring-boot-starter-web", version = "2.0.6.RELEASE")

    // database
    compile(group = "org.rocksdb", name = "rocksdbjni", version = "5.15.10")

    // redisson
    compile("org.redisson:redisson:3.8.2")

    // test framework
    testCompile(group = "junit", name = "junit", version = "4.12")
    testCompile(group = "org.springframework.boot", name = "spring-boot-starter-test", version = "2.0.5.RELEASE")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

java.sourceSets["main"].withConvention(KotlinSourceSet::class) {
    kotlin.srcDirs(
            file("src/main/kotlin"),
            file("build/generated/source/proto/main/java"),
            file("build/generated/source/proto/main/grpc")
    )
}

java.sourceSets {
    getByName("main") {
        withGroovyBuilder {
            "proto" {
                "srcDir"("src/main/proto")
            }
        }
    }
}


protobuf.protobuf.run {
    protoc(delegateClosureOf<ExecutableLocator> {
        artifact = "com.google.protobuf:protoc:3.5.1-1"
    })
    plugins(delegateClosureOf<NamedDomainObjectContainer<ExecutableLocator>> {
        this {
            "grpc" {
                artifact = "io.grpc:protoc-gen-grpc-java:1.15.1"
            }
        }
    })
    generateProtoTasks(delegateClosureOf<ProtobufConfigurator.GenerateProtoTaskCollection> {
        all().forEach {
            it.plugins(delegateClosureOf<NamedDomainObjectContainer<GenerateProtoTask.PluginOptions>> {
                this {
                    "grpc"()
                }
            })
        }
    })
}
