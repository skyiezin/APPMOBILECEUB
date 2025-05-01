package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.myapplication.FinancePreferences
import com.example.myapplication.UserPreferences
import java.time.Month
import java.time.format.TextStyle
import java.util.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas

   //Tela que exibe o gráfico dos ultimos 12 meses registrados de gastos
@Composable
fun GraficoGastosScreen(navController: NavController) {
    val context = LocalContext.current
    val financePrefs = FinancePreferences(context)
    // Usuário logado
    val loggedUser = remember { UserPreferences(context).getLoggedUser() }
    // Busca todos os registros financeiros do usuário
    val todosMeses = financePrefs.getAllFinanceData(loggedUser)
    // Ordena chaves MM/YY em ordem cronológica, pra garantir que o grafico siga a ordem certa
    val mesesOrdenados = todosMeses.keys.sortedBy {
        runCatching {
            val (mes, ano) = it.split("-").map { part -> part.toInt() }
            ano * 100 + mes
        }.getOrDefault(0)
    }

    // Cria lista de pares (nome do mês, soma de gastos)
    val listaGastos = mesesOrdenados.mapNotNull { mesAno ->
        val dadosMes = todosMeses[mesAno] ?: return@mapNotNull null
        // Soma fixos + supérfluos (ignora emergenciais)
        val gasto = (dadosMes["fixed"] ?: 0.0) + (dadosMes["variable"] ?: 0.0)
        // Converte número do mês em abreviação em português
        val nomeMes = runCatching {
            val numeroMes = mesAno.split("-")[0].toInt()
            Month.of(numeroMes)
                .getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
        }.getOrDefault(mesAno)

        nomeMes to gasto
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Maiores Gastos dos Últimos 12 Meses",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (listaGastos.isEmpty()) {
            // Mostra mensagem caso não haja dados
            Text("Nenhum dado encontrado.", color = Color.Gray)
        } else {
            // Área do gráfico de barras
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Define largura de cada barra e valor máximo para escala
                    val barWidth = size.width / (listaGastos.size * 2)
                    val maxGasto = listaGastos.maxOf { it.second }.takeIf { it > 0 } ?: 1.0

                    // Desenha cada barra e valor no topo
                    drawIntoCanvas { canvas ->
                        listaGastos.forEachIndexed { index, (label, value) ->
                            val barHeight = (value / maxGasto).toFloat() * size.height
                            val barX = index * 2 * barWidth
                            val barY = size.height - barHeight

                            // Barra preenchida em roxo médio
                            drawRect(
                                color = Color(0xFF9C27B0),
                                topLeft = Offset(barX, barY),
                                size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                            )

                            // Texto com valor em roxo escuro acima da barra, pra que fique visivel
                            // Tanto no modo Dark quanto no normal
                            canvas.nativeCanvas.drawText(
                                "R$ %.2f".format(value),
                                barX + barWidth / 2,
                                barY - 8,
                                android.graphics.Paint().apply {
                                    textAlign = android.graphics.Paint.Align.CENTER
                                    textSize = 30f
                                    color = android.graphics.Color.rgb(74, 20, 140)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legenda com abreviações dos meses
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listaGastos.forEach { (label, _) ->
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
