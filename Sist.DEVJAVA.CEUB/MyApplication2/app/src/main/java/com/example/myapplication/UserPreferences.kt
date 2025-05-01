package com.example.myapplication

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
// Cria propriedade de extensão para DataStore com o nome "user_prefs"
private val Context.dataStore by preferencesDataStore(name = "user_prefs")
// Gerencia dados de usuário usando DataStore
class UserPreferences(private val context: Context) {
    // Salva informações do usuário: nome, telefone, endereço e senha
    fun saveUser(
        username: String,
        name: String,
        phone: String,
        address: String,
        password: String
    ) = runBlocking {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("${'$'}{username}_name")]     = name
            prefs[stringPreferencesKey("${'$'}{username}_phone")]    = phone
            prefs[stringPreferencesKey("${'$'}{username}_address")]  = address
            prefs[stringPreferencesKey("${'$'}{username}_password")] = password
        }
    }
    // Recupera dados do usuário a partir do nome de usuário
    fun getUser(username: String): Map<String, String> = runBlocking {
        val prefs = context.dataStore.data.first()
        return@runBlocking mapOf(
            "name"     to (prefs[stringPreferencesKey("${'$'}{username}_name")]     ?: ""),
            "phone"    to (prefs[stringPreferencesKey("${'$'}{username}_phone")]    ?: ""),
            "address"  to (prefs[stringPreferencesKey("${'$'}{username}_address")]  ?: ""),
            "password" to (prefs[stringPreferencesKey("${'$'}{username}_password")] ?: "")
        )
    }
    // Salva usuário atualmente logado para manter a sessão
    fun saveLoggedUser(username: String) = runBlocking {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("logged_user")] = username
        }
    }
    // Recupera nome de usuário logado, retorna string vazia se não houver
    fun getLoggedUser(): String = runBlocking {
        val prefs = context.dataStore.data.first()
        return@runBlocking prefs[stringPreferencesKey("logged_user")] ?: ""
    }
}
