package com.andreivanceadev.groupedbargraph

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andreivanceadev.groupedbargraph.ui.theme.GroupedBarGraphTheme
import com.andreivanceadev.groupedbargraph.ui.theme.black90

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GroupedBarGraphTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .background(black90)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(24.dp)
                    ) {
                        BarGraph(barGroups = mockedGraphData)
                    }
                }
            }
        }
    }
}