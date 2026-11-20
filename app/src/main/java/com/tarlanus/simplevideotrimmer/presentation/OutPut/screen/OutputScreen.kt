package com.tarlanus.simplevideotrimmer.presentation.OutPut.screen

import androidx.compose.runtime.*
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tarlanus.simplevideotrimmer.presentation.Main.viewstates.ViewStateMain
import com.tarlanus.simplevideotrimmer.presentation.OutPut.viewmodels.ViewModelOutput
import com.tarlanus.simplevideotrimmer.presentation.components.*

@Composable
fun OutputScreen(onClick: () -> Unit, outputPath: String,
    viewModelOutput: ViewModelOutput = hiltViewModel<ViewModelOutput, ViewModelOutput.Factory>(
        creationCallback = { it.create(outputPath) }
    )
) {
    val state by viewModelOutput.viewStateUI.collectAsStateWithLifecycle()
    val exo by viewModelOutput.viewStateExo.collectAsStateWithLifecycle()
    LifecycleStartEffect(viewModelOutput) {
        viewModelOutput.setStartBuildExolayer()
        onStopOrDispose { viewModelOutput.stopExoBuildPlayer() }
    }
    VideoPage("Output video", onBack = onClick, preview = { MediaInspection(state, exo) }) {
        (state as? ViewStateMain.SUCCESS)?.let { MetadataPanel(it.mediaDetails) }
    }
}
