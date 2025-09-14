plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("maven-publish")
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.16.3"
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
dependencies {
    testImplementation(kotlin("test"))
}

apiValidation {
    // Validate only public API
    publicMarkers.add("com.pisces.xcodebn.passwordvalidator.PasswordValidator")
    
    // Ignore internal classes from validation
    ignoredClasses.addAll(listOf(
        // Add any internal classes you don't want to track
    ))
    
    // Optional: Configure for specific packages
    validationDisabled = false
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            
            groupId = "com.pisces.xcodebn"
            artifactId = "password-validator"
            version = "1.0.0"
            
            pom {
                name.set("Password Validator")
                description.set("A modular, extensible password validation library for Kotlin with customizable rules and error handling")
                url.set("https://github.com/xcodeBn/PasswordValidatorKotlin")
                
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                
                developers {
                    developer {
                        id.set("xcodeBn")
                        name.set("Hassan Bazzoun")
                        email.set("hassan.bazzoundev@gmail.com")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/xcodeBn/PasswordValidatorKotlin.git")
                    developerConnection.set("scm:git:ssh://github.com:xcodeBn/PasswordValidatorKotlin.git")
                    url.set("https://github.com/xcodeBn/PasswordValidatorKotlin")
                }
            }
        }
    }
}