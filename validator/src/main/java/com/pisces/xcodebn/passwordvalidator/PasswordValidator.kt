package com.pisces.xcodebn.passwordvalidator

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

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<PasswordError> = emptyList()
) {
    companion object {
        fun success() = ValidationResult(true)
        fun failure(errors: List<PasswordError>) = ValidationResult(false, errors)
    }
}

interface PasswordRule {
    fun validate(password: String): Result<Unit>
}

class MinLengthRule(private val minLength: Int = 8) : PasswordRule {
    override fun validate(password: String): Result<Unit> {
        return if (password.length >= minLength) {
            Result.success(Unit)
        } else {
            Result.failure(PasswordError.TooShort)
        }
    }
}

class UppercaseRule : PasswordRule {
    override fun validate(password: String): Result<Unit> {
        return if (password.any { it.isUpperCase() }) {
            Result.success(Unit)
        } else {
            Result.failure(PasswordError.MissingUppercase)
        }
    }
}

class DigitRule : PasswordRule {
    override fun validate(password: String): Result<Unit> {
        return if (password.any { it.isDigit() }) {
            Result.success(Unit)
        } else {
            Result.failure(PasswordError.MissingDigit)
        }
    }
}

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

class PasswordValidator private constructor(private val rules: List<PasswordRule>) {
    
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
    
    class Builder {
        private val rules = mutableListOf<PasswordRule>()
        
        fun minLength(length: Int) = apply {
            rules.add(MinLengthRule(length))
        }
        
        fun requireUppercase() = apply {
            rules.add(UppercaseRule())
        }
        
        fun requireDigit() = apply {
            rules.add(DigitRule())
        }
        
        fun requireSpecialCharacter(specialChars: String? = null) = apply {
            if (specialChars != null) {
                rules.add(SpecialCharacterRule(specialChars))
            } else {
                rules.add(SpecialCharacterRule())
            }
        }
        
        fun addRule(rule: PasswordRule) = apply {
            rules.add(rule)
        }
        
        fun build(): PasswordValidator {
            return PasswordValidator(rules.toList())
        }
    }
    
    companion object {
        fun builder() = Builder()
        
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