package com.sentinal.app.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import java.util.Locale

class VoiceSession(private val context: Context) {
    private var recognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private val main = Handler(Looper.getMainLooper())

    fun init(onReady: (() -> Unit)? = null) {
        main.post {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                recognizer = SpeechRecognizer.createSpeechRecognizer(context)
            }
            tts = TextToSpeech(context) { onReady?.invoke() }
            tts?.language = Locale.getDefault()
        }
    }

    fun startListening(onResult: (text: String) -> Unit, onError: (String) -> Unit) {
        val r = recognizer ?: run { onError("Recognizer unavailable"); return }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        r.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                onError("STT error: $error")
                // restart after brief delay to keep continuous
                main.postDelayed({ startListening(onResult, onError) }, 600)
            }
            override fun onResults(results: Bundle) {
                val texts = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!texts.isNullOrEmpty()) onResult(texts.first())
                // continue listening
                main.post { startListening(onResult, onError) }
            }
            override fun onPartialResults(partialResults: Bundle) {
                val texts = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!texts.isNullOrEmpty()) onResult(texts.first())
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        r.startListening(intent)
    }

    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_ADD, null, "sentinal_tts_${System.currentTimeMillis()}")
    }

    fun release() {
        main.post {
            recognizer?.destroy()
            recognizer = null
            tts?.shutdown()
            tts = null
        }
    }
}
