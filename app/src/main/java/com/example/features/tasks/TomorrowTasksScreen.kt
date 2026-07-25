package com.example.features.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.R

@Composable
fun TomorrowTasksScreen(navController: NavController? = null) {

    Scaffold {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = stringResource(R.string.tomorrow_tasks_coming),
                style = MaterialTheme.typography.titleMedium
            )

        }

    }

}
