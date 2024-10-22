@file:OptIn(ExperimentalMaterial3Api::class)

package net.annedawson.whitenoise

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhiteNoiseApp()
        }
    }
}


@Composable
fun WhiteNoiseApp() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("White Noise") })
        }
    ) { innerPadding ->
        WhiteNoisePlayer(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun WhiteNoisePlayer(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.whitenoise) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (isPlaying) {
                    mediaPlayer.pause()
                } else {
                    mediaPlayer.start()
                }
                isPlaying = !isPlaying
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(if (isPlaying) "Pause" else "Play")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }
}