package com.pisces.xcodebn.passwordvalidator

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for DigitRule
 * @author xcodeBn@github
 */
class DigitRuleTest {
    
    private val rule = DigitRule()
    
    @Test
    fun `validate should succeed when password contains single digit`() {
        val result = rule.validate("Password1")
        
        assertTrue("Password with single digit should pass", result.isSuccess)
    }
    
    @Test
    fun `validate should succeed when password contains multiple digits`() {
        val result = rule.validate("Password123")
        
        assertTrue("Password with multiple digits should pass", result.isSuccess)
    }
    
    @Test
    fun `validate should succeed with digit at the beginning`() {
        val result = rule.validate("1Password")
        
        assertTrue("Password with digit at beginning should pass", result.isSuccess)
    }
    
    @Test
    fun `validate should succeed with digit in the middle`() {
        val result = rule.validate("Pass1word")
        
        assertTrue("Password with digit in middle should pass", result.isSuccess)
    }
    
    @Test
    fun `validate should succeed with digit at the end`() {
        val result = rule.validate("Password1")
        
        assertTrue("Password with digit at end should pass", result.isSuccess)
    }
    
    @Test
    fun `validate should succeed with only digits`() {
        val result = rule.validate("123456")
        
        assertTrue("Password with only digits should pass", result.isSuccess)
    }
    
    @Test
    fun `validate should fail when password has no digits`() {
        val result = rule.validate("Password")
        
        assertTrue("Password with no digits should fail", result.isFailure)
        assertTrue("Should return MissingDigit error", result.exceptionOrNull() is PasswordError.MissingDigit)
    }
    
    @Test
    fun `validate should fail with only lowercase letters`() {
        val result = rule.validate("password")
        
        assertTrue("Password with only lowercase should fail", result.isFailure)
        assertTrue("Should return MissingDigit error", result.exceptionOrNull() is PasswordError.MissingDigit)
    }
    
    @Test
    fun `validate should fail with only uppercase letters`() {
        val result = rule.validate("PASSWORD")
        
        assertTrue("Password with only uppercase should fail", result.isFailure)
        assertTrue("Should return MissingDigit error", result.exceptionOrNull() is PasswordError.MissingDigit)
    }
    
    @Test
    fun `validate should fail with only special characters`() {
        val result = rule.validate("!@#$%^&*()")
        
        assertTrue("Password with only special characters should fail", result.isFailure)
        assertTrue("Should return MissingDigit error", result.exceptionOrNull() is PasswordError.MissingDigit)
    }
    
    @Test
    fun `validate should fail with empty password`() {
        val result = rule.validate("")
        
        assertTrue("Empty password should fail", result.isFailure)
        assertTrue("Should return MissingDigit error", result.exceptionOrNull() is PasswordError.MissingDigit)
    }
    
    @Test
    fun `validate should succeed with all digits 0-9`() {
        val result = rule.validate("0123456789")
        
        assertTrue("Password with all digits should pass", result.isSuccess)
    }
    
    @Test
    fun `validate should succeed with mixed content including digit`() {
        val result = rule.validate("Aa1!@#")
        
        assertTrue("Mixed password with digit should pass", result.isSuccess)
    }
}