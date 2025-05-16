package com.example.deepsea.ui.screens.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.payment.ConfirmPaymentRequest
import com.example.deepsea.data.model.payment.PaymentIntentRequest
import com.example.deepsea.utils.SessionManager
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    sessionManager: SessionManager,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showQrPayment by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val userId by sessionManager.userId.collectAsState(initial = null)
    var clientSecret by remember { mutableStateOf<String?>(null) }
    var paymentStatus by remember { mutableStateOf<String?>(null) }
    val paymentService = RetrofitClient.paymentService

    // Define the payment result callback
    val paymentResultCallback: (PaymentSheetResult) -> Unit = { result ->
        when (result) {
            is PaymentSheetResult.Completed -> {
                scope.launch {
                    userId?.let { uid ->
                        try {
                            clientSecret?.let { secret ->
                                val response = paymentService.confirmPayment(
                                    ConfirmPaymentRequest(
                                        userId = uid,
                                        paymentIntentId = secret.split("_secret_")[0]
                                    )
                                )
                                if (response.success) {
                                    sessionManager.setPremiumStatus(true)
                                    paymentStatus = "Payment successful!"
                                } else {
                                    paymentStatus = "Payment confirmation failed"
                                }
                            }
                        } catch (e: Exception) {
                            paymentStatus = "Payment confirmation failed: ${e.message}"
                        }
                    }
                }
            }
            is PaymentSheetResult.Canceled -> {
                paymentStatus = "Payment canceled"
            }
            is PaymentSheetResult.Failed -> {
                paymentStatus = "Payment failed: ${result.error.message}"
            }
        }
    }

    // Initialize PaymentSheet with paymentResultCallback
    val paymentSheet = rememberPaymentSheet(paymentResultCallback = paymentResultCallback)

    // Fetch PaymentIntent client secret
    LaunchedEffect(userId) {
        userId?.let { uid ->
            try {
                val response = paymentService.createPaymentIntent(
                    PaymentIntentRequest(
                        userId = uid,
                        amount = 999, // $9.99 in cents
                        currency = "usd"
                    )
                )
                clientSecret = response.clientSecret
            } catch (e: Exception) {
                paymentStatus = "Failed to initialize payment: ${e.message}"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Go Premium",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "DeepSea Premium",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$9.99/month",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Unlock all features:\n- Unlimited story access\n- Ad-free experience\n- Exclusive courses",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Button(
                        onClick = {
                            clientSecret?.let { secret ->
                                paymentSheet.presentWithPaymentIntent(
                                    paymentIntentClientSecret = secret,
                                    configuration = PaymentSheet.Configuration(
                                        merchantDisplayName = "DeepSea",
                                        allowsDelayedPaymentMethods = true
                                    )
                                )
                            } ?: run {
                                paymentStatus = "Payment not initialized"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Subscribe Now")
                    }
                    paymentStatus?.let {
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            color = if (it.contains("successful", true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }


                paymentStatus?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = if (it.contains("failed", ignoreCase = true)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "DeepSea Donate",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "money is not important, pay as much as you like",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "- Nothing \uD83E\uDD70",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Button(
                        onClick = { showQrPayment = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Pay with QR Code")
                    }
                    if (showQrPayment) {
                        Spacer(modifier = Modifier.height(16.dp))

                        AsyncImage(
                            model = "https://scontent.fhan2-3.fna.fbcdn.net/v/t1.15752-9/494823127_1251834223007976_320845584253777640_n.png?_nc_cat=108&ccb=1-7&_nc_sid=9f807c&_nc_ohc=HZ-xBfARwLUQ7kNvwGrEtmQ&_nc_oc=AdmsPW72p6DguMeo2kMI1nr2fb4PKqbNNAgRk8l3JhYlwDM2gZw_RyjBKY6sF22ltA8&_nc_zt=23&_nc_ht=scontent.fhan2-3.fna&oh=03_Q7cD2QHk5tuKt1LtNPgUbV7upSzL_oktRvfht3PDk_oMnmif2w&oe=684D51DE", // Thay URL thật ở đây
                            contentDescription = "QR Code",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                        )
                    }
                }
            }
        }
    }
}