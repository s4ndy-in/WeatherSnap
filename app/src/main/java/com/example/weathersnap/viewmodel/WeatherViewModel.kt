package com.example.weathersnap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersnap.data.model.City
import com.example.weathersnap.data.model.WeatherData
import com.example.weathersnap.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    // City Search
    private val _cityQuery = MutableStateFlow("")
    val cityQuery: StateFlow<String> = _cityQuery.asStateFlow()

    private val _suggestionsState = MutableStateFlow<SuggestionsState>(SuggestionsState.Idle)
    val suggestionsState: StateFlow<SuggestionsState> = _suggestionsState.asStateFlow()

    //  Weather
    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Idle)
    val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()

    //  Selected City
    private val _selectedCity = MutableStateFlow<City?>(null)
    val selectedCity: StateFlow<City?> = _selectedCity.asStateFlow()

    init {
        // Debounce city input
        _cityQuery
            .debounce(300L)
            .filter { it.length > 2 }
            .distinctUntilChanged()
            .onEach { query -> fetchSuggestions(query) }
            .launchIn(viewModelScope)
    }

    fun onCityQueryChanged(query: String) {
        _cityQuery.value = query
        if (query.length <= 2) {
            _suggestionsState.value = SuggestionsState.Idle
        }
    }

    private fun fetchSuggestions(query: String) {
        viewModelScope.launch {
            _suggestionsState.value = SuggestionsState.Loading
            repository.searchCities(query).fold(
                onSuccess = { cities ->
                    _suggestionsState.value = if (cities.isEmpty())
                        SuggestionsState.Empty
                    else
                        SuggestionsState.Success(cities)
                },
                onFailure = {
                    _suggestionsState.value = SuggestionsState.Error(
                        it.message ?: "Failed to load suggestions"
                    )
                }
            )
        }
    }

    fun onCitySelected(city: City) {
        _selectedCity.value = city
        _cityQuery.value = city.displayName
        _suggestionsState.value = SuggestionsState.Idle
        fetchWeather(city)
    }

    private fun fetchWeather(city: City) {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            repository.getWeather(city).fold(
                onSuccess = { weather ->
                    _weatherState.value = WeatherState.Success(weather)
                },
                onFailure = {
                    _weatherState.value = WeatherState.Error(
                        it.message ?: "Failed to load weather"
                    )
                }
            )
        }
    }

    fun resetWeather() {
        _weatherState.value = WeatherState.Idle
        _suggestionsState.value = SuggestionsState.Idle
        _cityQuery.value = ""
        _selectedCity.value = null
    }
}

// Suggestion States
sealed class SuggestionsState {
    object Idle : SuggestionsState()
    object Loading : SuggestionsState()
    object Empty : SuggestionsState()
    data class Success(val cities: List<City>) : SuggestionsState()
    data class Error(val message: String) : SuggestionsState()
}

// Weather States
sealed class WeatherState {
    object Idle : WeatherState()
    object Loading : WeatherState()
    data class Success(val data: WeatherData) : WeatherState()
    data class Error(val message: String) : WeatherState()
}