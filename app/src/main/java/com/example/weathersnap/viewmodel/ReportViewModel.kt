package com.example.weathersnap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersnap.data.local.entity.WeatherReportEntity
import com.example.weathersnap.data.model.WeatherData
import com.example.weathersnap.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

    private val _weatherData = MutableStateFlow<WeatherData?>(null)
    val weatherData: StateFlow<WeatherData?> = _weatherData.asStateFlow()

    // Captured image paths-
    private val _capturedImagePath = MutableStateFlow<String?>(null)
    val capturedImagePath: StateFlow<String?> = _capturedImagePath.asStateFlow()

    private val _compressedImagePath = MutableStateFlow<String?>(null)
    val compressedImagePath: StateFlow<String?> = _compressedImagePath.asStateFlow()

    // Image size
    private val _originalSizeKb = MutableStateFlow(0L)
    val originalSizeKb: StateFlow<Long> = _originalSizeKb.asStateFlow()

    private val _compressedSizeKb = MutableStateFlow(0L)
    val compressedSizeKb: StateFlow<Long> = _compressedSizeKb.asStateFlow()

    // Notes
    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    // Save state
    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    fun setWeatherData(data: WeatherData) {
        _weatherData.value = data
    }

    fun onNotesChanged(text: String) {
        _notes.value = text
    }

    fun onImageCaptured(
        originalPath: String,
        compressedPath: String,
        originalSizeKb: Long,
        compressedSizeKb: Long
    ) {
        _capturedImagePath.value = originalPath
        _compressedImagePath.value = compressedPath
        _originalSizeKb.value = originalSizeKb
        _compressedSizeKb.value = compressedSizeKb
    }

    fun saveReport() {
        val weather = _weatherData.value ?: return
        val imagePath = _compressedImagePath.value ?: return

        viewModelScope.launch {
            _saveState.value = SaveState.Saving
            try {
                val entity = WeatherReportEntity(
                    cityName = weather.cityName,
                    temperature = weather.temperature,
                    condition = weather.condition,
                    humidity = weather.humidity,
                    windSpeed = weather.windSpeed,
                    pressure = weather.pressure,
                    imagePath = imagePath,
                    notes = _notes.value,
                    originalSizeKb = _originalSizeKb.value,
                    compressedSizeKb = _compressedSizeKb.value
                )
                repository.saveReport(entity)
                _saveState.value = SaveState.Success
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "Failed to save report")
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }

    fun resetReport() {
        _capturedImagePath.value = null
        _compressedImagePath.value = null
        _originalSizeKb.value = 0L
        _compressedSizeKb.value = 0L
        _notes.value = ""
        _saveState.value = SaveState.Idle
    }
}

// Save States
sealed class SaveState {
    object Idle : SaveState()
    object Saving : SaveState()
    object Success : SaveState()
    data class Error(val message: String) : SaveState()
}