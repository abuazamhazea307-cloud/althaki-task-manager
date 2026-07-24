package com.example.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navigation.Screen
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.delay
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.features.tasks.TaskLocalStore
import com.example.features.tasks.Task
import com.example.features.tasks.getCurrentDateString
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun HomeScreen(navController: NavController) {
  var currentDayName by remember { mutableStateOf("") }
  var currentDate by remember { mutableStateOf("") }
  var currentTime by remember { mutableStateOf("") }

  val context = androidx.compose.ui.platform.LocalContext.current
  val taskStore = remember { TaskLocalStore(context) }
  var tasksList by remember { mutableStateOf<List<Task>>(emptyList()) }

  val navBackStackEntry by navController.currentBackStackEntryAsState()
  LaunchedEffect(navBackStackEntry) {
    tasksList = taskStore.loadTasks() ?: emptyList()
  }

  val today = getCurrentDateString()

  val updatedTasksList = remember(tasksList, today) {
    tasksList.map { task ->
      if (!task.isCompleted && task.targetDate < today) {
        task.copy(targetDate = today, isRolledOver = true)
      } else {
        task
      }
    }
  }

  val todaysTasks = remember(updatedTasksList, today) {
    updatedTasksList.filter { it.targetDate == today }
  }

  val totalTasksCount = todaysTasks.size
  val completedTasksCount = todaysTasks.count { it.isCompleted }
  val pendingTasksCount = todaysTasks.count { !it.isCompleted }

  LaunchedEffect(Unit) {
    while (true) {
      val calendar = Calendar.getInstance()
      val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
      val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
      val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())

      currentDayName = dayFormat.format(calendar.time)
      currentDate = dateFormat.format(calendar.time)
      currentTime = timeFormat.format(calendar.time)

      delay(1000)
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize().testTag("home_screen_root"),
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
      ) {
        NavigationBarItem(
          selected = true,
          onClick = { /* Already on Home */ },
          icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.nav_home)) },
          label = { Text(stringResource(R.string.nav_home), style = MaterialTheme.typography.labelSmall) }
        )
        NavigationBarItem(
          selected = false,
          onClick = { navController.navigate(Screen.Tasks.route) },
          icon = { Icon(Icons.Default.List, contentDescription = stringResource(R.string.nav_tasks)) },
          label = { Text(stringResource(R.string.nav_tasks), style = MaterialTheme.typography.labelSmall) }
        )
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(paddingValues)
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {


      // Elegant Day, Date and Time Card (M-002)
      Card(
        modifier = Modifier.fillMaxWidth().testTag("datetime_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // App Series Name, Logo & Settings button (B-001)
          Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = stringResource(R.string.series_title),
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 22.sp
              )
            )

            IconButton(
              onClick = { navController.navigate(Screen.Settings.route) },
              modifier = Modifier
                .align(Alignment.CenterEnd)
                .testTag("home_settings_button")
            ) {
              Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.settings_title),
                tint = MaterialTheme.colorScheme.primary
              )
            }
          }
          
          Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.bodyLarge.copy(
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
              fontSize = 14.sp
            )
          )
          
          androidx.compose.material3.HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f),
            thickness = 1.dp
          )
          
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = stringResource(R.string.label_day),
                style = MaterialTheme.typography.bodyLarge.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                  fontSize = 15.sp
                )
              )
              Text(
                text = currentDayName,
                style = MaterialTheme.typography.bodyLarge.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface,
                  fontSize = 15.sp
                )
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = stringResource(R.string.label_date),
                style = MaterialTheme.typography.bodyLarge.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                  fontSize = 15.sp
                )
              )
              Text(
                text = currentDate,
                style = MaterialTheme.typography.bodyLarge.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface,
                  fontSize = 15.sp
                )
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = stringResource(R.string.label_time),
                style = MaterialTheme.typography.bodyLarge.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                  fontSize = 15.sp
                )
              )
              Text(
                text = currentTime,
                style = MaterialTheme.typography.bodyLarge.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                  fontSize = 15.sp
                )
              )
            }
          }
        }
      }



      // Statistics Section Title
      Text(
        text = stringResource(R.string.stats_title),
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        ),
        modifier = Modifier.padding(top = 8.dp)
      )

      // Grid of stats cards
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        StatCard(
          title = stringResource(R.string.stat_total_tasks),
          value = totalTasksCount.toString(),
          icon = Icons.Default.FormatListBulleted,
          iconColor = MaterialTheme.colorScheme.primary,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = stringResource(R.string.stat_in_progress),
          value = pendingTasksCount.toString(),
          icon = Icons.Default.PendingActions,
          iconColor = MaterialTheme.colorScheme.tertiary,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = stringResource(R.string.stat_completed_tasks),
          value = completedTasksCount.toString(),
          icon = Icons.Default.CheckCircle,
          iconColor = Color(0xFF10B981), // Healthy green
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Direct actions section
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Text(
            text = stringResource(R.string.action_section_title),
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          )

          Text(
            text = stringResource(R.string.action_section_desc),
            style = MaterialTheme.typography.bodyLarge.copy(
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
              lineHeight = 18.sp
            )
          )

          Button(
            onClick = { navController.navigate(Screen.Tasks.route) },
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("go_to_tasks_button"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(
              text = stringResource(R.string.btn_go_to_tasks),
              style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = stringResource(R.string.btn_enter_desc),
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun StatCard(
  title: String,
  value: String,
  icon: ImageVector,
  iconColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .background(iconColor.copy(alpha = 0.1f), shape = CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = iconColor,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 24.sp
        )
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
          color = MaterialTheme.colorScheme.secondary,
          fontSize = 11.sp
        ),
        textAlign = TextAlign.Center
      )
    }
  }
}
