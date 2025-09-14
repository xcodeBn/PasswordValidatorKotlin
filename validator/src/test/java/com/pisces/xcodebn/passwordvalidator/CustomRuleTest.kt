package com.pisces.xcodebn.passwordvalidator

import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for custom rules with custom error messages
 * @author xcodeBn@github
 */
class CustomRuleTest {
    
    class NoConsecutiveCharsRule : PasswordRule {
        override fun validate(password: String): Result<Unit> {
            for (i in 0 until password.length - 1) {
                if (password[i] == password[i + 1]) {
                    return Result.failure(PasswordError.Custom("Password must not contain consecutive identical characters"))
                }
            }
            return Result.success(Unit)
        }
    }
    
    class MustContainWordRule(private val requiredWord: String) : PasswordRule {
        override fun validate(password: String): Result<Unit> {
            return if (password.contains(requiredWord, ignoreCase = true)) {
                Result.success(Unit)
            } else {
                Result.failure(PasswordError.Custom("Password must contain the word '$requiredWord'"))
            }
        }
    }
    
    class MaxLengthRule(private val maxLength: Int) : PasswordRule {
        override fun validate(password: String): Result<Unit> {
            return if (password.length <= maxLength) {
                Result.success(Unit)
            } else {
                Result.failure(PasswordError.Custom("Password must not exceed $maxLength characters"))
            }
        }
    }
    
    @Test
    fun `custom rule should succeed when validation passes`() {
        val rule = NoConsecutiveCharsRule()
        val result = rule.validate("Password123!")
        
        assertTrue("Password without consecutive chars should pass", result.isSuccess)
    }
    
    @Test
    fun `custom rule should fail with custom error message`() {
        val rule = NoConsecutiveCharsRule()
        val result = rule.validate("Passwword123!")
        
        assertTrue("Password with consecutive chars should fail", result.isFailure)
        
        val error = result.exceptionOrNull()
        assertTrue("Should return Custom error", error is PasswordError.Custom)
        assertEquals(
            "Should have custom error message",
            "Password must not contain consecutive identical characters",
            error?.message
        )
    }
    
    @Test
    fun `custom rule with parameters should work correctly`() {
        val rule = MustContainWordRule("secure")
        
        val validResult = rule.validate("MySecurePassword123!")
        assertTrue("Password containing required word should pass", validResult.isSuccess)
        
        val invalidResult = rule.validate("MyPassword123!")
        assertTrue("Password missing required word should fail", invalidResult.isFailure)
        
        val error = invalidResult.exceptionOrNull()
        assertTrue("Should return Custom error", error is PasswordError.Custom)
        assertEquals(
            "Should have custom error message with parameter",
            "Password must contain the word 'secure'",
            error?.message
        )
    }
    
    @Test
    fun `custom rule can be integrated with validator`() {
        val validator = PasswordValidator.builder()
            .minLength(8)
            .requireUppercase()
            .addRule(NoConsecutiveCharsRule())
            .addRule(MustContainWordRule("safe"))
            .build()
        
        val validResult = validator.validate("MySafePassword123!")
        assertTrue("Password meeting all rules should pass", validResult.isValid)
        
        val invalidResult = validator.validate("MyPassword")
        assertFalse("Password failing multiple rules should fail", invalidResult.isValid)
        
        val errors = invalidResult.errors
        assertTrue("Should contain MissingDigit error", errors.any { it is PasswordError.MissingDigit })
        assertTrue("Should contain Custom error for missing word", 
                   errors.any { it is PasswordError.Custom && it.message.contains("safe") })
    }
    
    @Test
    fun `multiple custom rules can have different error messages`() {
        val validator = PasswordValidator.builder()
            .addRule(MaxLengthRule(20))
            .addRule(MustContainWordRule("admin"))
            .build()
        
        val result = validator.validate("ThisIsAVeryLongPasswordThatExceedsTheMaximumLengthLimit")
        assertFalse("Password violating multiple custom rules should fail", result.isValid)
        
        val customErrors = result.errors.filterIsInstance<PasswordError.Custom>()
        assertEquals("Should have 2 custom errors", 2, customErrors.size)
        
        val messages = customErrors.map { it.message }
        assertTrue("Should contain max length error", messages.any { it.contains("exceed") })
        assertTrue("Should contain missing word error", messages.any { it.contains("admin") })
    }
    
    @Test
    fun `custom error description should match message`() {
        val customMessage = "Password must not start with a number"
        val error = PasswordError.Custom(customMessage)
        
        assertEquals("Description should equal message", customMessage, error.description)
        assertEquals("Message should equal provided string", customMessage, error.message)
    }
    
    @Test
    fun `custom rule can implement complex business logic`() {
        class BusinessRule : PasswordRule {
            override fun validate(password: String): Result<Unit> {
                val hasUppercase = password.any { it.isUpperCase() }
                val hasLowercase = password.any { it.isLowerCase() }
                val hasDigit = password.any { it.isDigit() }
                val hasSpecial = password.any { !it.isLetterOrDigit() }
                val isLongEnough = password.length >= 12
                
                return when {
                    !isLongEnough -> Result.failure(PasswordError.Custom("Business rule: minimum 12 characters required"))
                    !hasUppercase -> Result.failure(PasswordError.Custom("Business rule: uppercase letter required"))
                    !hasLowercase -> Result.failure(PasswordError.Custom("Business rule: lowercase letter required"))
                    !hasDigit -> Result.failure(PasswordError.Custom("Business rule: digit required"))
                    !hasSpecial -> Result.failure(PasswordError.Custom("Business rule: special character required"))
                    password.contains("password", ignoreCase = true) -> 
                        Result.failure(PasswordError.Custom("Business rule: password cannot contain the word 'password'"))
                    else -> Result.success(Unit)
                }
            }
        }
        
        val validator = PasswordValidator.builder()
            .addRule(BusinessRule())
            .build()
        
        val invalidResult = validator.validate("password123")
        assertFalse("Password violating business rule should fail", invalidResult.isValid)
        
        val error = invalidResult.errors.first()
        assertTrue("Should be custom error", error is PasswordError.Custom)
        assertTrue("Should mention business rule", error.description.startsWith("Business rule:"))
    }
}