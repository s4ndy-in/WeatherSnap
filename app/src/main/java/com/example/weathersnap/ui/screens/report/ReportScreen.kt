package com.example.weathersnap.ui.screens.report

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.weathersnap.data.model.WeatherData
import com.example.weathersnap.viewmodel.ReportViewModel
import com.example.weathersnap.viewmodel.SaveState
import java.io.File

// Design Colors
private val BgTop = Color(0xFFE1F5FE)
private val BgBottom = Color(0xFFE8F5E9)
private val BtnGradientStart = Color(0xFF0091EA)
private val BtnGradientEnd = Color(0xFF00E676)
private val GlassDark = Color(0xFF111111)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: ReportViewModel,
    onCapturePhoto: () -> Unit,
    onReportSaved: () -> Unit,
    onBack: () -> Unit
) {
    val weatherData by viewModel.weatherData.collectAsStateWithLifecycle()
    val compressedImagePath by viewModel.compressedImagePath.collectAsStateWithLifecycle()
    val originalSizeKb by viewModel.originalSizeKb.collectAsStateWithLifecycle()
    val compressedSizeKb by viewModel.compressedSizeKb.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()

    // Navigate after save
    LaunchedEffect(saveState) {
        if (saveState is SaveState.Success) {
            viewModel.resetReport()
            onReportSaved()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgTop, BgBottom)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 48.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Create Report",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = GlassDark
                    )
                    Text(
                        text = "Capture, compress, annotate",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                // Back Button
                Surface(
                    onClick = onBack,
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        "Back",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = GlassDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Weather Cards
            weatherData?.let { data ->
                ReportWeatherSection(data)
            }

            // Image Preview Area
            AnimatedContent(
                targetState = compressedImagePath,
                transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(200)) },
                label = "image_preview"
            ) { path ->
                if (path != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        AsyncImage(
                            model = File(path),
                            contentDescription = "Captured Photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Crop
                        )
                        // Size Comparison Pills
                        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                            SizePill("Original", "$originalSizeKb KB")
                            SizePill("Compressed", "$compressedSizeKb KB")
                        }
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("Photo preview", color = GlassDark, fontSize = 16.sp)
                        }
                    }
                }
            }

            // Capture Button (Gradient)
            GradientButton(
                text = if (compressedImagePath != null) "Retake Photo" else "Capture Photo",
                onClick = onCapturePhoto,
                enabled = true
            )

            // Notes Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Field Notes",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = GlassDark
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = viewModel::onNotesChanged,
                    placeholder = { Text("Notes", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            // Save Button
            val isSaveEnabled = compressedImagePath != null && saveState !is SaveState.Saving
            Button(
                onClick = { viewModel.saveReport() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = isSaveEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    contentColor = Color.White,
                    disabledContentColor = Color.Gray
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (isSaveEnabled)
                                Brush.horizontalGradient(listOf(BtnGradientStart, BtnGradientEnd))
                            else
                                Brush.horizontalGradient(listOf(Color.LightGray.copy(alpha = 0.3f), Color.LightGray.copy(alpha = 0.3f)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (saveState is SaveState.Saving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Save Report", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (isSaveEnabled) Color.White else Color.Gray)
                    }
                }
            }

            if (saveState is SaveState.Error) {
                Text(text = "⚠ ${(saveState as SaveState.Error).message}", color = Color.Red, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun ReportWeatherSection(data: WeatherData) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Top White Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = data.cityName, fontSize = 20.sp, color = GlassDark)
                Text(text = "${data.temperature.toInt()}°C", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = GlassDark)
                Text(text = data.condition, fontSize = 16.sp, color = Color.DarkGray)
            }
        }

        // Bottom Glass Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ReportStatItem("Humidity", "${data.humidity}%", "💧")
            ReportStatItem("Wind", "${data.windSpeed} m/s", "🌬")
            ReportStatItem("Pressure", "${data.pressure}", "🧭")
        }
    }
}

@Composable
fun ReportStatItem(title: String, value: String, icon: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF90CAF9).copy(alpha = 0.4f), // Soft blue glass
        modifier = Modifier.width(105.dp).height(110.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, fontSize = 13.sp, color = GlassDark.copy(alpha = 0.8f))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GlassDark)
        }
    }
}

@Composable
fun GradientButton(text: String, onClick: () -> Unit, enabled: Boolean) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        enabled = enabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(listOf(BtnGradientStart, BtnGradientEnd))),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SizePill(label: String, value: String) {
    Surface(shape = RoundedCornerShape(50), color = Color(0xFF81C784)) { // Soft Green
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(text = "$label ", fontSize = 12.sp, color = Color.White)
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}