package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ThemeViewModel
import com.example.myapplication.ui.screens.*

    //Função que define a navegação entre as telas
@Composable
fun AppNavGraph(themeViewModel: ThemeViewModel) {
    // Cria o controlador de navegação
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login" // tela inicial do app
    ) {
        // Rota de login, recebe ThemeViewModel para troca de tema
        composable("login") {
            LoginScreen(navController, themeViewModel)
        }
        // Rota de cadastro de usuário
        composable("register") {
            RegisterScreen(navController)
        }
        // Rota principal de inserção de dados financeiros
        composable("finance") {
            FinanceDataScreen(navController)
        }
        // Rota de perfil, permite alteração de tema
        composable("profile") {
            ProfileScreen(navController, themeViewModel)
        }
        // Rota de resultado com parâmetro mês/ano
        composable("resultado/{mesAno}") { back ->
            val mesAno = back.arguments?.getString("mesAno") ?: ""
            ResultadoFinanceiroScreen(navController, mesAno)
        }
        // Lista de meses cadastrados
        composable("meses") {
            MesesAnterioresScreen(navController)
        }
        // Rota de edição de mês específico, recebe mês/ano
        composable("editar/{mesAno}") { back ->
            val mesAno = back.arguments?.getString("mesAno") ?: ""
            EditarMesScreen(navController, mesAno)
        }
        // Rota para editar perfil do usuário
        composable("editar_perfil") {
            EditarPerfilScreen(navController)
        }
        // Rota para tela de gráfico de gastos mensais
        composable("graficoGastos") {
            GraficoGastosScreen(navController)
        }
        // Rota para relatório anual de gastos
        composable("relatorioAnual") {
            RelatorioAnualScreen(navController)
        }
    }
}
