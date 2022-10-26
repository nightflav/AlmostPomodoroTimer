package com.nightflav.itspomodorotime.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Settings(
    var time: Long,
    var amountOfExercises: Int
) : Parcelable {
    companion object {
        @JvmStatic
        val DEFAULT_SETTINGS = Settings(1500, 1)
    }
}

fun getParsedTimeMinutes(time: Long): String {
    return (time / 60).toString()
}

fun getParsedTimerSeconds(time: Long): String {
    return if (time % 60 < 10) "0${time % 60}" else (time % 60).toString()
}

fun getParsedTime(time: Long): String {
    val minutes = (time / 60).toString()
    val seconds = if (time % 60 < 10) "0${time % 60}" else (time % 60).toString()
    return "$minutes:$seconds"
}

fun parseStringToTimeInMills(textTime: String): Long {
    val minutes = textTime.split(':').first().toLong()
    val seconds = textTime.split(':').last().toLong()
    return minutes * 60 + seconds
}