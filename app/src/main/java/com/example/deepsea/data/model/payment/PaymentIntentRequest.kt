package com.example.deepsea.data.model.payment

data class PaymentIntentRequest(
    val userId: Long,
    val amount: Int, // In cents (e.g., 999 for $9.99)
    val currency: String = "usd"
)