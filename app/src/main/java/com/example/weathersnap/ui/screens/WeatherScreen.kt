package com.example.weathersnap.ui.screens

import android.R.attr.text
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weathersnap.data.model.City
import com.example.weathersnap.data.model.WeatherData
import com.example.weathersnap.viewmodel.SuggestionsState
import com.example.weathersnap.viewmodel.WeatherState
import com.example.weathersnap.viewmodel.WeatherViewModel

val BgGradientTop = Color(0xFFE1F5FE)
val BgGradientBottom = Color(0xFFE8F5E9)
val CardGradientTop = Color(0xFFD6EAF8)
val CardGradientBottom = Color(0xFF85C1E9)
val ButtonGradientStart = Color(0xFF0091EA)
val ButtonGradientEnd = Color(0xFF00E676)
val TextPrimary = Color(0xFF111111)
val TextSecondary = Color(0xFF666666)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    onCreateReport: (WeatherData) -> Unit,
    onViewReports: () -> Unit
) {
    val cityQuery by viewModel.cityQuery.collectAsStateWithLifecycle()
    val suggestionsState by viewModel.suggestionsState.collectAsStateWithLifecycle()
    val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgGradientTop, BgGradientBottom)))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Header Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "WeatherSnap",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp,
                            color = TextPrimary,
                            letterSpacing = (-1).sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Live weather reports",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                    OutlinedButton(
                        onClick = onViewReports,
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Reports", color = TextPrimary)
                    }
                }
            }

            // Search Field
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.6f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "City",
                            fontSize = 15.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        TextField(
                            value = cityQuery,
                            onValueChange = viewModel::onCityQueryChanged,
                            placeholder = {
                                Text(
                                    "Enter city name.",
                                    fontSize = 14.sp,
                                    color = TextSecondary.copy(alpha = 0.5f)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true,
                            trailingIcon = {
                                if (suggestionsState is SuggestionsState.Loading) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else if (weatherState is WeatherState.Idle) {
                                    Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
                                }
                            }
                        )
                    }
                }
            }

            item {
                AnimatedVisibility(
                    visible = suggestionsState is SuggestionsState.Success,
                    enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
                    exit = shrinkVertically(animationSpec = tween(200)) + fadeOut()
                ) {
                    val cities = (suggestionsState as? SuggestionsState.Success)?.cities ?: emptyList()
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column {
                            cities.forEach { city ->
                                CityItem(city = city, onClick = { viewModel.onCitySelected(city) })
                                if (city != cities.last()) HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                            }
                        }
                    }
                }
            }

            // Weather State
            item {
                AnimatedContent(
                    targetState = weatherState,
                    transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) },
                    label = "weather_state"
                ) { state ->
                    when (state) {
                        is WeatherState.Idle -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 64.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "⛅",
                                    fontSize = 64.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Search for a city to see weather\nreports and camera evidence.",
                                    textAlign = TextAlign.Center,
                                    color = TextSecondary,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }

                        is WeatherState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = ButtonGradientEnd)
                            }
                        }

                        is WeatherState.Error -> {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = "⚠ ${state.message}",
                                    modifier = Modifier.padding(16.dp),
                                    color = Color(0xFFD32F2F)
                                )
                            }
                        }

                        is WeatherState.Success -> {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                NewWeatherCard(weatherData = state.data)
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = "Ready",
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Report readiness",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = TextPrimary
                                            )

                                        }
                                    }
                                }

                                // Gradient Button
                                Button(
                                    onClick = { onCreateReport(state.data) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    colors = listOf(ButtonGradientStart, ButtonGradientEnd)
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Create Report",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun NewWeatherCard(weatherData: WeatherData) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(listOf(CardGradientTop, CardGradientBottom)))
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "${weatherData.cityName}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = weatherData.condition,
                fontSize = 16.sp,
                color = TextPrimary.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${weatherData.temperature}°C",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = (-2).sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherStatItem(title = "Humidity", value = "${weatherData.humidity}%", icon = "💧")
                Spacer(modifier = Modifier.width(6.dp))
                WeatherStatItem(title = "Wind", value = "${weatherData.windSpeed} m/s", icon = "🌬")
                Spacer(modifier = Modifier.width(6.dp))
                WeatherStatItem(title = "Pressure", value = "${weatherData.pressure}", icon = "🧭")
            }
        }
    }
}

@Composable
fun WeatherStatItem(title: String, value: String, icon: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.4f)),
        modifier = Modifier
            .width(100.dp)
            .height(110.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 12.sp, color = TextPrimary.copy(alpha = 0.7f))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
fun CityItem(city: City, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = city.name, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 16.sp)
            Text(text = city.country, fontSize = 13.sp, color = TextSecondary)
        }
    }
}