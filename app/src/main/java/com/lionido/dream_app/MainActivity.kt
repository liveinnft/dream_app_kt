package com.lionido.dream_app

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.TextView
import android.content.Intent
import android.widget.Toast
import java.io.File
import java.io.IOException
import java.util.*

import com.lionido.dream_app.analyzer.DreamAnalyzer
import com.lionido.dream_app.analyzer.DreamApiAnalyzer
import com.lionido.dream_app.storage.DreamStorage
import com.lionido.dream_app.model.Dream

class MainActivity : AppCompatActivity() {

    private lateinit var recordButton: FloatingActionButton
    private lateinit var pulseCircle1: View
    private lateinit var pulseCircle2: View
    private lateinit var recordStatus: TextView

    private var isRecording = false
    private var mediaRecorder: MediaRecorder? = null
    private var speechRecognizer: SpeechRecognizer? = null

    private val floatingAnimator = ValueAnimator.ofFloat(0f, 360f)
    private var pulseAnimator1: ObjectAnimator? = null
    private var pulseAnimator2: ObjectAnimator? = null

    private val recordPermissionCode = 101
    private val dreamAnalyzer = DreamAnalyzer()
    private lateinit var dreamApiAnalyzer: DreamApiAnalyzer
    private lateinit var dreamStorage: DreamStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dreamStorage = DreamStorage(this)
        dreamApiAnalyzer = DreamApiAnalyzer(this)

