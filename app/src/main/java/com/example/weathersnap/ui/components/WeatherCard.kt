package com.example.weathersnap.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathersnap.data.model.WeatherData
import com.example.weathersnap.ui.theme.*

@Composable
fun WeatherCard(
    weatherData: WeatherData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // City + Temperature
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = weatherData.cityName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = weatherData.condition,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
                Text(
                    text = "${weatherData.temperature.toInt()}°C",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherStatChip(label = "Humidity", value = "${weatherData.humidity}%")
                WeatherStatChip(label = "Wind", value = "${weatherData.windSpeed} m/s")
                WeatherStatChip(label = "Pressure", value = "${weatherData.pressure}")
            }
        }
    }
}

@Composable
fun WeatherStatChip(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = ChipBackground
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 11.sp, color = TextSecondary)
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = WeatherGreenDark
            )
        }
    }
}