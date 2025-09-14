package com.pisces.xcodebn.passwordvalidator

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for MinLengthRule
 * @author xcodeBn@github
 */
class MinLengthRuleTest {
    
    @Test
    fun `validate should succeed when password meets minimum length`() {
        val rule = MinLengthRule(8)
        val result = rule.validate("password123")
        
        assertTrue("Password with 11 characters should pass 8-char minimum", result.isSuccess)
    }
    
    @Test
    fun `validate should succeed when password exactly meets minimum length`() {
        val rule = MinLengthRule(8)
        val result = rule.validate("12345678")
        
        assertTrue("Password with exactly 8 characters should pass 8-char minimum", result.isSuccess)
    }
    
    @Test
    fun `validate should fail when password is shorter than minimum length`() {
        val rule = MinLengthRule(8)
        val result = rule.validate("1234567")
        
        assertTrue("Password with 7 characters should fail 8-char minimum", result.isFailure)
        assertTrue("Should return TooShort error", result.exceptionOrNull() is PasswordError.TooShort)
    }
    
    @Test
    fun `validate should fail when password is empty`() {
        val rule = MinLengthRule(8)
        val result = rule.validate("")
        
        assertTrue("Empty password should fail", result.isFailure)
        assertTrue("Should return TooShort error", result.exceptionOrNull() is PasswordError.TooShort)
    }
    
    @Test
    fun `validate should work with custom minimum length`() {
        val rule = MinLengthRule(12)
        
        val shortResult = rule.validate("password123")
        assertTrue("11-char password should fail 12-char minimum", shortResult.isFailure)
        
        val longResult = rule.validate("password1234")
        assertTrue("12-char password should pass 12-char minimum", longResult.isSuccess)
    }
    
    @Test
    fun `validate should handle single character passwords`() {
        val rule = MinLengthRule(1)
        
        val result = rule.validate("a")
        assertTrue("Single character should pass 1-char minimum", result.isSuccess)
    }
    
    @Test
    fun `default minimum length should be 8`() {
        val rule = MinLengthRule()
        
        val shortResult = rule.validate("1234567")
        assertTrue("7-char password should fail default minimum", shortResult.isFailure)
        
        val validResult = rule.validate("12345678")
        assertTrue("8-char password should pass default minimum", validResult.isSuccess)
    }
}