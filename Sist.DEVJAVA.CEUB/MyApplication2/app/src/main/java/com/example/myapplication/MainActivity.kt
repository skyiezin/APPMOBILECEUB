package com.example.myapplication

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.ui.navigation.AppNavGraph
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.util.*

// Activity principal que configura tema, canal e agendamento de notificações
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Cria o canal de notificações
        createNotificationChannel()
        // Agenda notificação semanal às segundas 9h
        scheduleWeeklyNotification()
        // Define o conteúdo Compose da Activity
        setContent {
            // ViewModel para alternar tema claro/escuro
            val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory(application))
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            // Aplica o tema salvo no dispositivo e configura um botão de teste pras notificações
            MyApplicationTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column {
                        AppNavGraph(themeViewModel)

                        // Botão para emitir notificação manual de teste
                        Button(
                            onClick = { showTestNotification(this@MainActivity) },
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text("🔔 Testar Notificação (Manual)")
                        }
                    }
                }
            }
        }
    }

    // Configura o canal de notificações com ID "monthly_reminder" (inicialmente as notificações seriam mensais)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "FinanceReminderChannel"
            val descriptionText = "Lembra o cliente de cadastrar dados financeiros semanalmente"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("monthly_reminder", name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Agenda repetição de notificação usando AlarmManager
    private fun scheduleWeeklyNotification() {
        // Intent para o BroadcastReceiver
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Obtém serviço de alarme
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Define data de início: próxima segunda às 9h
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (timeInMillis < System.currentTimeMillis()) add(Calendar.WEEK_OF_YEAR, 1)
        }
        // Registra alarme semanal
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )
    }
    // Exibe notificação de teste usando NotificationCompat
    private fun showTestNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, "monthly_reminder")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Notificação de Teste")
            .setContentText("Esta é uma notificação de teste do Planejador Financeiro.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(999, builder.build())
        }
    }
}
