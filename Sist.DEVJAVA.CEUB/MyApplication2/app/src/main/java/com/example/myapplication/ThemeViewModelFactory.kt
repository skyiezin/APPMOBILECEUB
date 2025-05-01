package com.example.myapplication

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Fábrica para criar instâncias de ThemeViewModel com dependência de Application
class ThemeViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    // Cria o ViewModel solicitado, injetando o Application no construtor
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica se a classe solicitada é ThemeViewModel
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            // Retorna uma nova instância de ThemeViewModel
            return ThemeViewModel(app) as T
        }
        // Lança exceção se a classe não for suportada
        throw IllegalArgumentException("Classe de ViewModel desconhecida: ${modelClass.name}")
    }
}
