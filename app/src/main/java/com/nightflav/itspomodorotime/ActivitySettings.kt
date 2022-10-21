package com.nightflav.itspomodorotime

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter.EXTRA_DATA
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.forEach
import androidx.core.view.get
import com.nightflav.itspomodorotime.databinding.ActivitySettingsBinding
import com.nightflav.itspomodorotime.model.Settings
import com.nightflav.itspomodorotime.model.getParsedTime
import com.nightflav.itspomodorotime.model.parseStringToTimeInMills

class ActivitySettings : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val getResultIntent
        get() = Intent().apply {
            putExtra(OUTPUT_TIME, parseStringToTimeInMills(binding.etExerciseTime.text.toString()))
            putExtra(OUTPUT_AMOUNT, getAmount())
        }

    private fun getAmount(): Int {
        val id = binding.rgExercises.checkedRadioButtonId
        val rButton = findViewById<RadioButton>(id)
        return rButton.text.toString().toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val oldSettings = Settings(
            intent.getLongExtra(EXTRA_TIME, Settings.DEFAULT_SETTINGS.time),
            intent.getIntExtra(EXTRA_AMOUNT, Settings.DEFAULT_SETTINGS.amountOfExercises)
        )

        binding.etExerciseTime.setText(getParsedTime(oldSettings.time.toString().toLong()))

        binding.rgExercises.forEach {
            it as RadioButton
            if (it.text.toString().toInt() == oldSettings.amountOfExercises) it.isChecked = true
        }

        binding.btnCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSaveSettings.setOnClickListener {
            setResult(RESULT_OK, getResultIntent)
            finish()
        }
    }

    class Contract : ActivityResultContract<Settings, Settings>() {
        override fun createIntent(context: Context, input: Settings): Intent {
            return Intent(context, ActivitySettings::class.java).apply {
                putExtra(
                    EXTRA_TIME, input.time
                )
                putExtra(
                    EXTRA_AMOUNT, input.amountOfExercises
                )
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Settings {
            val time = intent?.getLongExtra(OUTPUT_TIME, 300)
                ?: Settings.DEFAULT_SETTINGS.time
            val amountOfExercises =
                intent?.getIntExtra(OUTPUT_AMOUNT, 1)
                    ?: Settings.DEFAULT_SETTINGS.amountOfExercises
            return Settings(time, amountOfExercises)
        }
    }

    companion object {
        const val EXTRA_TIME = "Extra Data time"
        const val EXTRA_AMOUNT = "Extra Data time"
        const val OUTPUT_TIME = "time"
        const val OUTPUT_AMOUNT = "amount"
    }
}