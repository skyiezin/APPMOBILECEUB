package com.example.myapplication

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

    // Cria a instância do DataStore para preferências de tema
private val Context.dataStore by preferencesDataStore(name = "theme_prefs")
    // Gerencia a preferência de tema (claro/escuro) do usuário
class ThemePreferences(private val context: Context) {
    //guarda a escolha de tema do usuario no dispositivo para que ele
    //não precise escolher toda vez que logar no app
    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }
    // Fluxo que emite o valor atual de modo escuro, padrão é false
    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[DARK_MODE_KEY] ?: false }
        // Salva a escolha de tema (escuro ou claro) no DataStore
    suspend fun saveTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDark
        }
    }
}
