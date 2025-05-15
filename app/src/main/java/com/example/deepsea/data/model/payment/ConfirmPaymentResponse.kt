package com.example.deepsea.data.model.payment

data class ConfirmPaymentResponse(
    val success: Boolean,
    val subscriptionId: String?
)