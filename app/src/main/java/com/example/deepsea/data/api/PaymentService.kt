package com.example.deepsea.data.api

import com.example.deepsea.data.model.payment.ConfirmPaymentRequest
import com.example.deepsea.data.model.payment.ConfirmPaymentResponse
import com.example.deepsea.data.model.payment.PaymentIntentRequest
import com.example.deepsea.data.model.payment.PaymentIntentResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentService {
    @POST("api/payments/intent")
    suspend fun createPaymentIntent(@Body request: PaymentIntentRequest): PaymentIntentResponse

    @POST("api/payments/confirm")
    suspend fun confirmPayment(@Body request: ConfirmPaymentRequest): ConfirmPaymentResponse
}
