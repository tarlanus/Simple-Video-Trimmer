package com.tarlanus.simplevideotrimmer.presentation.Trim.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun DialogScreen(dialogMessage: MutableState<String?>) {

    val setTargetValue = remember { mutableStateOf(0f) }
    val progressValue = animateFloatAsState(
        targetValue = setTargetValue.value
    )
    Dialog(onDismissRequest = {}) {
        Card(modifier = Modifier.width(250.dp).height(340.dp), shape = RoundedCornerShape(30.dp)) {
            Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {


                val getDialogMessage = dialogMessage.value

                if (getDialogMessage != null) {

                    if (getDialogMessage.contains("Error")) {
                        Text(text = getDialogMessage, fontSize = 20.sp, color = Color.Red, textAlign = TextAlign.Center)
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = getDialogMessage, fontSize = 20.sp, color = Color.Blue, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(15.dp))

                            val convertTOFlotat = getDialogMessage.toFloatOrNull()
                            if (convertTOFlotat != null) {
                                setTargetValue.value = convertTOFlotat
                                CircularWavyProgressIndicator(progress = {progressValue.value / 100f})

                            }

                        }
                    }
                }


            }
        }
    }
}

