package com.example.internify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.internify.ui.screen.HomeScreen
import com.example.internify.ui.screen.HomeScreenViewModel
import com.example.internify.ui.theme.InternifyTheme

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val homeViewModel: HomeScreenViewModel by viewModels { HomeScreenViewModel.Factory }
        homeViewModel.deleteInternship(6)
        setContent {
            InternifyTheme {
                enableEdgeToEdge()
                HomeScreen(homeViewModel)
            }
        }

    }
}






