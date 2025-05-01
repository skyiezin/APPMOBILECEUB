package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.UserPreferences


 //Tela para edição dos dados de perfil do usuário.

@Composable
fun EditarPerfilScreen(navController: NavController) {
    // Contexto da Activity atual para acessar recursos e SharedPreferences
    val context = LocalContext.current
    // Instância de UserPreferences para ler/gravar dados de usuário
    val userPrefs = remember { UserPreferences(context) }
    // Usuário atualmente logado
    val currentUser = userPrefs.getLoggedUser()
    // Dados do usuário (nome, telefone, endereço, senha)
    val user = userPrefs.getUser(currentUser)

    // Estados locais que armazenam o valor dos campos de texto
    var nome by remember { mutableStateOf(user["name"] ?: "") }
    var telefone by remember { mutableStateOf(user["phone"] ?: "") }
    var endereco by remember { mutableStateOf(user["address"] ?: "") }
    var senhaAntiga by remember { mutableStateOf("") }
    var novaSenha by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Cabeçalho da tela
        Text("Editar Perfil", style = MaterialTheme.typography.headlineSmall)

        // Campo de texto para o nome
        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo de texto para o telefone
        OutlinedTextField(
            value = telefone,
            onValueChange = { telefone = it },
            label = { Text("Telefone") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        // Campo de texto para o endereço
        OutlinedTextField(
            value = endereco,
            onValueChange = { endereco = it },
            label = { Text("Endereço") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto para senha atual (mascara oculta)
        OutlinedTextField(
            value = senhaAntiga,
            onValueChange = { senhaAntiga = it },
            label = { Text("Senha atual") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        // Campo de texto para nova senha, com uma camada de segurança onde
        // o usuario precisa validar a senha antiga pra seguir com a mudança
        OutlinedTextField(
            value = novaSenha,
            onValueChange = { novaSenha = it },
            label = { Text("Nova senha") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botão para salvar alterações
        Button(
            onClick = {
                // Valida a senha inserida pra autorizar a troca
                val senhaAtual = user["password"] ?: ""
                if (senhaAntiga == senhaAtual) {
                    // Se nova senha não estiver vazia, atualiza-a
                    val senhaFinal = if (novaSenha.isNotBlank()) novaSenha else senhaAtual
                    // Salva os dados atualizados
                    userPrefs.saveUser(
                        currentUser,
                        nome,
                        telefone,
                        endereco,
                        senhaFinal
                    )
                    // Mensagem de sucesso e volta à tela anterior
                    Toast.makeText(context, "Perfil atualizado com sucesso", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                } else {
                    // Mensagem em caso de senha incorreta
                    Toast.makeText(context, "Senha atual incorreta", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Salvar alterações")
        }

        // Botão para voltar sem salvar
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Voltar")
        }
    }
}