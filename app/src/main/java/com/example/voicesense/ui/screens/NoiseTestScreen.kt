package com.example.voicesense.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun NoiseTestScreen(onPassed: () -> Unit) {
    var isTesting by remember { mutableStateOf(false) }
    var dbValue by remember { mutableStateOf<Int?>(0) }
    var message by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Top title + description
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
        ) {
            Text(
                text = "Test Ambient Noise Level",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Before you can start the call we will have to check your ambient noise level.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )
        }

        // Gauge + message in the middle
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            NoiseGauge(dbValue = dbValue ?: 0)

            Spacer(Modifier.height(8.dp))
            Text(
                text = "${dbValue ?: 0} db",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))
            message?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }

        // Bottom full-width Start Test button
        Button(
            onClick = {
                isTesting = true
                val measured = Random.nextInt(25, 55) // simulate measurement 0–60
                dbValue = measured
                message = if (measured < 40) {
                    "Good to proceed"
                } else {
                    "Please move to a quieter place"
                }
                isTesting = false
                if (measured < 40) {
                    onPassed()
                }
            },
            enabled = !isTesting,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = "Start Test", fontSize = 18.sp)
        }
    }
}


@Composable
private fun NoiseGauge(dbValue: Int) {
    val clampedDb = dbValue.coerceIn(0, 60)
    val startAngle = 180f
    val sweepAngle = 180f
    val thresholdAngle = startAngle + sweepAngle * (40f / 60f)

    Canvas(
        modifier = Modifier
            .size(260.dp)
            .padding(8.dp)
    ) {
        val strokeWidth = 24f
        val radius = size.minDimension / 2 - strokeWidth
        val center = Offset(size.width / 2, size.height / 2)

        val oval = Rect(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius
        )

        // Blue arc (0–40 dB)
        drawArc(
            color = Color(0xFF1976D2),
            startAngle = startAngle,
            sweepAngle = thresholdAngle - startAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            size = oval.size,
            topLeft = oval.topLeft
        )

        // Red arc (40–60 dB)
        drawArc(
            color = Color(0xFFE53935),
            startAngle = thresholdAngle,
            sweepAngle = startAngle + sweepAngle - thresholdAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            size = oval.size,
            topLeft = oval.topLeft
        )

        // Tick labels 10,20,30,40,50
        val tickValues = listOf(10, 20, 30, 40, 50)
        val labelRadius = radius - strokeWidth * 1.2f
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#718792")
            textSize = 28f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }

        drawIntoCanvas { canvas ->
            tickValues.forEach { value ->
                val ratio = value / 60f
                val angleDeg = startAngle + sweepAngle * ratio
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val x = center.x + (labelRadius * cos(angleRad)).toFloat()
                val y = center.y + (labelRadius * sin(angleRad)).toFloat() + 10f
                canvas.nativeCanvas.drawText(value.toString(), x, y, textPaint)
            }
        }

        // Needle
        val ratio = clampedDb / 60f
        val needleAngleDeg = startAngle + sweepAngle * ratio
        val needleAngleRad = Math.toRadians(needleAngleDeg.toDouble())
        val needleLength = radius * 0.8f
        val needleEnd = Offset(
            x = center.x + (needleLength * cos(needleAngleRad)).toFloat(),
            y = center.y + (needleLength * sin(needleAngleRad)).toFloat()
        )

        drawLine(
            color = Color(0xFFB0BEC5),
            start = center,
            end = needleEnd,
            strokeWidth = 8f,
            cap = StrokeCap.Round
        )
    }
}