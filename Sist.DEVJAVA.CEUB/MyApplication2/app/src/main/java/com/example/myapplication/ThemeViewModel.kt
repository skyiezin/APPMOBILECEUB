package com.example.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ViewModel que gerencia a escolha de tema claro/escuro
class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    // Acesso às preferências de tema via DataStore
    private val themePrefs = ThemePreferences(application)
    // Estado interno que guarda o modo escuro atual
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()
    init {
        // Ao iniciar, coleta o valor salvo em DataStore e atualiza o estado
        viewModelScope.launch {
            themePrefs.isDarkMode.collect { savedTheme ->
                _isDarkTheme.value = savedTheme
            }
        }
    }
    // Alterna entre tema claro e escuro
    fun toggleTheme() {
        val newTheme = !_isDarkTheme.value  // inverte o valor atual
        _isDarkTheme.value = newTheme      // atualiza estado imediato
        // Salva a nova preferência em background
        viewModelScope.launch {
            themePrefs.saveTheme(newTheme)
        }
    }
}
