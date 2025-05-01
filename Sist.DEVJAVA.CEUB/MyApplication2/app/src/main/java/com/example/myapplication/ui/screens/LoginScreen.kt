package com.example.myapplication.ui.screens

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.UserPreferences
import com.example.myapplication.ThemeViewModel
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

// Tela de login onde usuário insere credenciais
// Tem também um botao pra testar as notificações

@Composable
fun LoginScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel
) {

    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }

    // Usuário e senha
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    // Estado do tema claro/escuro vindo do ViewModel
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Boas Vindas
        Text(
            text = "Bem-vindo ao Planejador Financeiro",
            style = MaterialTheme.typography.headlineSmall
        )

        // Campo de usuário
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuário") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo de senha com máscara
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        // Botões de ação: Login e Cadastrar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    // Verifica credenciais
                    val user = userPrefs.getUser(username)
                    when {
                        user["name"].isNullOrBlank() ->
                            Toast.makeText(context, "Usuário não encontrado!", Toast.LENGTH_SHORT).show()
                        password != user["password"] ->
                            Toast.makeText(context, "Senha incorreta!", Toast.LENGTH_SHORT).show()
                        else -> {
                            // Salva usuário logado e navega para tela financeira
                            userPrefs.saveLoggedUser(username)
                            Toast.makeText(context, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                            navController.navigate("finance")
                        }
                    }
                }
            ) { Text("Login") }

            Button(
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("register") }
            ) { Text("Cadastrar") }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Alterna tema claro/escuro
        Button(
            onClick = { themeViewModel.toggleTheme() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isDarkTheme) "Alternar para Tema Claro" else "Alternar para Tema Escuro")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão de teste de notificação manual, feito para debugar durante o desenvolvimento
        Button(
            onClick = {
                val channelId = "monthly_reminder"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        channelId,
                        "Lembretes Mensais",
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply { description = "Canal de lembretes mensais" }
                    (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                        .createNotificationChannel(channel)
                }
                // Constrói e exibe notificação de teste
                val notif = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)
                    .setContentTitle("🔔 Notificação de Teste")
                    .setContentText("Esta é uma notificação enviada manualmente.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .build()
                NotificationManagerCompat.from(context).notify(999, notif)
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("🔔 Testar Notificação (Manual)") }
    }
}
