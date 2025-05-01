package com.example.myapplication

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.YearMonth

// Extensão que cria o DataStore “finance_prefs” no Context
private val Context.dataStore by preferencesDataStore(name = "finance_prefs")

// Gerencia dados financeiros do usuário usando DataStore
class FinancePreferences(private val context: Context) {

    // Gera uma chave única: "{username}_{field}_{month}"
    private fun key(username: String, field: String, month: String) =
        stringPreferencesKey("${username}_${field}_$month")

    // Salva ou atualiza os valores de salário, fixos, variável, emergência e meta
    fun saveFinanceData(
        username: String,
        month: String,
        salary: String,
        fixed: String,
        variable: String,
        emergency: String,
        goal: String
    ) = runBlocking {
        context.dataStore.edit { prefs ->
            prefs[key(username, "salary",   month)] = salary
            prefs[key(username, "fixed",    month)] = fixed
            prefs[key(username, "variable", month)] = variable
            prefs[key(username, "emergency",month)] = emergency
            prefs[key(username, "goal",     month)] = goal
        }
    }

    // Lê os valores de um mês específico como strings
    fun getFinanceData(username: String, month: String): Map<String, String> = runBlocking {
        val prefs = context.dataStore.data.first()
        return@runBlocking mapOf(
            "salary"    to (prefs[key(username, "salary",   month)] ?: ""),
            "fixed"     to (prefs[key(username, "fixed",    month)] ?: ""),
            "variable"  to (prefs[key(username, "variable", month)] ?: ""),
            "emergency" to (prefs[key(username, "emergency",month)] ?: ""),
            "goal"      to (prefs[key(username, "goal",     month)] ?: "")
        )
    }

    // Lista todos os meses (MM-YYYY) para os quais o usuário tem dados, em ordem decrescente
    fun getAllMonths(username: String): List<String> = runBlocking {
        val prefs = context.dataStore.data.first()
        return@runBlocking prefs.asMap().keys
            .mapNotNull { prefKey ->
                val raw = prefKey.name
                val prefix = "${username}_salary_"
                if (raw.startsWith(prefix)) raw.removePrefix(prefix)
                else null
            }
            .distinct()
            .sortedDescending()
    }

    // Retorna o mês atual no formato MM-YYYY
    fun getCurrentMonth(): String {
        val now = YearMonth.now()
        return now.monthValue.toString().padStart(2, '0') + "-${now.year}"
    }

    // Exclui todos os registros daquele mês para o usuário
    fun deleteFinanceData(username: String, month: String) = runBlocking {
        context.dataStore.edit { prefs ->
            prefs.remove(key(username, "salary",   month))
            prefs.remove(key(username, "fixed",    month))
            prefs.remove(key(username, "variable", month))
            prefs.remove(key(username, "emergency",month))
            prefs.remove(key(username, "goal",     month))
        }
    }

    // Recupera todos os registros financeiros agrupados por mês, convertendo valores para Double
    fun getAllFinanceData(username: String): Map<String, Map<String, Double>> = runBlocking {
        val prefs = context.dataStore.data.first()
        val data = mutableMapOf<String, MutableMap<String, Double>>()

        prefs.asMap().forEach { (prefKey, value) ->
            val raw = prefKey.name
            if (raw.startsWith("${username}_")) {
                val parts = raw.removePrefix("${username}_").split("_")
                if (parts.size == 2) {
                    val field = parts[0]    // ex: "fixed"
                    val month = parts[1]    // ex: "04-2025"
                    val number = (value as? String)?.toDoubleOrNull() ?: return@forEach

                    // Agrupa por mês
                    val monthMap = data.getOrPut(month) { mutableMapOf() }
                    monthMap[field] = number
                }
            }
        }
        return@runBlocking data
    }
}
