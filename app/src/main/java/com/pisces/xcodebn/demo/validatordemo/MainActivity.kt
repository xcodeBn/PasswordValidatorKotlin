package com.pisces.xcodebn.demo.validatordemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.pisces.xcodebn.demo.validatordemo.ui.theme.ValidatorDemoTheme

/**
 * Main activity showcasing the modular password validator
 * @author xcodeBn@github
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ValidatorDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PasswordValidationScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}