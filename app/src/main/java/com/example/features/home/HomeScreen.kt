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
import androidx.compose.material.icons.filled.Event
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.withFrameMillis
import kotlin.math.pow
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
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith

private val demoIds = setOf("1", "2", "3", "4")

private fun isUserTask(task: Task): Boolean {
  if (task.id in demoIds) return false
  val title = task.title.lowercase(Locale.US)
  val desc = task.description.lowercase(Locale.US)
  if (title.contains("demo") || title.contains("sample") || title.contains("template") || title.contains("hidden")) return false
  if (desc.contains("demo") || desc.contains("sample") || desc.contains("template") || desc.contains("hidden")) return false
  return true
}

@Composable
fun HomeScreen(navController: NavController) {
  androidx.compose.runtime.remember {
    com.example.debug.StartupTracer.mark("HOME_SCREEN_CREATED")
    Unit
  }
  androidx.compose.runtime.SideEffect {
    com.example.debug.StartupTracer.mark("HOME_SCREEN_FIRST_COMPOSITION")
  }
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val current = navBackStackEntry?.destination?.route

  var currentDayName by remember { mutableStateOf("") }
  var currentDate by remember { mutableStateOf("") }
  var currentTime by remember { mutableStateOf("") }

  val context = androidx.compose.ui.platform.LocalContext.current
  val taskStore = remember { TaskLocalStore(context) }
  val tasksList by TaskLocalStore.tasksFlow.collectAsState()

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
    updatedTasksList.filter { it.targetDate == today && isUserTask(it) }
  }

  val totalTasksCount = remember(todaysTasks) { todaysTasks.size }
  val completedTasksCount = remember(todaysTasks) { todaysTasks.count { it.isCompleted } }
  val pendingTasksCount = remember(todaysTasks) { todaysTasks.count { !it.isCompleted } }

  LaunchedEffect(Unit) {
    val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    while (true) {
      val calendar = Calendar.getInstance()

      currentDayName = dayFormat.format(calendar.time)
      currentDate = dateFormat.format(calendar.time)
      currentTime = timeFormat.format(calendar.time)

      delay(1000)
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize().testTag("home_screen_root"),
    bottomBar = {
      NavigationBar {
          NavigationBarItem(
              selected = current == Screen.Home.route,
              modifier = Modifier.testTag("nav_tab_home"),
              onClick = {
                  navController.navigate(Screen.Home.route) {
                      popUpTo(Screen.Home.route) { inclusive = true }
                      launchSingleTop = true
                  }
              },
              icon = {
                  Icon(Icons.Default.Home, null)
              },
              label = {
                  Text(stringResource(R.string.home), style = MaterialTheme.typography.labelSmall)
              }
          )

          NavigationBarItem(
              selected = current == Screen.Tasks.route,
              modifier = Modifier.testTag("nav_tab_tasks"),
              onClick = {
                  navController.navigate(Screen.Tasks.route) {
                      popUpTo(Screen.Home.route) { saveState = true }
                      launchSingleTop = true
                      restoreState = true
                  }
              },
              icon = {
                  Icon(Icons.Default.CheckCircle, null)
              },
              label = {
                  Text(stringResource(R.string.today_tasks), style = MaterialTheme.typography.labelSmall)
              }
          )

          NavigationBarItem(
              selected = current == Screen.TomorrowTasks.route,
              modifier = Modifier.testTag("nav_tab_tomorrow_tasks"),
              onClick = {
                  navController.navigate(Screen.TomorrowTasks.route) {
                      popUpTo(Screen.Home.route) { saveState = true }
                      launchSingleTop = true
                      restoreState = true
                  }
              },
              icon = {
                  Icon(Icons.Default.Event, null)
              },
              label = {
                  Text(stringResource(R.string.tomorrow_tasks), style = MaterialTheme.typography.labelSmall)
              }
          )

          NavigationBarItem(
              selected = current == Screen.Settings.route,
              modifier = Modifier.testTag("nav_tab_settings"),
              onClick = {
                  navController.navigate(Screen.Settings.route) {
                      popUpTo(Screen.Home.route) { saveState = true }
                      launchSingleTop = true
                      restoreState = true
                  }
              },
              icon = {
                  Icon(Icons.Default.Settings, null)
              },
              label = {
                  Text(stringResource(R.string.settings), style = MaterialTheme.typography.labelSmall)
              }
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
          // Jewel logo with the same identity as the splash screen
          SmallDiamondRenderer(
            modifier = Modifier.size(72.dp)
          )

          Spacer(modifier = Modifier.height(4.dp))

          // App Name with Task Manager Logo
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "الذكي",
              style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 22.sp
              )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
              text = "|",
              style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                fontSize = 24.sp
              )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Task Manager Logo: Sheet of paper + checkmark
            SmallTaskManagerLogo(
              modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(10.dp))
            
            Text(
              text = stringResource(R.string.app_name),
              style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 26.sp
              )
            )
          }
          
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
      AnimatedContent(
        targetState = value,
        transitionSpec = {
          slideInVertically { height -> height } + fadeIn() togetherWith
              slideOutVertically { height -> -height } + fadeOut()
        },
        label = "stat_value_anim"
      ) { targetValue ->
        Text(
          text = targetValue,
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 24.sp
          )
        )
      }
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

