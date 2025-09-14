package com.pisces.xcodebn.demo.validatordemo

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.pisces.xcodebn.passwordvalidator.PasswordValidator
import com.pisces.xcodebn.passwordvalidator.ValidationResult

/**
 * ViewModel for password validation
 * @author xcodeBn@github
 */
class PasswordValidationViewModel(
    private val validator: PasswordValidator = PasswordValidator.defaultRules()
) : ViewModel() {
    
    private val _password = mutableStateOf("")
    val password: State<String> = _password
    
    private val _validationResult = mutableStateOf<ValidationResult?>(null)
    val validationResult: State<ValidationResult?> = _validationResult
    
    private val _isValidating = mutableStateOf(false)
    val isValidating: State<Boolean> = _isValidating
    
    fun updatePassword(newPassword: String) {
        _password.value = newPassword
        validatePassword()
    }
    
    private fun validatePassword() {
        _isValidating.value = true
        val result = validator.validate(_password.value)
        _validationResult.value = result
        _isValidating.value = false
    }
    
    fun clearValidation() {
        _validationResult.value = null
    }
}