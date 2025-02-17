package com.example.internify.ui.screen

import com.example.internify.data.AppRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.internify.InternifyApp
import com.example.internify.model.Internship
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface HomeScreenUIState {
    data class Success(val data: List<Internship>): HomeScreenUIState
}

class HomeScreenViewModel(private var appRepository: AppRepository): ViewModel(){


    private val _uiState = MutableStateFlow<HomeScreenUIState>(HomeScreenUIState.Success(emptyList()))
    val uiState: StateFlow<HomeScreenUIState> = _uiState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Internship>>(emptyList())
    val searchResults: StateFlow<List<Internship>> = _searchResults.asStateFlow()

    init {
        getAllInternships()
    }

    fun getAllInternships() {
        viewModelScope.launch {
            appRepository.getAllInternships()
                .collect { jobPostings ->
                    _uiState.value = HomeScreenUIState.Success(jobPostings)
                }
        }
    }


    fun addInternship(internship: Internship) {
        viewModelScope.launch {
            appRepository.insertInternship(internship)
            getAllInternships()
        }
    }

    fun updateInternship(internship: Internship) {
        viewModelScope.launch {
            appRepository.updateInternship(internship)
        }
    }

    fun deleteInternship(id:Int) {
        viewModelScope.launch {
            appRepository.deleteInternship(id)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as InternifyApp
                val appContainer = application.container
                return HomeScreenViewModel(appContainer.appRepository) as T
            }
        }
    }
}