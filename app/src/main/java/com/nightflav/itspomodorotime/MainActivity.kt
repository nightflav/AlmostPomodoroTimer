package com.nightflav.itspomodorotime

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.nightflav.itspomodorotime.databinding.ActivityMainBinding
import com.nightflav.itspomodorotime.model.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settings: Settings
    private lateinit var timer: CountDownTimer
    private var timeLeftAfterPause by Delegates.notNull<Long>()
    private var isTimerPaused = false
    private var isTimerStarted = false
    private var exercisesCompleted = 0

    private val editMessageLauncher = registerForActivityResult(ActivitySettings.Contract()) {
        if(settings.amountOfExercises != it.amountOfExercises) {
            exercisesCompleted = 0
            settings.amountOfExercises = it.amountOfExercises
        }

        if(settings.time != it.time ) {
            settings.time = it.time
        }

        renderExercises()
        restartTimer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        settings = savedInstanceState?.getParcelable(SETTING_KEY) ?: Settings.DEFAULT_SETTINGS

        timeLeftAfterPause = settings.time

        binding.btnSettings.setOnClickListener { openSettings() }

        binding.btnRestartAll.setOnClickListener { setAllDefault() }

        setupTimer()
        updateTimer(settings.time * 1000)
        renderExercises()
    }

    private fun setAllDefault() {
        exercisesCompleted = 0
        restartTimer()
        renderExercises()
    }

    private fun renderExercises() {
        val undone = R.drawable.ic_exercise_undone
        val done = R.drawable.ic_exercise_done
        binding.llOneExercise.visibility = View.INVISIBLE
        binding.llTwoExercise.visibility = View.INVISIBLE
        binding.llThreeExercise.visibility = View.INVISIBLE
        when (settings.amountOfExercises) {
            1 -> {
                binding.llOneExercise.visibility = View.VISIBLE
                if (exercisesCompleted == 1)
                    binding.ivOnly.setImageResource(done)
                else
                    binding.ivOnly.setImageResource(undone)
            }
            2 -> {
                binding.llTwoExercise.visibility = View.VISIBLE
                when (exercisesCompleted) {
                    0 -> {
                        binding.ivDoubleFirst.setImageResource(undone)
                        binding.ivDoubleSecond.setImageResource(undone)
                    }
                    1 -> {
                        binding.ivDoubleFirst.setImageResource(done)
                        binding.ivDoubleSecond.setImageResource(undone)
                    }
                    else -> {
                        binding.ivDoubleFirst.setImageResource(done)
                        binding.ivDoubleSecond.setImageResource(done)
                    }
                }
            }
            3 -> {
                binding.llThreeExercise.visibility = View.VISIBLE
                when (exercisesCompleted) {
                    0 -> {
                        binding.ivTripleFirst.setImageResource(undone)
                        binding.ivTripleSecond.setImageResource(undone)
                        binding.ivTripleThird.setImageResource(undone)
                    }
                    1 -> {
                        binding.ivTripleFirst.setImageResource(done)
                        binding.ivTripleSecond.setImageResource(undone)
                        binding.ivTripleThird.setImageResource(undone)
                    }
                    2 -> {
                        binding.ivTripleFirst.setImageResource(done)
                        binding.ivTripleSecond.setImageResource(done)
                        binding.ivTripleThird.setImageResource(undone)
                    }
                    else -> {
                        binding.ivTripleFirst.setImageResource(done)
                        binding.ivTripleSecond.setImageResource(done)
                        binding.ivTripleThird.setImageResource(done)
                    }
                }
            }
            else -> {

            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(SETTING_KEY, settings)
    }

    override fun onStart() {
        super.onStart()
        binding.btnStart.setOnClickListener { startTimer() }
        binding.btnRestart.setOnClickListener { restartTimer() }
        binding.btnPause.setOnClickListener { pauseTimer() }
    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
    }

    private fun setupTimer() {
        timer = object : CountDownTimer(timeLeftAfterPause * 1000, 1000) {
            override fun onTick(p0: Long) {
                if (!isTimerPaused) {
                    updateTimer(p0)
                }
            }

            override fun onFinish() {
                exercisesCompleted++
                updateTimer(0)
                renderExercises()
                if(settings.amountOfExercises + 1 == exercisesCompleted) {
                    exercisesCompleted = 0
                }
                timerEnd()
            }
        }
    }

    private fun openSettings() {
        timer.cancel()
        editMessageLauncher.launch(settings)
    }

    private fun restartTimer() {
        timeLeftAfterPause = settings.time
        isTimerPaused = false
        isTimerStarted = false
        timer.cancel()
        updateTimer(timeLeftAfterPause * 1000)
        setupTimer()
    }

    private fun pauseTimer() {
        if (!isTimerPaused) {
            timeLeftAfterPause = parseStringToTimeInMills("${binding.timerView.text}:${binding.timerViewSeconds.text}")
        } else {
            timer.cancel()
            setupTimer()
            timer.start()
        }
        isTimerPaused = !isTimerPaused
    }

    private fun startTimer() {
        if (!isTimerStarted) {
            isTimerStarted = true
            timer.start()
        }
    }

    private fun timerEnd() {
        showEndTimerDialog()
        if(exercisesCompleted == settings.amountOfExercises)
            exercisesCompleted = 0
        restartTimer()
    }

    private fun showEndTimerDialog() {
        val dialog = AlertDialog.Builder(this@MainActivity)
            .setTitle(getString(R.string.time_end))
            .setMessage(
                if(settings.amountOfExercises == exercisesCompleted) getString(R.string.time_end_message)
            else getString(R.string.time_end_not_all_excercises)
            )
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, null)
            .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { dialog.dismiss(); renderExercises() }
    }

    private fun updateTimer(timeM: Long) {
        binding.timerView.text = getParsedTimeMinutes(timeM / 1000)
        binding.timerViewSeconds.text = getParsedTimerSeconds(timeM / 1000)
    }

    private companion object {
        const val SETTING_KEY = "Key Settings"
    }
}