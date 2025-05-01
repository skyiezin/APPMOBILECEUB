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

  // Tela de resumo financeiro mensal:
  // Exibe totais, economia, meta e maior categoria de gasto
  // Permite editar, excluir ou navegar a outras telas

@Composable
fun ResultadoFinanceiroScreen(navController: NavController, mesAno: String) {
    val context = LocalContext.current
    val userPrefs = UserPreferences(context)
    val financePrefs = FinancePreferences(context)
    // Recupera usuário e dados do mês selecionado
    val username = userPrefs.getLoggedUser()
    val dados = financePrefs.getFinanceData(username, mesAno)
    // Converte strings em floats, definindo 0f em caso de falha
    val salario     = dados["salary"]?.toFloatOrNull() ?: 0f
    val gastosFixos = dados["fixed"]?.toFloatOrNull() ?: 0f
    val gastosSup   = dados["variable"]?.toFloatOrNull() ?: 0f
    val gastosEmerg = dados["emergency"]?.toFloatOrNull() ?: 0f
    val meta        = dados["goal"]?.toFloatOrNull() ?: 0f
    // Calcula gastos totais, economia mensal e verifica as metas
    val totalGastos = gastosFixos + gastosSup + gastosEmerg
    val economia    = salario - totalGastos
    val atingiuMeta = economia >= meta
    // Identifica a categoria com maior valor gasto
    val maiorGasto = listOf(
        "Gastos Fixos" to gastosFixos,
        "Supérfluos"    to gastosSup,
        "Emergenciais"  to gastosEmerg
    ).maxByOrNull { it.second }
    // Estado para controlar exibição do diálogo de confirmação
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Resumo de $mesAno",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(16.dp))
        // Indicadores de gastos e economia
        Text("Total de gastos: R$ %.2f".format(totalGastos))
        Text("Economia: R$ %.2f".format(economia))
        Text("Meta atingida: ${if (atingiuMeta) "Sim 🎉" else "Não ❌"}")
        Text(
            "Maior tipo de gasto: ${maiorGasto?.first} (R$ %.2f)".format(maiorGasto?.second ?: 0f)
        )

        Spacer(modifier = Modifier.height(32.dp))
        // Botões de navegação e ações
        Button(onClick = { navController.navigate("finance") }, modifier = Modifier.fillMaxWidth()) {
            Text("Cadastrar novo mês")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { navController.navigate("meses") }, modifier = Modifier.fillMaxWidth()) {
            Text("Ver meses anteriores")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { navController.navigate("profile") }, modifier = Modifier.fillMaxWidth()) {
            Text("Voltar para o perfil")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { navController.navigate("editar/$mesAno") }, modifier = Modifier.fillMaxWidth()) {
            Text("Editar este mês")
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Excluir este mês")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { navController.navigate("graficoGastos") }, modifier = Modifier.fillMaxWidth()) {
            Text("Ver gráfico de gastos")
        }
    }
    // Confirma com o usuário a exclusão dos dados selecionados.
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar exclusão") },
            text = { Text("Tem certeza que deseja excluir os dados de $mesAno?") },
            confirmButton = {
                TextButton(onClick = {
                    // Remove dados e volta à tela de meses
                    financePrefs.deleteFinanceData(username, mesAno)
                    Toast.makeText(context, "Mês $mesAno excluído!", Toast.LENGTH_SHORT).show()
                    showDialog = false
                    navController.navigate("meses")
                }) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
