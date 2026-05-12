package com.example.weathersnap.viewmodel

import androidx.lifecycle.ViewModel
import com.example.weathersnap.data.local.entity.WeatherReportEntity
import com.example.weathersnap.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SavedReportsViewModel @Inject constructor(
    repository: ReportRepository
) : ViewModel() {

    val reportsState: StateFlow<ReportsState> = repository
        .getAllReports()
        .map { reports ->
            if (reports.isEmpty()) ReportsState.Empty
            else ReportsState.Success(reports)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ReportsState.Loading
        )
}

//  Reports States
sealed class ReportsState {
    object Loading : ReportsState()
    object Empty : ReportsState()
    data class Success(val reports: List<WeatherReportEntity>) : ReportsState()
}