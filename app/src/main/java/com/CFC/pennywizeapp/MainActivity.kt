package com.CFC.pennywizeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.CFC.pennywizeapp.navigation.AppNavGraph
import com.CFC.pennywizeapp.navigation.Routes
import com.CFC.pennywizeapp.theme.PennyWizeAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PennyWizeAppTheme {
                AppNavGraph()
            }
        }
    }
}