package com.tarlanus.simplevideotrimmer.presentation.Trim.screen

import androidx.activity.compose.LocalActivity
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tarlanus.simplevideotrimmer.domain.MediaDetails
import com.tarlanus.simplevideotrimmer.presentation.Trim.viewmodels.ViewModelTrim
import com.tarlanus.simplevideotrimmer.presentation.Trim.viewstates.ViewStateProgress
import com.tarlanus.simplevideotrimmer.presentation.Trim.viewstates.ViewStateTrim
import com.tarlanus.simplevideotrimmer.presentation.exocontroller.ExoPreview
import com.tarlanus.simplevideotrimmer.presentation.exocontroller.ViewStateExo
import com.tarlanus.simplevideotrimmer.ui.theme.LocalCustomAccentColors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrimScreen(
    onClick: (String) -> Unit,
    inputPath: String,
    videoDetails: MediaDetails,
    viewModelTrim: ViewModelTrim = hiltViewModel<ViewModelTrim, ViewModelTrim.Factory>(
        creationCallback = { factory -> factory.create(inputPath) }
    )
) {

    val activity = LocalActivity.current

    val durationSeconds = remember(videoDetails.duration) {
        (videoDetails.duration.filter { it.isDigit() }.toLongOrNull() ?: 0L) / 1000f
    }
    val maxSelectableRange = minOf(durationSeconds, 59f)

    val showDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf<String?>(null) }


    val uiState = viewModelTrim.viewStateTrimUI.collectAsStateWithLifecycle()
    val exoState = viewModelTrim.viewStateExo.collectAsStateWithLifecycle()
    val progressState = viewModelTrim.viewStateProgress.collectAsStateWithLifecycle()


    LaunchedEffect(dialogMessage.value) {
        if (dialogMessage.value?.contains("Error") == true) {
            delay(3000)
            activity?.finishAffinity()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifeCycle = lifecycleOwner.lifecycle

    DisposableEffect(lifecycleOwner) {
        val eventObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {}
                Lifecycle.Event.ON_START -> {

                    viewModelTrim.setStartBuildExolayer()


                }

                Lifecycle.Event.ON_RESUME -> {}
                Lifecycle.Event.ON_PAUSE -> {}
                Lifecycle.Event.ON_STOP -> {
                    viewModelTrim.stopExoBuildPlayer()


                }

                Lifecycle.Event.ON_DESTROY -> {}
                Lifecycle.Event.ON_ANY -> {}
            }

        }
        lifeCycle.addObserver(eventObserver)
        onDispose {
            lifeCycle.removeObserver(eventObserver)
        }

    }

    var startValue by rememberSaveable { mutableFloatStateOf(0f) }
    var endValue by rememberSaveable { mutableFloatStateOf(maxSelectableRange) }

    val sliderState = remember(durationSeconds) {
        RangeSliderState(
            activeRangeStart = startValue,
            activeRangeEnd = endValue,
            valueRange = 0f..durationSeconds
        )
    }
    LaunchedEffect(sliderState.activeRangeStart, sliderState.activeRangeEnd) {
        val currentRange = sliderState.activeRangeEnd - sliderState.activeRangeStart

        if (currentRange > maxSelectableRange) {
            if (sliderState.activeRangeStart != startValue) {
                sliderState.activeRangeEnd = sliderState.activeRangeStart + maxSelectableRange
            } else {
                sliderState.activeRangeStart = sliderState.activeRangeEnd - maxSelectableRange
            }
        }

        startValue = sliderState.activeRangeStart
        endValue = sliderState.activeRangeEnd
    }
    LaunchedEffect(startValue, endValue) {
        val startpositionMs = (startValue * 1000).toLong()
        val endpositionMs = (endValue * 1000).toLong()


        viewModelTrim.setSeek(startpositionMs, endpositionMs)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalCustomAccentColors.current.accentBackground),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Box(
            modifier = Modifier
                .padding(all = 30.dp)
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.Black)
        ) {

            val getUiStateValue = uiState.value
            val getExoState = exoState.value
            val getProgressState = progressState.value

            when (getUiStateValue) {
                ViewStateTrim.ERROR -> {
                    Text(
                        "An error occured!",
                        fontSize = 20.sp,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }

                ViewStateTrim.IDLE -> {}
                ViewStateTrim.LOADING -> {
                    CircularProgressIndicator(color = LocalCustomAccentColors.current.accentBackground)
                }
            }
            when (getExoState) {
                is ViewStateExo.EXO -> {
                    val player = getExoState.exoPlayer



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

            when (getProgressState) {
                is ViewStateProgress.ERROR -> {
                    val getError = getProgressState.errorMSG

                    if (showDialog.value != true) {
                        showDialog.value = true

                    }
                    dialogMessage.value = "Error $getError"


                }

                ViewStateProgress.IDLE -> {

                    showDialog.value = false
                    dialogMessage.value = null


                }

                is ViewStateProgress.PROGRESS -> {
                    val getPercentage = getProgressState.progress
                    if (showDialog.value != true) {
                        showDialog.value = true

                    }
                    dialogMessage.value = getPercentage

                }

                is ViewStateProgress.SUCCESS -> {
                    showDialog.value = false
                    dialogMessage.value = null

                    LaunchedEffect(getProgressState.path) {
                        onClick(getProgressState.path)
                    }
                }
            }

            if (showDialog.value) {
                DialogScreen(dialogMessage)

            }


        }

        Spacer(modifier = Modifier.height(15.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {

            val startpositionMs = (startValue * 1000).toLong()
            val endpositionMs = (endValue * 1000).toLong()

            var setStart = viewModelTrim.getTimeFormat(startpositionMs)
            val setEnd = viewModelTrim.getTimeFormat(endpositionMs)



            setStart = "$setStart  --  "


            Text(setStart, fontSize = 18.sp, textAlign = TextAlign.Start, color =LocalCustomAccentColors.current.accentText)
            Text(setEnd, fontSize = 18.sp, textAlign = TextAlign.Start, color =LocalCustomAccentColors.current.accentText)

        }
        Spacer(modifier = Modifier.height(15.dp))

        RangeSlider(
            state = sliderState,
            modifier = Modifier.padding(horizontal = 48.dp),
            colors = SliderColors(
                thumbColor = LocalCustomAccentColors.current.accentButton,
                activeTickColor = LocalCustomAccentColors.current.accentButton,
                inactiveTickColor = Color.Gray,
                disabledThumbColor = Color.Gray,
                disabledActiveTrackColor = Color.Gray,
                disabledActiveTickColor = Color.Gray,
                disabledInactiveTickColor = Color.Gray,
                disabledInactiveTrackColor = Color.Gray,
                inactiveTrackColor = Color.Gray,
                activeTrackColor = LocalCustomAccentColors.current.accentButton
            )
        )


        Spacer(modifier = Modifier.height(30.dp))



        Button(
            onClick = {

                viewModelTrim.startTrimVideo(videoDetails)
                //     onClick(inputPath)

            },
            modifier = Modifier.padding(horizontal = 15.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LocalCustomAccentColors.current.accentButton,
                contentColor = Color.Gray.copy(alpha = 1f)
            )
        ) {
            Text(
                "TRIM",
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                color = LocalCustomAccentColors.current.accentText
            )

        }


    }

}

@Composable
@Preview(showBackground = true)
fun PreviewTrimScreen() {
    TrimScreen(onClick = {

    }, inputPath = "", videoDetails = MediaDetails())
}
