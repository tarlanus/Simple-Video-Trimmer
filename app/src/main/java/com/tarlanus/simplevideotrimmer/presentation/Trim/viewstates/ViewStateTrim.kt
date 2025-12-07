package com.tarlanus.simplevideotrimmer.presentation.Trim.viewstates


sealed class ViewStateTrim {
    data object LOADING : ViewStateTrim()
    data object ERROR : ViewStateTrim()
    data object IDLE : ViewStateTrim()

}