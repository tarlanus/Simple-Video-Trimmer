package com.tarlanus.simplevideotrimmer.presentation.OutPut.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import com.tarlanus.simplevideotrimmer.domain.MediaDetails
import com.tarlanus.simplevideotrimmer.presentation.Main.viewstates.ViewStateMain
import com.tarlanus.simplevideotrimmer.presentation.OutPut.viewmodels.ViewModelOutput
import com.tarlanus.simplevideotrimmer.presentation.exocontroller.ExoPreview
import com.tarlanus.simplevideotrimmer.presentation.exocontroller.ViewStateExo
import com.tarlanus.simplevideotrimmer.ui.theme.AccentGreen
import com.tarlanus.simplevideotrimmer.ui.theme.AccentYellow

@Composable
fun OutputScreen(
    onClick: () -> Unit,
    outputPath: String,
    viewModelOutput: ViewModelOutput = hiltViewModel()
) {
    val details = remember { mutableStateOf(MediaDetails()) }
    val uiState = viewModelOutput.viewStateUI.collectAsStateWithLifecycle()

    val exoState = viewModelOutput.viewStateExo.collectAsStateWithLifecycle()




    LaunchedEffect(Unit) {
        viewModelOutput?.clearMediaJob()

    }
    DisposableEffect(Unit) {
        onDispose {
            viewModelOutput?.clearMediaJob()

        }
    }

    LaunchedEffect(outputPath) {
        viewModelOutput.retrieveMediaMetaData(outputPath)

    }

    val lOwner = LocalLifecycleOwner.current

    val lifeSyscle = lOwner.lifecycle

    DisposableEffect(lOwner) {

        val eventObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {

                }

                Lifecycle.Event.ON_START -> {

                    viewModelOutput.setStartBuildExolayer()

                }

                Lifecycle.Event.ON_RESUME -> {


                }

                Lifecycle.Event.ON_PAUSE -> {


                }

                Lifecycle.Event.ON_STOP -> {
                    viewModelOutput.stopExoBuildPlayer()

                }

                Lifecycle.Event.ON_DESTROY -> {


                }

                Lifecycle.Event.ON_ANY -> {

                }
            }

        }
        lifeSyscle.addObserver(eventObserver)
        onDispose {
            lifeSyscle.removeObserver(eventObserver)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AccentYellow),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Spacer(modifier = Modifier.height(15.dp))


        Box(
            modifier = Modifier
                .padding(all = 30.dp)
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.Black)
        ) {
            val getUiStateValue = uiState.value
            val getExoState = exoState.value

            when (getUiStateValue) {
                ViewStateMain.ERROR -> {
                    Text(
                        "An error occured!",
                        fontSize = 20.sp,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )

                }

                ViewStateMain.IDLE -> {
                    details.value = MediaDetails()


                }

                ViewStateMain.LOADING -> {

                    CircularProgressIndicator(color = AccentYellow)

                }

                is ViewStateMain.SUCCESS -> {


                    val getVideoData = getUiStateValue.mediaDetails
                    details.value = getVideoData


                }
            }

            when (getExoState) {
                is ViewStateExo.EXO -> {
                    val player = getExoState.exoPlayer
                    LaunchedEffect(player) {
                        player?.prepare()
                        player?.seekTo(0L)
                        player?.playWhenReady = true
                    }

                    DisposableEffect(Unit) {
                        onDispose {
                            player?.playWhenReady = false
                            player?.stop()
                        }
                    }
                    if (player != null) {
                        ExoPreview(player)

                    }

                }

                ViewStateExo.IDLE -> {
                    Box(
                        modifier = Modifier
                            .padding(all = 30.dp)
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {}

                }
            }


        }
        Text(
            details.value.mediaDetails,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(15.dp))
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        ) {
            Text(
                details.value.name,
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                color = Color.Black
            )
            Text(
                details.value.size,
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                color = Color.Black
            )
            Text(
                details.value.resolution,
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                color = Color.Black
            )
            Text(
                details.value.path,
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                color = Color.Black
            )
            Text(
                details.value.bitrate,
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                color = Color.Black
            )
            Text(
                details.value.frameRate,
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                color = Color.Black
            )
            Text(
                details.value.duration,
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(15.dp))
        Button(
            onClick = {

                onClick()

            },
            modifier = Modifier.padding(horizontal = 15.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentGreen,
                contentColor = Color.Gray.copy(alpha = 1f)
            )
        ) {
            Text(
                "GO BACK",
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                color = Color.Black
            )

        }


    }


}

@Composable
@Preview(showBackground = true)
fun PreviewOutputScreen() {
    val details = MediaDetails()

    OutputScreen(onClick = {}, "")
}