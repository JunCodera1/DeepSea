package com.example.deepsea.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.deepsea.utils.UserState

@Composable
fun ProfilePage(onLogout: () -> Unit, userState: UserState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to profile page")
    }
}


@Preview
@Composable
fun ProfileScreenPreview(){
//    ProfilePage({
//        authViewModel.logout()
//        nestedNavController.navController.navigate("welcome") {
//            popUpTo(0) { inclusive = true }
//        }
//    }, userState)
}