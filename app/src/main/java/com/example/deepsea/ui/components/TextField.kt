package com.example.deepsea.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.deepsea.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeHolder: String = "",
    label: String? = null,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier,
    padding: Dp = 0.dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    // State để bật/tắt hiển thị mật khẩu
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label?.let { { Text(it) } },
        placeholder = { Text(placeHolder) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        keyboardOptions = if (isPassword)
            KeyboardOptions(keyboardType = KeyboardType.Password)
        else
            KeyboardOptions.Default,
        trailingIcon = {
            if (isPassword) {
                val icon = if (passwordVisible)
                    painterResource(R.drawable.visibility_off_eye)
                else
                    painterResource(R.drawable.visibility_eye)
                val desc = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = icon,           // 👈 dùng `painter`
                        contentDescription = desc
                    )
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = color,
            unfocusedIndicatorColor = color,
            cursorColor = color
        ),
        modifier = modifier
            .padding(padding)
            .fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeHolder: String = "",
    label: String? = null,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier,
    padding: Dp = 0.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    errorMessage: String? = null
) {
    androidx.compose.foundation.layout.Column {
        OutlinedTextField(
            value = value,
            shape = RoundedCornerShape(16.dp),
            onValueChange = onValueChange,
            placeholder = { Text(placeHolder) },
            label = label?.let { { Text(it) } },
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password)
            else KeyboardOptions.Default,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = if (errorMessage != null) MaterialTheme.colorScheme.error else color,
                unfocusedIndicatorColor = if (errorMessage != null) MaterialTheme.colorScheme.error else color,
                errorIndicatorColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error,
                errorCursorColor = MaterialTheme.colorScheme.error
            ),
            isError = errorMessage != null,
            modifier = modifier
                .padding(padding)
                .fillMaxWidth()
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
