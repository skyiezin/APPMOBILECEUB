package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.FinancePreferences
import com.example.myapplication.UserPreferences


   //Tela de entrada de dados financeiros do usuário.

@Composable
fun FinanceDataScreen(navController: NavController) {
    // Recupera o contexto da Activity para Toasts e SharedPreferences
    val context = LocalContext.current
    // Instâncias de preferências para dados financeiros e usuário
    val financePrefs = remember { FinancePreferences(context) }
    val userPrefs    = remember { UserPreferences(context) }

    // Carrega o nome do usuario pra exibir uma mensagem de boas vindas
    val loggedUser = userPrefs.getLoggedUser()
    val userName   = userPrefs.getUser(loggedUser)["name"] ?: "usuário"

    // Estados para cada campo de texto (inicialmente vazios)
    var mesAno      by remember { mutableStateOf("") }
    var salario     by remember { mutableStateOf("") }
    var gastosFixos by remember { mutableStateOf("") }
    var gastosSup   by remember { mutableStateOf("") }
    var gastosEmerg by remember { mutableStateOf("") }
    var meta        by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Boas vindas
        Text(
            text = "Olá, $userName!",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Request pro usuario
        Text("Insira seus gastos", style = MaterialTheme.typography.headlineSmall)

        // Campo para mês/ano (formato MM/YY)
        OutlinedTextField(
            value = mesAno,
            onValueChange = { mesAno = it },
            label = { Text("Mês/Ano (ex: 04/2025)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campos numéricos para salário e gastos
        OutlinedTextField(
            value = salario,
            onValueChange = { salario = it },
            label = { Text("Salário (R$)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = gastosFixos,
            onValueChange = { gastosFixos = it },
            label = { Text("Gastos Fixos (R$)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = gastosSup,
            onValueChange = { gastosSup = it },
            label = { Text("Gastos Supérfluos (R$)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = gastosEmerg,
            onValueChange = { gastosEmerg = it },
            label = { Text("Gastos Emergenciais (R$)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = meta,
            onValueChange = { meta = it },
            label = { Text("Meta de Economia (R$)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Botão para salvar e navegar ao resumo do mês
        Button(
            onClick = {
                // Substitui / por - para chave de armazenamento
                val safeMesAno = mesAno.replace("/", "-")
                financePrefs.saveFinanceData(
                    loggedUser,
                    safeMesAno,
                    salario,
                    gastosFixos,
                    gastosSup,
                    gastosEmerg,
                    meta
                )
                // Feedback e navegação
                Toast.makeText(context, "Dados salvos com sucesso", Toast.LENGTH_SHORT).show()
                navController.navigate("resultado/$safeMesAno")
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Salvar")
        }

        // Botões de navegação para perfil, lista de meses e gráfico
        Button(
            onClick = { navController.navigate("profile") },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Ver Perfil")
        }
        Button(
            onClick = { navController.navigate("meses") },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Ver meses anteriores")
        }
        Button(
            onClick = { navController.navigate("graficoGastos") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Ver gráfico de gastos")
        }
    }
}
