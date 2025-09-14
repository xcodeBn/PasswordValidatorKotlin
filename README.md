# Password Validator

A modular, extensible password validation library for Kotlin with customizable rules and error handling.

[![](https://jitpack.io/v/xcodeBn/PasswordValidatorKotlin.svg)](https://jitpack.io/#xcodeBn/PasswordValidatorKotlin)

## Features

✅ **Modular Design** - Individual validation rules that can be combined  
✅ **Builder Pattern** - Fluent API for creating custom validators  
✅ **Custom Rules** - Easy to add your own validation logic  
✅ **Custom Error Messages** - Meaningful, context-specific error feedback  
✅ **Kotlin Result API** - Type-safe error handling  
✅ **Comprehensive Testing** - Full test coverage for all components  
✅ **Zero Dependencies** - Pure Kotlin implementation  

## Installation

### Gradle (Kotlin DSL)

Add JitPack repository to your project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency to your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.xcodeBn:PasswordValidatorKotlin:1.0.1")
}
```

### Gradle (Groovy)

Add JitPack repository to your project's `settings.gradle`:

```groovy
dependencyResolutionManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency to your module's `build.gradle`:

```groovy
dependencies {
    implementation 'com.github.xcodeBn:PasswordValidatorKotlin:1.0.1'
}
```

### Maven

Add JitPack repository to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add the dependency:

```xml
<dependency>
    <groupId>com.github.xcodeBn</groupId>
    <artifactId>PasswordValidatorKotlin</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Quick Start

### Basic Usage

```kotlin
// Use default validator (8+ chars, uppercase, digit, special char)
val validator = PasswordValidator.defaultRules()
val result = validator.validate("MyPassword123!")

if (result.isValid) {
    println("Password is valid!")
} else {
    result.errors.forEach { error ->
        println(error.description)
    }
}
```

### Custom Validator

```kotlin
// Build a custom validator
val customValidator = PasswordValidator.builder()
    .minLength(12)
    .requireUppercase()
    .requireDigit()
    .requireSpecialCharacter("!@#$%")
    .build()

val result = customValidator.validate("MyCustomPass123!")
```

### Custom Rules

```kotlin
// Create your own validation rule
class NoConsecutiveCharsRule : PasswordRule {
    override fun validate(password: String): Result<Unit> {
        for (i in 0 until password.length - 1) {
            if (password[i] == password[i + 1]) {
                return Result.failure(
                    PasswordError.Custom("Password must not contain consecutive identical characters")
                )
            }
        }
        return Result.success(Unit)
    }
}

// Use it in a validator
val validator = PasswordValidator.builder()
    .minLength(8)
    .requireUppercase()
    .addRule(NoConsecutiveCharsRule())
    .build()
```

## Available Rules

### Built-in Rules

- **MinLengthRule** - Configurable minimum character length
- **UppercaseRule** - Requires at least one uppercase letter
- **DigitRule** - Requires at least one numeric digit  
- **SpecialCharacterRule** - Requires at least one special character (configurable set)

### Builder Methods

```kotlin
PasswordValidator.builder()
    .minLength(length: Int)                           // Default: 8
    .requireUppercase()                               // Require uppercase letter
    .requireDigit()                                   // Require numeric digit
    .requireSpecialCharacter(chars: String? = null)   // Custom special chars
    .addRule(rule: PasswordRule)                      // Add custom rule
    .build()
```

## Error Handling

The library provides detailed error information:

```kotlin
val result = validator.validate("weak")

if (!result.isValid) {
    result.errors.forEach { error ->
        when (error) {
            is PasswordError.TooShort -> println("Password too short")
            is PasswordError.MissingUppercase -> println("Add uppercase letter")
            is PasswordError.MissingDigit -> println("Add a number")
            is PasswordError.MissingSpecialChar -> println("Add special character")
            is PasswordError.Custom -> println("Custom rule: ${error.message}")
        }
    }
}
```

## Android Integration

### With Compose

```kotlin
class PasswordValidationViewModel(
    private val validator: PasswordValidator = PasswordValidator.defaultRules()
) : ViewModel() {
    
    private val _validationResult = mutableStateOf<ValidationResult?>(null)
    val validationResult: State<ValidationResult?> = _validationResult
    
    fun validatePassword(password: String) {
        _validationResult.value = validator.validate(password)
    }
}

@Composable
fun PasswordField(viewModel: PasswordValidationViewModel) {
    var password by remember { mutableStateOf("") }
    val validationResult by viewModel.validationResult
    
    OutlinedTextField(
        value = password,
        onValueChange = { 
            password = it
            viewModel.validatePassword(it)
        },
        label = { Text("Password") },
        isError = validationResult?.isValid == false
    )
    
    validationResult?.errors?.forEach { error ->
        Text(
            text = error.description,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
```

## Advanced Examples

### Complex Business Rules

```kotlin
class BusinessPasswordRule : PasswordRule {
    override fun validate(password: String): Result<Unit> {
        return when {
            password.length < 12 -> 
                Result.failure(PasswordError.Custom("Business policy requires 12+ characters"))
            password.contains("password", ignoreCase = true) ->
                Result.failure(PasswordError.Custom("Cannot contain the word 'password'"))
            !hasAlternatingCase(password) ->
                Result.failure(PasswordError.Custom("Must have alternating upper/lower case"))
            else -> Result.success(Unit)
        }
    }
    
    private fun hasAlternatingCase(password: String): Boolean {
        // Implementation details...
        return true
    }
}
```

### Multiple Validation Policies

```kotlin
object PasswordPolicies {
    val basic = PasswordValidator.builder()
        .minLength(6)
        .requireUppercase()
        .build()
    
    val standard = PasswordValidator.defaultRules()
    
    val enterprise = PasswordValidator.builder()
        .minLength(14)
        .requireUppercase()
        .requireDigit()
        .requireSpecialCharacter()
        .addRule(BusinessPasswordRule())
        .build()
}
```

## Testing

The library includes comprehensive test coverage:

```bash
./gradlew :validator:test
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

```
MIT License

Copyright (c) 2024 xcodeBn

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

**Author:** [xcodeBn](https://github.com/xcodeBn)  
**Repository:** [PasswordValidatorKotlin](https://github.com/xcodeBn/PasswordValidatorKotlin)