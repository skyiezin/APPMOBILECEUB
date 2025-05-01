package com.example.myapplication.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.FinancePreferences
import com.example.myapplication.UserPreferences
import java.util.*

 // Tela de relatório anual de gastos:
 // Mostra quantas metas foram atingidas nos últimos 12 meses
 // Exibe um gráfico de pizza com a distribuição de gastos por categoria

@Composable
fun RelatorioAnualScreen(navController: NavController) {
    val context = LocalContext.current
    // Acessa os dados do usuario
    val userPrefs    = remember { UserPreferences(context) }
    val financePrefs = remember { FinancePreferences(context) }
    // Usuário logado
    val username = userPrefs.getLoggedUser()

    // Obtém todos os registros financeiros do usuário
    val dados = financePrefs.getAllFinanceData(username)

    // Ordena as chaves MM/YY de forma cronológica e pega os últimos 12 meses
    val ultimos12Meses = dados.keys
        .sortedBy { key ->
            val (mes, ano) = key.split("-").map { it.trim().toInt() }
            ano * 100 + mes  // ordenação no formato AAAA/MM
        }
        .takeLast(12)

    // Calcula número de "metas" atingidas e soma total de cada categoria
    val (metasAtingidas, totaisCategorias) = remember(dados, ultimos12Meses) {
        var atingidas = 0
        val totais = mutableMapOf(
            "fixed" to 0.0,
            "variable" to 0.0,
            "emergency" to 0.0
        )
        ultimos12Meses.forEach { mesAno ->
            val mesData = dados[mesAno] ?: return@forEach
            val salario    = mesData["salary"] ?: 0.0
            val meta       = mesData["goal"]   ?: 0.0
            val gastoTotal = (mesData["fixed"] ?: 0.0)
            + (mesData["variable"] ?: 0.0)
            + (mesData["emergency"] ?: 0.0)

            // Conta meta se economia >= meta definida
            if (salario - gastoTotal >= meta) atingidas++

            // Acumula por categoria
            totais["fixed"]     = totais["fixed"]!!     + (mesData["fixed"]     ?: 0.0)
            totais["variable"]  = totais["variable"]!!  + (mesData["variable"]  ?: 0.0)
            totais["emergency"] = totais["emergency"]!! + (mesData["emergency"] ?: 0.0)
        }
        atingidas to totais
    }

    // Identifica a categoria com maior gasto acumulado
    val categoriaMaisGasta = totaisCategorias
        .maxByOrNull { it.value }
        ?.key
        .orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título e resumo de metas
        Text(
            text = "Relatório dos Últimos 12 Meses",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(16.dp))

        Text("Metas atingidas: $metasAtingidas de ${ultimos12Meses.size} meses")
        Spacer(Modifier.height(8.dp))

        // Exibe categoria mais gasta, se existir
        if (categoriaMaisGasta.isNotEmpty()) {
            val label = when (categoriaMaisGasta) {
                "fixed"    -> "Gastos Fixos"
                "variable" -> "Supérfluos"
                "emergency"-> "Emergências"
                else        -> categoriaMaisGasta
            }
            Text("Categoria mais gasta: $label")
            Spacer(Modifier.height(4.dp))
            Text(
                text = when (categoriaMaisGasta) {
                    "fixed"     -> "Sua maior despesa foi com Gastos Fixos."
                    "variable"  -> "Seu maior gasto foi com Supérfluos."
                    "emergency" -> "Seu maior gasto foi com Emergências."
                    else         -> ""
                }
            )
        }

        Spacer(Modifier.height(24.dp))

        // Exibe gráfico de pizza se houver dados
        if (totaisCategorias.values.sum() > 0.0) {
            PieChart(totaisCategorias)
        } else {
            Text("Sem dados para exibir gráfico.")
        }

        Spacer(Modifier.height(24.dp))

        // Botão de retorno
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Voltar")
        }
    }
}

    //Gráfico de pizza mostrando proporção de cada categoria de gasto.

@Composable
fun PieChart(data: Map<String, Double>) {
    // Cores em tons de roxo para cada fatia
    val colors = listOf(
        Color(0xFF29033B), // roxo escuro para Gastos Fixos
        Color(0xFF3A0657), // roxo médio para Supérfluos
        Color(0xFF7B09AF)  // roxo claro para Emergências
    )
    // Converte valores para Float e calcula proporções
    val total = data.values.sum().toFloat()
    val proportions = data.values.map { (it.toFloat() / total) }
    // Traduz os tipos de gastos pra legenda que será exibida pro usuário
    val labels = data.keys.map { key ->
        when (key) {
            "fixed"    -> "Gastos Fixos"
            "variable" -> "Supérfluos"
            "emergency"-> "Emergências"
            else        -> key
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier.size(200.dp)) {
            var startAngle = -90f  // início no topo
            // Desenha cada fatia do gráfico
            proportions.forEachIndexed { index, prop ->
                val sweepAngle = prop * 360f
                drawArc(
                    color = colors.getOrElse(index) { Color.Gray },
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )
                startAngle += sweepAngle
            }
        }

        Spacer(Modifier.height(16.dp))

        // Legenda com retângulos coloridos e valores formatados
        labels.zip(proportions).forEachIndexed { index, (label, _) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(colors.getOrElse(index) { Color.Gray })
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "$label: R$ %.2f".format(data.values.toList()[index])
                )
            }
        }
    }
}