package com.example.snakegame

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.magnifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.preferencesDataStore
import com.example.snakegame.ui.theme.SnakeGameTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val Context.dataStore by preferencesDataStore("timeData")


    private val vm: TimeViewModel by viewModels {
        ViewModelFactory(dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SnakeGameTheme {
                // A surface container using the 'background' color from the theme

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    StopWatch(vm)
                }
            }
        }
    }
}


@Composable
fun StopWatch(
    viewModel: TimeViewModel,
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current

    var timeMills by remember {
        mutableStateOf(-1L)
    }

    var lapCount by remember {
        mutableStateOf(0)
    }

    var isClockRunning by remember {
        mutableStateOf(false)
    }

    val dataList by viewModel.getLapTime().collectAsState(initial = emptyList())

    Box(modifier = modifier.fillMaxSize()) {

        LaunchedEffect(isClockRunning) {
            while (isClockRunning) {
                delay(10L)
                timeMills += 10L
            }
        }

        Column {
            Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

                Text(
                    text = formatTime(timeMills),
                    modifier.padding(20.dp),
                    style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.primary)
                )

            }
            Spacer(modifier = modifier.height(80.dp))

            LazyColumn(
                modifier
                    .fillMaxWidth()
                    .height(250.dp),horizontalAlignment = Alignment.CenterHorizontally) {
                items(dataList) { data ->
                    data?.toLongOrNull()?.let { time ->
                        Text(
                            text = formatTime(time),
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }


        Row(
            modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.Bottom
        ) {


            IconButton(
                onClick = {
                    lapCount++
                    if (isClockRunning){
                        viewModel.updateLapTime(timeMills, lapCount.toString())
                    }
                },
                modifier
                    .height(50.dp).alpha(if(isClockRunning) 1f else 0f),
                enabled = isClockRunning
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.flag),
                    contentDescription = "Flag",
                    modifier.height(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))

            IconButton(
                onClick = {
                    isClockRunning = !isClockRunning
                },
                modifier
                    .height(50.dp)

            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start")
            }

            Spacer(modifier = Modifier.width(10.dp))

            IconButton(
                onClick = {
                    timeMills = -1L
                    lapCount = 0
                    isClockRunning = false
                    viewModel.clearAll()
                },
                modifier
                    .size(40.dp).alpha(if(isClockRunning) 1f else 0f),
                enabled = isClockRunning
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "reset")
            }


        }

    }
}

fun formatTime(timeInMills: Long): String {
    val seconds = (timeInMills / 1000) % 60
    val minutes = (timeInMills / 1000) / 60
    val milliSeconds = (timeInMills % 1000) / 10
    return String.format("%02d : %02d : %02d", minutes, seconds, milliSeconds)
}