        initViews()
        setupFloatingAnimation()
        setupClickListeners()
    }

    private fun initViews() {
        recordButton = findViewById(R.id.record_button)
        pulseCircle1 = findViewById(R.id.pulse_circle_1)
        pulseCircle2 = findViewById(R.id.pulse_circle_2)
        recordStatus = findViewById(R.id.record_status)
    }

    private fun setupFloatingAnimation() {
        // Анимация "плавания" кнопки - плавное изменение формы
        floatingAnimator.apply {
            duration = 4000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()

            addUpdateListener { animator ->
                val progress = animator.animatedValue as Float

                // Плавное изменение масштаба для создания эффекта "дыхания"
                val scale = 1.0f + 0.05f * Math.sin(Math.toRadians(progress.toDouble())).toFloat()
                recordButton.scaleX = scale
                recordButton.scaleY = scale

                // Небольшое вращение для создания "живости"
                recordButton.rotation = Math.sin(Math.toRadians(progress.toDouble())).toFloat() * 3f

                // Изменение elevation для создания эффекта "парения"
                recordButton.elevation = 8f + 4f * Math.sin(Math.toRadians(progress.toDouble())).toFloat()
            }
            start()
        }
    }

    private fun setupClickListeners() {
        recordButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                if (checkPermissions()) {
                    startRecording()
                } else {
                    requestPermissions()
                }
            }
        }

        findViewById<View>(R.id.btn_my_dreams).setOnClickListener {
            val intent = Intent(this, DreamsListActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.btn_statistics).setOnClickListener {
            val intent = Intent(this, DreamStatisticsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkPermissions(): Boolean {
        val audioPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        )
        return audioPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            recordPermissionCode
        )
    }

    private fun startRecording() {
        isRecording = true
        updateUI()
        startPulseAnimation()

        // Инициализация речевого распознавания
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                recordStatus.text = getString(R.string.recording)
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                stopRecording()
                Toast.makeText(
                    this@MainActivity,
                    "Ошибка распознавания речи: $error",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val dreamText = matches[0]
                    processDreamText(dreamText)
                }
                stopRecording()
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        // Запуск распознавания речи
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        speechRecognizer?.startListening(intent)
    }

    private fun stopRecording() {
        isRecording = false
        updateUI()
        stopPulseAnimation()

        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null

        mediaRecorder?.apply {
            try {
                stop()
                release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mediaRecorder = null
    }

    private fun updateUI() {
        if (isRecording) {
            recordButton.setImageResource(R.drawable.ic_stop)
            recordStatus.text = getString(R.string.recording)
        } else {
            recordButton.setImageResource(R.drawable.ic_mic)
            recordStatus.text = getString(R.string.tap_to_start_recording)
        }
    }

    private fun startPulseAnimation() {
        // Анимация пульсации для первого круга
        pulseAnimator1 = ObjectAnimator.ofFloat(pulseCircle1, "alpha", 0f, 0.6f, 0f).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            start()
        }

        // Анимация пульсации для второго круга с задержкой
        Handler(Looper.getMainLooper()).postDelayed({
            pulseAnimator2 = ObjectAnimator.ofFloat(pulseCircle2, "alpha", 0f, 0.4f, 0f).apply {
                duration = 2000
                repeatCount = ValueAnimator.INFINITE
                start()
            }
        }, 500)
    }

    private fun stopPulseAnimation() {
        pulseAnimator1?.cancel()
        pulseAnimator2?.cancel()
        pulseCircle1.alpha = 0f
        pulseCircle2.alpha = 0f
    }

    private fun processDreamText(dreamText: String) {
        recordStatus.text = getString(R.string.processing)

        // Анализируем сон в фоновом потоке
        Thread {
            try {
                val analysis = dreamAnalyzer.analyzeDream(dreamText)

                val dream = Dream(
                    title = generateDreamTitle(dreamText),
                    content = dreamText,
                    dateCreated = Date(),
                    emotions = analysis.emotions,
                    symbols = analysis.symbols,
                    interpretation = analysis.interpretation,
                    tags = analysis.tags,
                    mood = analysis.mood,
                    lucidDream = analysis.dreamType.name == "LUCID"
                )

                // Получаем расширенную интерпретацию через API
                dreamApiAnalyzer.interpretDream(dreamText) { apiInterpretation ->
                    runOnUiThread {
                        val finalDream = dream.copy(
                            interpretation = if (apiInterpretation != null) {
                                "${dream.interpretation}\n\n--- Расширенная интерпретация ---\n$apiInterpretation"
                            } else {
                                dream.interpretation
                            }
                        )

                        showDreamAnalysis(finalDream, analysis)
                        recordStatus.text = getString(R.string.tap_to_start_recording)
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Ошибка при анализе сна: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    recordStatus.text = getString(R.string.tap_to_start_recording)
                }
            }
        }.start()
    }

    private fun generateDreamTitle(dreamText: String): String {
        val words = dreamText.trim().split("\\s+".toRegex())
        return when {
            words.size <= 3 -> dreamText
            else -> "${words.take(3).joinToString(" ")}..."
        }
    }

    private fun showDreamAnalysis(dream: Dream, analysis: com.lionido.dream_app.analyzer.DreamAnalysis) {
        // Сохраняем сон во временное хранилище для передачи в следующую активность
        val tempDream = dream.copy(
            symbols = analysis.symbols,
            interpretation = dream.interpretation
        )

        // Сохраняем сон в локальное хранилище
        val success = dreamStorage.saveDream(tempDream)

        if (success) {
            // Показываем быстрое уведомление
            Toast.makeText(this, "✨ Анализ завершен!", Toast.LENGTH_SHORT).show()

            // Открываем экран с детальным анализом, передавая ID сна
            val intent = Intent(this, DreamAnalysisActivity::class.java).apply {
                putExtra("dreamId", tempDream.id)
            }
            startActivity(intent)
        } else {
            Toast.makeText(this, "Ошибка при сохранении сна", Toast.LENGTH_LONG).show()
        }
    }

    private fun getMoodEmoji(mood: com.lionido.dream_app.model.DreamMood): String {
        return when (mood) {
            com.lionido.dream_app.model.DreamMood.VERY_POSITIVE -> "😄"
            com.lionido.dream_app.model.DreamMood.POSITIVE -> "😊"
            com.lionido.dream_app.model.DreamMood.NEUTRAL -> "😐"
            com.lionido.dream_app.model.DreamMood.NEGATIVE -> "😔"
            com.lionido.dream_app.model.DreamMood.VERY_NEGATIVE -> "😰"
            com.lionido.dream_app.model.DreamMood.MIXED -> "🤔"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            recordPermissionCode -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecording()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.permission_denied),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingAnimator.cancel()
        stopPulseAnimation()
        
        // Properly clean up speech recognizer
        speechRecognizer?.apply {
            stopListening()
            destroy()
        }
        speechRecognizer = null
        
        // Properly clean up media recorder
        mediaRecorder?.apply {
            try {
                if (isRecording) {
                    stop()
                }
                release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mediaRecorder = null
        
        // Close dream storage
        dreamStorage.close()
    }

    override fun onPause() {
        super.onPause()
        // Stop recording when app goes to background to prevent resource leaks
        if (isRecording) {
            stopRecording()
        }
    }
}