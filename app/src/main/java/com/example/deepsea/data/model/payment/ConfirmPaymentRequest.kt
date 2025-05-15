package com.example.deepsea.data.model.payment

data class ConfirmPaymentRequest(
    val userId: Long,
    val paymentIntentId: String
)