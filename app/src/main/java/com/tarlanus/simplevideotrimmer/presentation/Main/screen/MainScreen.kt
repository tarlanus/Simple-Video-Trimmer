package com.tarlanus.simplevideotrimmer.presentation.Main.screen

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.oss.licenses.v2.OssLicensesMenuActivity
import com.tarlanus.simplevideotrimmer.domain.MediaDetails
import com.tarlanus.simplevideotrimmer.presentation.Main.viewmodels.ViewModelMain
import com.tarlanus.simplevideotrimmer.presentation.Main.viewstates.ViewStateMain
import com.tarlanus.simplevideotrimmer.presentation.components.*

@Composable
fun MainScreen(dark: Boolean, onToggleTheme: () -> Unit, onClick: (String, MediaDetails) -> Unit,
    viewModelMain: ViewModelMain = hiltViewModel()) {
    val state by viewModelMain.viewStateMain.collectAsStateWithLifecycle()
    val exo by viewModelMain.viewStateExo.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let(viewModelMain::retrieveMediaMetaData)
    }
    LifecycleStartEffect(viewModelMain) {
        viewModelMain.setStartBuildExolayer()
        onStopOrDispose { viewModelMain.stopExoBuildPlayer() }
    }
    BackHandler(enabled = state != ViewStateMain.IDLE, onBack = viewModelMain::stopTheVideo)
    VideoPage("Input video", preview = { MediaInspection(state, exo) }, topActions = {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            HeaderButton(if (dark) Icons.Default.LightMode else Icons.Default.DarkMode,
                if (dark) "Light mode" else "Dark mode", onToggleTheme)
            HeaderButton(Icons.Default.Info, "Open source licenses") {
                context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            }
        }
    }) {
        val selected = state as? ViewStateMain.SUCCESS
        if (selected == null) {
            VideoAction("Choose video", Icons.Default.VideoLibrary) {
                picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            }
        } else {
            MetadataPanel(selected.mediaDetails)
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Box(Modifier.weight(1f)) {
                    VideoAction("Go to trim", Icons.Default.ContentCut) {
                        onClick(selected.mediaPath, selected.mediaDetails)
                        viewModelMain.stopTheVideo()
                    }
                }
                TextButton(viewModelMain::stopTheVideo,
                    colors = ButtonDefaults.textButtonColors(contentColor = LocalContentColor.current)) { Text("Clear") }
            }
        }
    }
}

@Composable
private fun HeaderButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        Modifier.clip(RoundedCornerShape(10.dp)).clickable(role = Role.Button, onClick = onClick)
            .sizeIn(minWidth = 48.dp, minHeight = 48.dp).padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(21.dp))
        Text(label, fontSize = 12.sp, lineHeight = 16.sp, textAlign = TextAlign.Center)
    }
}
