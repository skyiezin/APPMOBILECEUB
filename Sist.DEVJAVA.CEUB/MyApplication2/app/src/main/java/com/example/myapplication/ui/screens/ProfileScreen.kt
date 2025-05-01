package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.UserPreferences
import com.example.myapplication.ThemeViewModel

// Tela de perfil do usuário, exibindo os dados/edição deles.

@Composable
fun ProfileScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel
) {
    val context = LocalContext.current
    val userPrefs = UserPreferences(context)
    // Estado do tema (claro/escuro) vindo do ViewModel
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    // Identifica usuário atual e traz os dados relacionados à ele.
    val loggedUser = userPrefs.getLoggedUser()
    val user = userPrefs.getUser(loggedUser)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Exibe uma imagem generica de perfil.
        Image(
            painter = painterResource(id = R.drawable.profile_placeholder),
            contentDescription = "Imagem de perfil",
            modifier = Modifier
                .size(200.dp)
                .padding(top = 24.dp)
        )

        Spacer(modifier = Modifier.height(240.dp)) // Espaço para layout responsivo

        // Exibe os dados do usuário
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Text(
                "Nome: ${user["name"]}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                "Telefone: ${user["phone"]}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                "Endereço: ${user["address"]}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Senha mascarada para não expor valor real, sempre com 8 caracteres pra ficar generica
            Text(
                "Senha: ${"•".repeat(8)}",
                style = MaterialTheme.typography.titleLarge
            )
        }

        // Botões de voltar/logout pra trocar de conta
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Voltar")
            }
            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Logout")
            }
        }

        // Botão de edição de perfil
        Button(
            onClick = { navController.navigate("editar_perfil") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Editar Perfil")
        }

        // Botão para alternar tema claro/escuro
        Button(
            onClick = { themeViewModel.toggleTheme() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(
                if (isDarkTheme) "Alternar para Tema Claro"
                else "Alternar para Tema Escuro"
            )
        }
    }
}
