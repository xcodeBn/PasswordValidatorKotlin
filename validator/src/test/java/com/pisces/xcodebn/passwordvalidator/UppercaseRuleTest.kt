package com.pisces.xcodebn.passwordvalidator

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for UppercaseRule
 * @author xcodeBn@github
 */
class UppercaseRuleTest {
    
    private val rule = UppercaseRule()
    
    @Test
    fun `validate should succeed when password contains uppercase letter`() {
        val result = rule.validate("Password123")
        
        assertTrue("Password with uppercase 'P' should pass", result.isSuccess)
    }
    
    @Test
    fun `validate should succeed when password contains multiple uppercase letters`() {
        val result = rule.validate("PASSWORD123")
        
        assertTrue("Password with multiple uppercase letters should pass", result.isSuccess)
    }
    
    @Test
    fun `validate should succeed with uppercase letter at the end`() {
        val result = rule.validate("password123A")
        
        assertTrue("Password with uppercase at end should pass", result.isSuccess)
    }
    
    @Test
    fun `validate should succeed with uppercase letter in the middle`() {
        val result = rule.validate("passWord123")
        
        assertTrue("Password with uppercase in middle should pass", result.isSuccess)
    }
    
    @Test
    fun `validate should fail when password has no uppercase letters`() {
        val result = rule.validate("password123")
        
        assertTrue("Password with no uppercase should fail", result.isFailure)
        assertTrue("Should return MissingUppercase error", result.exceptionOrNull() is PasswordError.MissingUppercase)
    }
    
    @Test
    fun `validate should fail with only lowercase and numbers`() {
        val result = rule.validate("abc123def456")
        
        assertTrue("Password with only lowercase and numbers should fail", result.isFailure)
        assertTrue("Should return MissingUppercase error", result.exceptionOrNull() is PasswordError.MissingUppercase)
    }
    
    @Test
    fun `validate should fail with only special characters`() {
        val result = rule.validate("!@#$%^&*()")
        
        assertTrue("Password with only special characters should fail", result.isFailure)
        assertTrue("Should return MissingUppercase error", result.exceptionOrNull() is PasswordError.MissingUppercase)
    }
    
    @Test
    fun `validate should fail with empty password`() {
        val result = rule.validate("")
        
        assertTrue("Empty password should fail", result.isFailure)
        assertTrue("Should return MissingUppercase error", result.exceptionOrNull() is PasswordError.MissingUppercase)
    }
    
    @Test
    fun `validate should succeed with single uppercase character`() {
        val result = rule.validate("A")
        
        assertTrue("Single uppercase character should pass", result.isSuccess)
    }
    
    @Test
    fun `validate should handle unicode uppercase characters`() {
        val result = rule.validate("passwordÄ123")
        
        assertTrue("Password with unicode uppercase should pass", result.isSuccess)
    }
}