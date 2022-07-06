package com.andreivanceadev.groupedbargraph

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andreivanceadev.groupedbargraph.BarChartDefaults.barCornerSize
import com.andreivanceadev.groupedbargraph.BarChartDefaults.barSpacing
import com.andreivanceadev.groupedbargraph.BarChartDefaults.barVisualMaxThreshold
import com.andreivanceadev.groupedbargraph.BarChartDefaults.barVisualMinThreshold
import com.andreivanceadev.groupedbargraph.BarChartDefaults.barWidth
import com.andreivanceadev.groupedbargraph.BarChartDefaults.groupBarAndLabelContainerHeight
import com.andreivanceadev.groupedbargraph.BarChartDefaults.groupBarContainerHeight
import com.andreivanceadev.groupedbargraph.ui.theme.GroupedBarGraphTheme
import com.andreivanceadev.groupedbargraph.ui.theme.white40
import com.andreivanceadev.groupedbargraph.ui.theme.white90
import kotlin.math.abs

@Preview
@Composable
private fun PreviewBarGraph() {
    //Preview for 3 bars per group
    GroupedBarGraphTheme {
        Box(modifier = Modifier.padding(24.dp)) {
            BarGraph(
                barGroups = mockedGraphData,
                onGroupSelectionChanged = {}
            )
        }
    }
}

val mockedGraphData = listOf(
    BarGroup(
        label = "2019",
        values = listOf(
            //we will have value/color pairs where the value will be between 100/-100
            //we need to add 3 values so we can have 3 bars
            67 to Color.Red,
            29 to Color.Green,
            -15 to Color.Blue,
        )
    ),
    BarGroup(
        label = "2020",
        values = listOf(
            78 to Color.Red,
            66 to Color.Green,
            -95 to Color.Blue,
        )
    ),
    BarGroup(
        label = "2021",
        values = listOf(
            22 to Color.Red,
            4 to Color.Green,
            33 to Color.Blue,
        )
    ),
    BarGroup(
        label = "2022",
        values = listOf(
            99 to Color.Red,
            -50 to Color.Green,
            -12 to Color.Blue,
        )
    )
)

private object BarChartDefaults {
    const val barVisualMinThreshold = -30
    const val barVisualMaxThreshold = 100

    val barWidth = 8.dp
    val barSpacing = 1.dp
    val barCornerSize = 1.dp

    val groupBarContainerHeight = barVisualMaxThreshold.dp + abs(barVisualMinThreshold).dp
    // groupBarContainerHeight + 40.dp height for the label
    val groupBarAndLabelContainerHeight = groupBarContainerHeight + 40.dp
}

@Composable
fun BarGraph(
    barGroups: List<BarGroup>,
    onGroupSelectionChanged: (index: Int) -> Unit = {}
) {
    val backgroundBrush = Brush.verticalGradient(
        listOf(Color.White.copy(alpha = 0.10f), Color.White.copy(alpha = 0.03f))
    )

    val selectedGroupIndex = remember {
        mutableStateOf(-1)
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .fillMaxWidth()
            .background(backgroundBrush)
            .padding(8.dp)
    ) {
        barGroups.forEachIndexed { index, item ->
            if (index == 0) {
                Spacer(modifier = Modifier.weight(1f))
            }
            ChartBarGroup(
                label = item.label,
                values = item.values,
                onGroupSelected = {
                    selectedGroupIndex.value = index
                    onGroupSelectionChanged(selectedGroupIndex.value)
                },
                onRemoveSelection = {
                    selectedGroupIndex.value = -1
                    onGroupSelectionChanged(selectedGroupIndex.value)
                },
                isSelected = selectedGroupIndex.value == index,
                isNothingSelected = selectedGroupIndex.value == -1
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChartBarGroup(
    modifier: Modifier = Modifier,
    label: String,
    values: List<Pair<Int, Color>>,
    onGroupSelected: () -> Unit = {},
    onRemoveSelection: () -> Unit = {},
    isSelected: Boolean,
    isNothingSelected: Boolean
) {
    Column(
        modifier = modifier
            .height(groupBarAndLabelContainerHeight)
            .pointerInteropFilter { event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        onGroupSelected()
                    }
                    MotionEvent.ACTION_UP -> {
                        onRemoveSelection()
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        onRemoveSelection()
                    }
                }
                true
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GroupLabel(
            text = label,
            isHighlighted = isSelected
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.height(groupBarContainerHeight), verticalAlignment = Alignment.Bottom
        ) {
            values.forEachIndexed { index, item ->
                val (realPercentage, color) = item
                val yOffset: Int
                val applyFadingEffect = realPercentage < barVisualMinThreshold
                val percentage = realPercentage.coerceIn(barVisualMinThreshold + 1, barVisualMaxThreshold - 1)

                yOffset = if (percentage >= 0) {
                    abs(barVisualMinThreshold)
                } else if (percentage in barVisualMinThreshold..-1) {
                    abs(barVisualMinThreshold) + percentage
                } else {
                    0
                }
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    ChartBar(
                        percentage = percentage,
                        brush = if (applyFadingEffect) {
                            Brush.verticalGradient(listOf(color, color.copy(alpha = 0f)))
                        } else {
                            Brush.verticalGradient(listOf(color, color))
                        },
                        isHighlighted = isSelected || isNothingSelected
                    )
                    Spacer(modifier = Modifier.height(yOffset.dp))
                }
                if (index in 0 until values.size - 1) {
                    Spacer(modifier = Modifier.width(barSpacing))
                }
            }
        }
    }
}


@Composable
private fun GroupLabel(
    text: String,
    color: Color = white40,
    highlightColor: Color = white90,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    isHighlighted: Boolean = false
) {
    Text(
        modifier = Modifier.padding(bottom = 8.dp),
        text = text,
        color = if (isHighlighted) highlightColor else color,
        style = textStyle
    )
}

@Composable
fun ChartBar(
    modifier: Modifier = Modifier,
    percentage: Int,
    brush: Brush,
    isHighlighted: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(barCornerSize))
            .height(abs(percentage).dp)
            .width(barWidth)
            .background(brush)
            .background(color = if (!isHighlighted) Color.Black.copy(alpha = 0.5f) else Color.Transparent)
    )
}

data class BarGroup(
    val label: String,
    val values: List<Pair<Int, Color>>
)