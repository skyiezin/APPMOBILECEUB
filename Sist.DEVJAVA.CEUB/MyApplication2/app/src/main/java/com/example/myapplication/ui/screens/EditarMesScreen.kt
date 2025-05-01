package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.FinancePreferences
import com.example.myapplication.UserPreferences
//Tela de edição dos dados dos meses salvos pelo usuário
@Composable
fun EditarMesScreen(
    navController: NavController,
    mesAno: String
) {
    // Contexto para acessar SharedPreferences e outros recursos
    val context = LocalContext.current
    // Preferências de finanças e usuário
    val financePrefs = remember { FinancePreferences(context) }
    val userPrefs    = remember { UserPreferences(context) }

    // Usuário logado e dados salvos para o mês/ano selecionado
    val username = userPrefs.getLoggedUser()
    val dados    = financePrefs.getFinanceData(username, mesAno)

    // Estados locais para campos de texto, iniciados com valores já salvos
    var salario     by remember { mutableStateOf(dados["salary"] ?: "") }
    var gastosFixos by remember { mutableStateOf(dados["fixed"]  ?: "") }
    var gastosSup   by remember { mutableStateOf(dados["variable"] ?: "") }
    var gastosEmerg by remember { mutableStateOf(dados["emergency"] ?: "") }
    var meta        by remember { mutableStateOf(dados["goal"]    ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        // Título com indicação do mês/ano sendo editado
        Text("Editar Mês $mesAno", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // Campos de entrada pros valores inseridos pelo usuario
        OutlinedTextField(
            value = salario,
            onValueChange = { salario = it },
            label = { Text("Salário (R$)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = gastosFixos,
            onValueChange = { gastosFixos = it },
            label = { Text("Gastos Fixos (R$)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = gastosSup,
            onValueChange = { gastosSup = it },
            label = { Text("Gastos Supérfluos (R$)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = gastosEmerg,
            onValueChange = { gastosEmerg = it },
            label = { Text("Gastos Emergenciais (R$)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = meta,
            onValueChange = { meta = it },
            label = { Text("Meta de Economia (R$)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botão para salvar alterações e navegar ao resumo do mês
        Button(
            onClick = {
                financePrefs.saveFinanceData(
                    username, mesAno,
                    salario, gastosFixos, gastosSup, gastosEmerg, meta
                )
                // Retorna para a tela de resultados com o mesmo parâmetro
                navController.navigate("resultado/$mesAno")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Alterações")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botão de cancelamento que retorna à tela anterior
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}
