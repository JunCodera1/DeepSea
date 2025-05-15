package com.example.deepsea.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.stripe.android.PaymentConfiguration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeepSeaApp()
            PaymentConfiguration.init(this, "pk_test_51RP1LdGfeGdan58I89htrGL1BbnpTRuwgyPpvKYoZbN6H85nngFGlPk283VqC1qChq2bwFQEwDAtykjvTdALj2T8007OrOC7fl")
        }
    }
}
