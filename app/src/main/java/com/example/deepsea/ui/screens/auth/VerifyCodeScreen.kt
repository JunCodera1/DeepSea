package com.example.deepsea.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun VerifyCodeScreen(
    email: String,
    verificationCode: List<String>,
    onCodeChange: (Int, String) -> Unit,
    onVerifyClicked: () -> Unit,
    onResendClicked: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    // Create focus requesters for each input field
    val focusRequesters = List(5) { FocusRequester() }

    // Create focus manager to handle focus changes
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Check your email",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            text = "We sent a reset link to $email\nenter 5 digit code that mentioned in the email",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 0 until 5) {
                OutlinedTextField(
                    value = verificationCode[i],
                    onValueChange = { value ->
                        if (value.length <= 1 && value.all { it.isDigit() }) {
                            onCodeChange(i, value)

                            // Auto-advance to next field if current field is filled
                            if (value.isNotEmpty() && i < 4) {
                                focusRequesters[i + 1].requestFocus()
                            }
                        }
                    },
                    modifier = Modifier
                        .width(56.dp)
                        .height(56.dp)
                        .focusRequester(focusRequesters[i])
                        .onKeyEvent { keyEvent ->
                            // Handle backspace to go to previous field
                            if (keyEvent.key == Key.Backspace && verificationCode[i].isEmpty() && i > 0) {
                                focusRequesters[i - 1].requestFocus()
                                return@onKeyEvent true
                            }
                            false
                        },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center
                    )
                )
            }
        }

        // Handle pasted verification code
        DisposableEffect(Unit) {
            onDispose {
                // Clean up if needed
            }
        }

        Button(
            onClick = onVerifyClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (verificationCode.any { it.isNotBlank() })
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        ) {
            Text("Verify Code")
        }

        TextButton(
            onClick = onResendClicked,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        ) {
            Text("Haven't got the email yet? Resend email")
        }

        // Additional feature: allow pasting the entire code
        TextButton(
            onClick = {
                clipboardManager.getText()?.text?.let { clipboardText ->
                    val digits = clipboardText.filter { it.isDigit() }.take(5)
                    if (digits.isNotEmpty()) {
                        digits.forEachIndexed { index, c ->
                            if (index < 5) {
                                onCodeChange(index, c.toString())
                            }
                        }
                        // Focus on the field after the last entered digit or the last field
                        val nextFocusIndex = minOf(digits.length, 4)
                        focusRequesters[nextFocusIndex].requestFocus()
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp)
        ) {
            Text("Paste code from clipboard")
        }
    }

    // Request focus on the first field when the screen is shown
    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }
}