package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.FinancePreferences
import com.example.myapplication.UserPreferences

@Composable
fun MesesAnterioresScreen(navController: NavController) {
    // Contexto e instâncias das prefs
    val context = LocalContext.current
    val userPrefs    = remember { UserPreferences(context) }
    val financePrefs = remember { FinancePreferences(context) }

    // Usuário logado
    val username = userPrefs.getLoggedUser()

    // Estado que vai conter a lista de meses
    var meses by remember { mutableStateOf<List<String>>(emptyList()) }

    // Carrega os meses assim que o Composable entrar na composição
    LaunchedEffect(username) {
        meses = financePrefs.getAllMonths(username)
            .sortedDescending()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text("Meses Cadastrados", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        if (meses.isEmpty()) {
            // Caso não haja meses cadastrados
            Text("Nenhum mês cadastrado ainda.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            // Lista rolável de botões
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(meses) { mes ->
                    Button(
                        onClick = { navController.navigate("resultado/$mes") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text("Ver resumo de $mes")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Botão para relatório anual
        Button(
            onClick = { navController.navigate("relatorioAnual") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📊 Ver relatório anual")
        }

        Spacer(Modifier.height(8.dp))

        // Botão voltar
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voltar")
        }
    }
}