private data class HomePoint3D(val x: Float, val y: Float, val z: Float)
private data class HomeFacet(val indices: IntArray)
private class HomePointF(val x: Float, val y: Float)

@Composable
fun SmallDiamondRenderer(modifier: Modifier = Modifier) {
    val timeMs = remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        val startTime = withFrameMillis { it }
        while (true) {
            withFrameMillis { frameTime ->
                timeMs.value = frameTime - startTime
            }
        }
    }

    val tableRadius = 0.50f
    val girdleRadius = 0.95f
    val tableY = 0.35f
    val girdleY = 0.08f
    val culetY = -0.78f

    // Vertices definition
    val vertices = remember {
        val tablePoints = List(8) { i ->
            val angle = (i * Math.PI / 4.0).toFloat()
            HomePoint3D(tableRadius * kotlin.math.cos(angle), tableY, tableRadius * kotlin.math.sin(angle))
        }
        val girdlePoints = List(16) { j ->
            val angle = (j * Math.PI / 8.0).toFloat()
            HomePoint3D(girdleRadius * kotlin.math.cos(angle), girdleY, girdleRadius * kotlin.math.sin(angle))
        }
        val culetPoint = HomePoint3D(0f, culetY, 0f)
        tablePoints + girdlePoints + listOf(culetPoint)
    }

    // Facets definition
    val facets = remember {
        val list = mutableListOf<HomeFacet>()
        
        // 1. Table facet (Octagon)
        list.add(HomeFacet(intArrayOf(0, 1, 2, 3, 4, 5, 6, 7)))

        // 2. Crown Star Triangles (8 facets)
        for (i in 0..7) {
            list.add(HomeFacet(intArrayOf(i, (i + 1) % 8, 8 + (2 * i + 1) % 16)))
        }

        // 3. Crown Kite left & right triangles (16 facets)
        for (i in 0..7) {
            list.add(HomeFacet(intArrayOf(i, 8 + (2 * i + 1) % 16, 8 + (2 * i) % 16)))
            list.add(HomeFacet(intArrayOf(i, 8 + (2 * i) % 16, 8 + (2 * i - 1 + 16) % 16)))
        }

        // 4. Pavilion triangles (16 facets)
        for (j in 0..15) {
            list.add(HomeFacet(intArrayOf(8 + j, 24, 8 + (j + 1) % 16)))
        }

        list.toList()
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val scale = kotlin.math.min(width, height) * 0.48f
        val centerX = width / 2f
        val centerY = height / 2f
        val cameraD = 3.0f

        val t = timeMs.value
        // Very slow Y-axis rotation
        val angleY = (t % 10000) / 10000f * 2f * Math.PI.toFloat()
        // Constant slight X-axis tilt
        val angleX = -0.32f

        val cosY = kotlin.math.cos(angleY)
        val sinY = kotlin.math.sin(angleY)
        val cosX = kotlin.math.cos(angleX)
        val sinX = kotlin.math.sin(angleX)

        // Rotate vertices in 3D
        val rotatedVertices = vertices.map { pt ->
            val x1 = pt.x * cosY + pt.z * sinY
            val y1 = pt.y
            val z1 = -pt.x * sinY + pt.z * cosY

            val rx = x1
            val ry = y1 * cosX - z1 * sinX
            val rz = y1 * sinX + z1 * cosX

            HomePoint3D(rx, ry, rz)
        }

        // Project vertices to 2D
        val projectedVertices = rotatedVertices.map { pt ->
            val factor = cameraD / (cameraD - pt.z)
            val px = centerX + pt.x * factor * scale
            val py = centerY - pt.y * factor * scale
            HomePointF(px, py)
        }

        // Draw soft professional halo glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x35FFFFFF),
                    Color(0x1200E5FF),
                    Color.Transparent
                ),
                center = androidx.compose.ui.geometry.Offset(centerX, centerY),
                radius = scale * 1.6f
            ),
            radius = scale * 1.6f,
            center = androidx.compose.ui.geometry.Offset(centerX, centerY)
        )

        // Calculate face normals and average Z for depth sorting
        val sortedFacetsWithDepth = facets.map { facet ->
            val avgZ = facet.indices.map { rotatedVertices[it].z }.average().toFloat()
            facet to avgZ
        }.sortedBy { it.second }

        // Light sources in 3D
        val l1x = 0.3f; val l1y = 0.5f; val l1z = 0.8f
        val l2x = -0.8f; val l2y = 0.2f; val l2z = 0.5f
        val l3x = 0.5f; val l3y = -0.6f; val l3z = -0.3f

        val sweepAngle = (t % 4000) / 4000f * 2f * Math.PI.toFloat()
        val lsx = kotlin.math.cos(sweepAngle)
        val lsy = 0.3f
        val lsz = kotlin.math.sin(sweepAngle)

        sortedFacetsWithDepth.forEach { (facet, _) ->
            val indices = facet.indices

            val p0 = rotatedVertices[indices[0]]
            val p1 = rotatedVertices[indices[1]]
            val p2 = rotatedVertices[indices[2]]

            val ux = p1.x - p0.x
            val uy = p1.y - p0.y
            val uz = p1.z - p0.z

            val vx = p2.x - p0.x
            val vy = p2.y - p0.y
            val vz = p2.z - p0.z

            var nx = uy * vz - uz * vy
            var ny = uz * vx - ux * vz
            var nz = ux * vy - uy * vx

            val len = kotlin.math.sqrt(nx * nx + ny * ny + nz * nz)
            if (len > 1e-6f) {
                nx /= len
                ny /= len
                nz /= len
            } else {
                nx = 0f; ny = 0f; nz = 1f
            }

            val dot1 = kotlin.math.abs(nx * l1x + ny * l1y + nz * l1z)
            val dot2 = kotlin.math.abs(nx * l2x + ny * l2y + nz * l2z)
            val dot3 = kotlin.math.abs(nx * l3x + ny * l3y + nz * l3z)
            val dotSweep = kotlin.math.abs(nx * lsx + ny * lsy + nz * lsz)

            val spec = nz.pow(16f)
            val specSweep = dotSweep.pow(24f)

            val dispersion = kotlin.math.abs(kotlin.math.sin(nx * 3.5f + ny * 3.5f + t * 0.0015f))
            val fireR = 0.12f * kotlin.math.sin(dispersion * Math.PI.toFloat()).coerceIn(0f, 1f)
            val fireG = 0.10f * kotlin.math.sin((dispersion + 0.33f) * Math.PI.toFloat()).coerceIn(0f, 1f)
            val fireB = 0.15f * kotlin.math.sin((dispersion + 0.66f) * Math.PI.toFloat()).coerceIn(0f, 1f)

            val r = (0.05f + 0.55f * dot1 + 0.10f * dot2 + 0.05f * dot3 + spec * 0.30f + specSweep * 0.40f + fireR).coerceIn(0f, 1f)
            val g = (0.08f + 0.55f * dot1 + 0.60f * dot2 + 0.10f * dot3 + spec * 0.30f + specSweep * 0.45f + fireG).coerceIn(0f, 1f)
            val b = (0.22f + 0.55f * dot1 + 0.30f * dot2 + 0.80f * dot3 + spec * 0.40f + specSweep * 0.45f + fireB).coerceIn(0f, 1f)

            val alpha = (0.50f + 0.25f * dot1 + specSweep * 0.20f).coerceIn(0.2f, 0.95f)

            val fillColor = Color(r, g, b, alpha)
            val strokeColor = Color(
                (0.85f + specSweep * 0.15f).coerceIn(0f, 1f),
                (0.92f + specSweep * 0.08f).coerceIn(0f, 1f),
                1.0f,
                (0.20f + specSweep * 0.45f).coerceIn(0f, 1f)
            )

            val path = Path().apply {
                val fPt = projectedVertices[indices[0]]
                moveTo(fPt.x, fPt.y)
                for (k in 1 until indices.size) {
                    val pt = projectedVertices[indices[k]]
                    lineTo(pt.x, pt.y)
                }
                close()
            }

            drawPath(path = path, color = fillColor)
            drawPath(path = path, color = strokeColor, style = Stroke(width = 1.dp.toPx()))
        }

        // Glimmers
        for (i in 0..7) {
            val zCoord = rotatedVertices[i].z
            if (zCoord > 0.15f) {
                val vertexPhase = (t + i * 750) % 2500
                if (vertexPhase < 600) {
                    val intensity = kotlin.math.sin((vertexPhase / 600f) * Math.PI.toFloat())
                    val pt = projectedVertices[i]
                    val sparkleSize = 8.dp.toPx() * intensity
                    val glowRadius = 5.dp.toPx() * intensity

                    drawCircle(
                        color = Color(0x75FFFFFF),
                        radius = glowRadius,
                        center = androidx.compose.ui.geometry.Offset(pt.x, pt.y)
                    )
                    drawLine(
                        color = Color.White,
                        start = androidx.compose.ui.geometry.Offset(pt.x - sparkleSize, pt.y),
                        end = androidx.compose.ui.geometry.Offset(pt.x + sparkleSize, pt.y),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = Color.White,
                        start = androidx.compose.ui.geometry.Offset(pt.x, pt.y - sparkleSize),
                        end = androidx.compose.ui.geometry.Offset(pt.x, pt.y + sparkleSize),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
        }
    }
}

@Composable
fun SmallTaskManagerLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Paper sheet
        val px0 = w * 0.20f
        val px1 = w * 0.80f
        val py0 = h * 0.14f
        val py1 = h * 0.86f

        val foldX = w * 0.58f
        val foldY = h * 0.64f

        val paperPath = Path().apply {
            moveTo(px0, py0)
            lineTo(px1, py0)
            lineTo(px1, foldY)
            lineTo(foldX, py1)
            lineTo(px0, py1)
            close()
        }

        val paperGradient = Brush.linearGradient(
            colors = listOf(Color(0x3EFFFFFF), Color(0x1AFFFFFF)),
            start = androidx.compose.ui.geometry.Offset(px0, py0),
            end = androidx.compose.ui.geometry.Offset(px1, py1)
        )
        drawPath(path = paperPath, brush = paperGradient)

        val borderGradient = Brush.linearGradient(
            colors = listOf(Color(0x80FFFFFF), Color(0x3000E5FF)),
            start = androidx.compose.ui.geometry.Offset(px0, py0),
            end = androidx.compose.ui.geometry.Offset(px1, py1)
        )
        drawPath(path = paperPath, brush = borderGradient, style = Stroke(width = 1.2.dp.toPx()))

        // 2. Crease
        val foldPath = Path().apply {
            moveTo(px1, foldY)
            lineTo(foldX, foldY)
            lineTo(foldX, py1)
            close()
        }
        drawPath(path = foldPath, color = Color(0x40FFFFFF))
        drawPath(path = foldPath, color = Color(0x80FFFFFF), style = Stroke(width = 0.8.dp.toPx()))

        // 3. Glowing Checkmark
        val checkPath = Path().apply {
            moveTo(w * 0.32f, h * 0.50f)
            lineTo(w * 0.46f, h * 0.64f)
            lineTo(w * 0.72f, h * 0.32f)
        }

        drawPath(
            path = checkPath,
            color = Color(0x3F00E5FF),
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        
        val checkGradient = Brush.linearGradient(
            colors = listOf(Color(0xFFE0F7FA), Color(0xFF00E5FF)),
            start = androidx.compose.ui.geometry.Offset(w * 0.32f, h * 0.50f),
            end = androidx.compose.ui.geometry.Offset(w * 0.72f, h * 0.32f)
        )
        drawPath(
            path = checkPath,
            brush = checkGradient,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

