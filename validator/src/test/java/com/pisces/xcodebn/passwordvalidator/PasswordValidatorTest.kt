package com.pisces.xcodebn.passwordvalidator

import org.junit.Test
import org.junit.Assert.*

/**
 * Integration tests for PasswordValidator
 * @author xcodeBn@github
 */
class PasswordValidatorTest {
    
    @Test
    fun `default validator should accept valid password`() {
        val validator = PasswordValidator.defaultRules()
        val result = validator.validate("Password123!")
        
        assertTrue("Valid password should pass all default rules", result.isValid)
        assertTrue("Should have no errors", result.errors.isEmpty())
    }
    
    @Test
    fun `default validator should reject password failing all rules`() {
        val validator = PasswordValidator.defaultRules()
        val result = validator.validate("pass")
        
        assertFalse("Invalid password should fail", result.isValid)
        assertEquals("Should have 4 errors", 4, result.errors.size)
        assertTrue("Should contain TooShort error", result.errors.any { it is PasswordError.TooShort })
        assertTrue("Should contain MissingUppercase error", result.errors.any { it is PasswordError.MissingUppercase })
        assertTrue("Should contain MissingDigit error", result.errors.any { it is PasswordError.MissingDigit })
        assertTrue("Should contain MissingSpecialChar error", result.errors.any { it is PasswordError.MissingSpecialChar })
    }
    
    @Test
    fun `validator should collect multiple errors`() {
        val validator = PasswordValidator.defaultRules()
        val result = validator.validate("password")
        
        assertFalse("Password missing multiple requirements should fail", result.isValid)
        assertEquals("Should have 3 errors", 3, result.errors.size)
        assertTrue("Should contain MissingUppercase error", result.errors.any { it is PasswordError.MissingUppercase })
        assertTrue("Should contain MissingDigit error", result.errors.any { it is PasswordError.MissingDigit })
        assertTrue("Should contain MissingSpecialChar error", result.errors.any { it is PasswordError.MissingSpecialChar })
    }
    
    @Test
    fun `builder pattern should work with custom minimum length`() {
        val validator = PasswordValidator.builder()
            .minLength(12)
            .build()
        
        val shortResult = validator.validate("Password1!")
        assertFalse("10-character password should fail 12-char minimum", shortResult.isValid)
        
        val longResult = validator.validate("LongPassword1!")
        assertTrue("14-character password should pass 12-char minimum", longResult.isValid)
    }
    
    @Test
    fun `builder pattern should work with selective rules`() {
        val validator = PasswordValidator.builder()
            .minLength(6)
            .requireUppercase()
            .build()
        
        val validResult = validator.validate("Password")
        assertTrue("Password meeting selected rules should pass", validResult.isValid)
        
        val invalidResult = validator.validate("password")
        assertFalse("Password missing uppercase should fail", invalidResult.isValid)
        assertEquals("Should have 1 error", 1, invalidResult.errors.size)
        assertTrue("Should contain MissingUppercase error", invalidResult.errors.any { it is PasswordError.MissingUppercase })
    }
    
    @Test
    fun `builder pattern should work with custom special characters`() {
        val validator = PasswordValidator.builder()
            .minLength(8)
            .requireSpecialCharacter("!@#")
            .build()
        
        val validResult = validator.validate("Password123!")
        assertTrue("Password with allowed special char should pass", validResult.isValid)
        
        val invalidResult = validator.validate("Password123$")
        assertFalse("Password with non-allowed special char should fail", invalidResult.isValid)
    }
    
    @Test
    fun `builder pattern should allow combining all rules`() {
        val validator = PasswordValidator.builder()
            .minLength(10)
            .requireUppercase()
            .requireDigit()
            .requireSpecialCharacter("!@#$")
            .build()
        
        val validResult = validator.validate("MyPassword123!")
        assertTrue("Password meeting all custom rules should pass", validResult.isValid)
        
        val invalidResult = validator.validate("mypass")
        assertFalse("Password failing all custom rules should fail", invalidResult.isValid)
        assertEquals("Should have 4 errors", 4, invalidResult.errors.size)
    }
    
    @Test
    fun `validator with no rules should accept any password`() {
        val validator = PasswordValidator.builder().build()
        
        val result = validator.validate("")
        assertTrue("Empty password should pass when no rules are set", result.isValid)
        assertTrue("Should have no errors", result.errors.isEmpty())
    }
    
    @Test
    fun `validator should handle edge case passwords`() {
        val validator = PasswordValidator.defaultRules()
        
        val exactLengthResult = validator.validate("Abcd123!")
        assertTrue("8-character password meeting all rules should pass", exactLengthResult.isValid)
        
        val unicodeResult = validator.validate("Päßwörd123!")
        assertTrue("Password with unicode characters should pass", unicodeResult.isValid)
        
        val allSpecialResult = validator.validate("!@#$%^&*A1")
        assertTrue("Password with mostly special chars should pass", allSpecialResult.isValid)
    }
    
    @Test
    fun `custom rule can be added via builder`() {
        class CustomRule : PasswordRule {
            override fun validate(password: String): Result<Unit> {
                return if (password.contains("custom")) {
                    Result.success(Unit)
                } else {
                    Result.failure(PasswordError.Custom("Password must contain the word 'custom'"))
                }
            }
        }
        
        val validator = PasswordValidator.builder()
            .addRule(CustomRule())
            .build()
        
        val validResult = validator.validate("customPassword")
        assertTrue("Password with custom rule requirement should pass", validResult.isValid)
        
        val invalidResult = validator.validate("regularPassword")
        assertFalse("Password without custom rule requirement should fail", invalidResult.isValid)
        
        val error = invalidResult.errors.first()
        assertTrue("Should be custom error", error is PasswordError.Custom)
        assertEquals("Should have custom error message", "Password must contain the word 'custom'", error.message)
    }
    
    @Test
    fun `validation result should provide meaningful error descriptions`() {
        val validator = PasswordValidator.defaultRules()
        val result = validator.validate("pass")
        
        result.errors.forEach { error ->
            assertNotNull("Error description should not be null", error.description)
            assertTrue("Error description should not be empty", error.description.isNotEmpty())
        }
    }
}