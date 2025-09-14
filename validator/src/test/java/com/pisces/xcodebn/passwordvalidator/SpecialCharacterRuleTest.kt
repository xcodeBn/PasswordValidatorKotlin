package com.pisces.xcodebn.passwordvalidator

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for SpecialCharacterRule
 * @author xcodeBn@github
 */
class SpecialCharacterRuleTest {
    
    @Test
    fun `validate should succeed when password contains default special character`() {
        val rule = SpecialCharacterRule()
        val result = rule.validate("Password1!")
        
        assertTrue("Password with exclamation mark should pass", result.isSuccess)
    }
    
    @Test
    fun `validate should succeed with multiple special characters`() {
        val rule = SpecialCharacterRule()
        val result = rule.validate("Password1!@#")
        
        assertTrue("Password with multiple special characters should pass", result.isSuccess)
    }
    
    @Test
    fun `validate should succeed with all default special characters`() {
        val rule = SpecialCharacterRule()
        val specialChars = "!@#$%^&*(),.?\":{}|<>-_+=[]\\;'`~"
        
        specialChars.forEach { char ->
            val result = rule.validate("Password1$char")
            assertTrue("Password with '$char' should pass", result.isSuccess)
        }
    }
    
    @Test
    fun `validate should fail when password has no special characters`() {
        val rule = SpecialCharacterRule()
        val result = rule.validate("Password123")
        
        assertTrue("Password with no special characters should fail", result.isFailure)
        assertTrue("Should return MissingSpecialChar error", result.exceptionOrNull() is PasswordError.MissingSpecialChar)
    }
    
    @Test
    fun `validate should fail with only letters and numbers`() {
        val rule = SpecialCharacterRule()
        val result = rule.validate("Password123ABC")
        
        assertTrue("Password with only alphanumeric should fail", result.isFailure)
        assertTrue("Should return MissingSpecialChar error", result.exceptionOrNull() is PasswordError.MissingSpecialChar)
    }
    
    @Test
    fun `validate should fail with empty password`() {
        val rule = SpecialCharacterRule()
        val result = rule.validate("")
        
        assertTrue("Empty password should fail", result.isFailure)
        assertTrue("Should return MissingSpecialChar error", result.exceptionOrNull() is PasswordError.MissingSpecialChar)
    }
    
    @Test
    fun `validate should work with custom special characters set`() {
        val rule = SpecialCharacterRule("!@#")
        
        val validResult = rule.validate("Password1!")
        assertTrue("Password with custom special character should pass", validResult.isSuccess)
        
        val invalidResult = rule.validate("Password1$")
        assertTrue("Password with non-custom special character should fail", invalidResult.isFailure)
    }
    
    @Test
    fun `validate should succeed with custom single special character`() {
        val rule = SpecialCharacterRule("*")
        val result = rule.validate("Password1*")
        
        assertTrue("Password with single custom special character should pass", result.isSuccess)
    }
    
    @Test
    fun `validate should fail when using character not in custom set`() {
        val rule = SpecialCharacterRule("!@#")
        val result = rule.validate("Password1%")
        
        assertTrue("Password with character not in custom set should fail", result.isFailure)
        assertTrue("Should return MissingSpecialChar error", result.exceptionOrNull() is PasswordError.MissingSpecialChar)
    }
    
    @Test
    fun `validate should succeed with special character at beginning`() {
        val rule = SpecialCharacterRule()
        val result = rule.validate("!Password123")
        
        assertTrue("Password with special character at beginning should pass", result.isSuccess)
    }
    
    @Test
    fun `validate should succeed with special character in middle`() {
        val rule = SpecialCharacterRule()
        val result = rule.validate("Pass!word123")
        
        assertTrue("Password with special character in middle should pass", result.isSuccess)
    }
    
    @Test
    fun `validate should succeed with only special characters`() {
        val rule = SpecialCharacterRule()
        val result = rule.validate("!@#$%^&*()")
        
        assertTrue("Password with only special characters should pass", result.isSuccess)
    }
    
    @Test
    fun `default special characters should include common symbols`() {
        val rule = SpecialCharacterRule()
        val commonSymbols = listOf("!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_", "+", "=")
        
        commonSymbols.forEach { symbol ->
            val result = rule.validate("Password1$symbol")
            assertTrue("Password with common symbol '$symbol' should pass", result.isSuccess)
        }
    }
}