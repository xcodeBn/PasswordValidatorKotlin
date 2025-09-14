package com.pisces.xcodebn.passwordvalidator

/**
 * A modular password validation library for Kotlin
 * 
 * This library provides a flexible, extensible way to validate passwords using
 * a composable rule-based system with comprehensive error reporting.
 * 
 * @author Hassan Bazzoun (xcodeBn@github)
 * @since 1.0.0
 */

/**
 * Sealed class representing all possible password validation errors.
 * 
 * Each error type provides a human-readable description that can be shown to users.
 * The Custom error type allows for user-defined validation rules with custom messages.
 */
sealed class PasswordError : Throwable() {
    object TooShort : PasswordError()
    object MissingUppercase : PasswordError()
    object MissingDigit : PasswordError()
    object MissingSpecialChar : PasswordError()
    data class Custom(override val message: String) : PasswordError()

    val description: String
        get() = when (this) {
            is TooShort -> "Password must be at least 8 characters long"
            is MissingUppercase -> "Password must include an uppercase letter"
            is MissingDigit -> "Password must include a number"
            is MissingSpecialChar -> "Password must include a special character"
            is Custom -> message
        }
}

/**
 * Contains the result of password validation.
 * 
 * @property isValid true if password passed all validation rules
 * @property errors list of validation errors (empty if valid)
 */
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<PasswordError> = emptyList()
) {
    companion object {
        fun success() = ValidationResult(true)
        fun failure(errors: List<PasswordError>) = ValidationResult(false, errors)
    }
}

/**
 * Interface for password validation rules.
 * 
 * Implement this interface to create custom validation logic.
 * Each rule should return Result.success() if valid, or Result.failure() with a PasswordError.
 */
interface PasswordRule {
    fun validate(password: String): Result<Unit>
}

/**
 * Validates minimum password length.
 * 
 * @param minLength minimum number of characters required (default: 8)
 */
class MinLengthRule(private val minLength: Int = 8) : PasswordRule {
    override fun validate(password: String): Result<Unit> {
        return if (password.length >= minLength) {
            Result.success(Unit)
        } else {
            Result.failure(PasswordError.TooShort)
        }
    }
}

/**
 * Validates that password contains at least one uppercase letter.
 */
class UppercaseRule : PasswordRule {
    override fun validate(password: String): Result<Unit> {
        return if (password.any { it.isUpperCase() }) {
            Result.success(Unit)
        } else {
            Result.failure(PasswordError.MissingUppercase)
        }
    }
}

/**
 * Validates that password contains at least one numeric digit.
 */
class DigitRule : PasswordRule {
    override fun validate(password: String): Result<Unit> {
        return if (password.any { it.isDigit() }) {
            Result.success(Unit)
        } else {
            Result.failure(PasswordError.MissingDigit)
        }
    }
}

/**
 * Validates that password contains at least one special character.
 * 
 * @param specialChars string containing allowed special characters
 *                     (default: "!@#$%^&*(),.?\":{}|<>-_+=[]\\;'`~")
 */
class SpecialCharacterRule(
    private val specialChars: String = "!@#$%^&*(),.?\":{}|<>-_+=[]\\;'`~"
) : PasswordRule {
    override fun validate(password: String): Result<Unit> {
        return if (password.any { it in specialChars }) {
            Result.success(Unit)
        } else {
            Result.failure(PasswordError.MissingSpecialChar)
        }
    }
}

/**
 * Main password validator that orchestrates multiple validation rules.
 * 
 * Use the Builder pattern to create validators with custom rule combinations:
 * ```
 * val validator = PasswordValidator.builder()
 *     .minLength(12)
 *     .requireUppercase()
 *     .requireDigit()
 *     .build()
 * ```
 * 
 * Or use the default configuration:
 * ```
 * val validator = PasswordValidator.defaultRules()
 * ```
 */
class PasswordValidator private constructor(private val rules: List<PasswordRule>) {
    
    /**
     * Validates a password against all configured rules.
     * 
     * @param password the password to validate
     * @return ValidationResult containing success status and any errors
     */
    fun validate(password: String): ValidationResult {
        val errors = mutableListOf<PasswordError>()
        
        rules.forEach { rule ->
            rule.validate(password).onFailure { throwable ->
                if (throwable is PasswordError) {
                    errors.add(throwable)
                }
            }
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.success()
        } else {
            ValidationResult.failure(errors)
        }
    }
    
    /**
     * Builder class for creating custom password validators.
     * 
     * Provides a fluent API for combining validation rules.
     */
    class Builder {
        private val rules = mutableListOf<PasswordRule>()
        
        /**
         * Adds minimum length requirement.
         * @param length minimum number of characters
         */
        fun minLength(length: Int) = apply {
            rules.add(MinLengthRule(length))
        }
        
        /**
         * Requires at least one uppercase letter.
         */
        fun requireUppercase() = apply {
            rules.add(UppercaseRule())
        }
        
        /**
         * Requires at least one numeric digit.
         */
        fun requireDigit() = apply {
            rules.add(DigitRule())
        }
        
        /**
         * Requires at least one special character.
         * @param specialChars optional custom set of allowed special characters
         */
        fun requireSpecialCharacter(specialChars: String? = null) = apply {
            if (specialChars != null) {
                rules.add(SpecialCharacterRule(specialChars))
            } else {
                rules.add(SpecialCharacterRule())
            }
        }
        
        /**
         * Adds a custom validation rule.
         * @param rule implementation of PasswordRule interface
         */
        fun addRule(rule: PasswordRule) = apply {
            rules.add(rule)
        }
        
        /**
         * Builds the validator with all configured rules.
         * @return configured PasswordValidator instance
         */
        fun build(): PasswordValidator {
            return PasswordValidator(rules.toList())
        }
    }
    
    companion object {
        /**
         * Creates a new Builder instance.
         * @return Builder for configuring validation rules
         */
        fun builder() = Builder()
        
        /**
         * Creates a validator with sensible default rules:
         * - Minimum 8 characters
         * - At least one uppercase letter
         * - At least one digit
         * - At least one special character
         * 
         * @return PasswordValidator with default rules
         */
        fun defaultRules(): PasswordValidator {
            return builder()
                .minLength(8)
                .requireUppercase()
                .requireDigit()
                .requireSpecialCharacter()
                .build()
        }
    }
}