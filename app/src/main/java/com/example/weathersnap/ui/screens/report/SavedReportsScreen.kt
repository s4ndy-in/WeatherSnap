package com.example.weathersnap.ui.screens.report

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.weathersnap.data.local.entity.WeatherReportEntity
import com.example.weathersnap.viewmodel.ReportsState
import com.example.weathersnap.viewmodel.SavedReportsViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SavedReportsScreen(
    onBack: () -> Unit,
    viewModel: SavedReportsViewModel = hiltViewModel()
) {
    val reportsState by viewModel.reportsState.collectAsStateWithLifecycle()

    Scaffold(containerColor = Color(0xFFFAFAFA)) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Top Navigation Bar (Matches Image 2)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { onBack() }
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF000000))
                }

                Text(
                    text = "Saved Weather Reports Gallery",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            AnimatedContent(
                targetState = reportsState,
                transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) },
                label = "reports_state"
            ) { state ->
                when (state) {
                    is ReportsState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF00E676))
                        }
                    }
                    is ReportsState.Empty -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📭", fontSize = 48.sp)
                                Spacer(Modifier.height(12.dp))
                                Text("No reports yet", fontWeight = FontWeight.SemiBold, color = Color.Black)
                            }
                        }
                    }
                    is ReportsState.Success -> {
                        LazyColumn(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            items(state.reports) { report ->
                                ReportCard(report = report)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCard(report: WeatherReportEntity) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(report.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image (Full width, rounded top)
            AsyncImage(
                model = File(report.imagePath),
                contentDescription = "Report Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(20.dp)) {
                // City + Temp Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(text = report.cityName, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.Black)
                        Text(text = report.condition, fontSize = 14.sp, color = Color.DarkGray)
                        Text(text = formattedDate, fontSize = 14.sp, color = Color.DarkGray)
                    }
                    Text(text = "${report.temperature.toInt()}°C", fontWeight = FontWeight.Bold, fontSize = 28.sp, color = Color.Black)
                }

                Spacer(Modifier.height(16.dp))

                // Size Chips
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GallerySizePill("Original", "${report.originalSizeKb} KB")
                    GallerySizePill("Compressed", "${report.compressedSizeKb} KB")
                }

                // Notes
                if (report.notes.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Text(text = report.notes, fontSize = 15.sp, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun GallerySizePill(label: String, value: String) {
    Surface(shape = RoundedCornerShape(50), color = Color(0xFF81C784)) { // Soft Green matching image
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
            Text(text = "$label ", fontSize = 12.sp, color = Color.White)
            Text(text = value, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f)) // Slight contrast for value
        }
    }
}