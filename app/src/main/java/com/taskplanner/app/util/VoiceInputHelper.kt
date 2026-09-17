package com.taskplanner.app.util

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import java.util.Locale

object VoiceInputHelper {

    fun createSpeechIntent(promptText: String = "Dictate your task or note..."): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, promptText)
        }
    }

    fun parseSpeechResult(data: Intent?): String? {
        val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        return matches?.firstOrNull()
    }
}
