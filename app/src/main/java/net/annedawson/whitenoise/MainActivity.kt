@file:OptIn(ExperimentalMaterial3Api::class)

package net.annedawson.whitenoise

import android.media.MediaPlayer
import android.net.Uri
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity
import android.content.Intent

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
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    // val mediaPlayer = remember { MediaPlayer.create(context, R.raw.whitenoise) }
    var sliderPosition by remember { mutableStateOf(1f) }
    var isLooping by remember { mutableStateOf(true) }
    var selectedAudioUri by remember { mutableStateOf<Uri?>(null) } // Store URI


    // Set looping based on the state
    mediaPlayer?.isLooping = isLooping

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row( // Row for the switch
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Looping")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = isLooping,
                onCheckedChange = { isChecked ->
                    isLooping = isChecked
                    mediaPlayer?.isLooping = isChecked // Update looping
                }
            )
        }

        LaunchedEffect(selectedAudioUri) {
            if (selectedAudioUri != null) {
                mediaPlayer?.release() // Release any existing MediaPlayer

                // Create and prepare the MediaPlayer for the selected URI
                mediaPlayer = MediaPlayer.create(context, selectedAudioUri).apply {
                    isLooping = isLooping
                    setVolume(sliderPosition, sliderPosition)
                    // Prepare the MediaPlayer asynchronously (optional but recommended)
                    // prepareAsync()
                }
            }
        }

        Button(
            onClick = {
                if (mediaPlayer != null) {
                    if (isPlaying) {
                        mediaPlayer?.pause()
                    } else {
                        mediaPlayer?.start()
                    }
                    isPlaying = !isPlaying
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(if (isPlaying) "Pause" else "Play")
        }


        Slider( // Volume slider
            value = sliderPosition,
            onValueChange = { newPosition ->
                sliderPosition = newPosition
                mediaPlayer?.setVolume(newPosition, newPosition) // Set volume
            },
            modifier = Modifier.padding(16.dp)
        )

        

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedAudioUri = result.data?.data
            }
        }

        Button(onClick = {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "audio/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            launcher.launch(intent) // Launch the intent using the launcher
        }) {
            Text("Select Audio")
        }

    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }
}