package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

class VizoraAudioSynthesizer(context: Context) {
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var activeAudioJob: Job? = null
    private var activeTrack: AudioTrack? = null

    init {
        try {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    isTtsReady = true
                }
            }
        } catch (_: Exception) {
            isTtsReady = false
        }
    }

    fun speakVoiceover(
        script: String,
        language: String,
        speed: Float = 1.0f,
        pitch: Float = 1.0f
    ) {
        stopAllAudio()
        if (isTtsReady && tts != null) {
            val locale = when (language.lowercase()) {
                "swahili" -> Locale.forLanguageTag("sw-TZ")
                "french" -> Locale.FRENCH
                "spanish" -> Locale.forLanguageTag("es-ES")
                "portuguese" -> Locale.forLanguageTag("pt-BR")
                "arabic" -> Locale.forLanguageTag("ar-SA")
                "chinese" -> Locale.SIMPLIFIED_CHINESE
                "japanese" -> Locale.JAPANESE
                else -> Locale.US
            }
            tts?.language = locale
            tts?.setSpeechRate(speed.coerceIn(0.5f, 2.0f))
            tts?.setPitch(pitch.coerceIn(0.5f, 2.0f))
            tts?.speak(script, TextToSpeech.QUEUE_FLUSH, null, "vizora_tts_${System.currentTimeMillis()}")
        }
    }

    fun playMusicPreview(
        scope: CoroutineScope,
        bpm: Int = 100,
        volume: Float = 0.8f,
        fadeIn: Boolean = true,
        fadeOut: Boolean = true,
        onComplete: () -> Unit = {}
    ) {
        stopAllAudio()
        activeAudioJob = scope.launch(Dispatchers.Default) {
            try {
                val sampleRate = 22050
                val durationSeconds = 4
                val totalSamples = sampleRate * durationSeconds
                val buffer = ShortArray(totalSamples)

                // Warm Afrobeat / Cinematic chord progression frequencies (F#m7 - Dmaj7 - A - E)
                val chordFreqs = listOf(
                    doubleArrayOf(185.0, 220.0, 277.18, 329.63),
                    doubleArrayOf(146.83, 185.0, 220.0, 277.18),
                    doubleArrayOf(220.0, 277.18, 329.63, 440.0),
                    doubleArrayOf(164.81, 207.65, 246.94, 329.63)
                )
                val beatSamples = (sampleRate * 60) / bpm.coerceAtLeast(60)

                for (i in 0 until totalSamples) {
                    if (!isActive) break
                    val t = i.toDouble() / sampleRate
                    val chordIdx = ((i / (totalSamples / 4))).coerceIn(0, 3)
                    val freqs = chordFreqs[chordIdx]

                    // Warm Rhodes-like harmonic pad + rhythmic pulse
                    var sampleVal = 0.0
                    for (f in freqs) {
                        sampleVal += 0.20 * sin(2.0 * PI * f * t)
                        sampleVal += 0.06 * sin(4.0 * PI * f * t)
                    }

                    // Subtle Afrobeat shaker/percussion envelope on beats
                    val posInBeat = i % beatSamples
                    val beatEnv = exp(-8.0 * (posInBeat.toDouble() / beatSamples))
                    val subBass = 0.25 * sin(2.0 * PI * (freqs[0] * 0.5) * t) * beatEnv
                    sampleVal += subBass

                    // Fade in / out envelope
                    var env = volume.toDouble()
                    if (fadeIn && t < 0.6) {
                        env *= (t / 0.6)
                    }
                    if (fadeOut && t > durationSeconds - 0.8) {
                        env *= ((durationSeconds - t) / 0.8).coerceAtLeast(0.0)
                    }

                    val clamped = (sampleVal * env * Short.MAX_VALUE).toInt()
                        .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                    buffer[i] = clamped.toShort()
                }

                playPcmBuffer(buffer, sampleRate)
            } catch (_: Exception) {
                // Ignore audio hardware exception in headless test environments
            } finally {
                onComplete()
            }
        }
    }

    fun playSfxPreview(
        scope: CoroutineScope,
        sfxName: String,
        onComplete: () -> Unit = {}
    ) {
        stopAllAudio()
        activeAudioJob = scope.launch(Dispatchers.Default) {
            try {
                val sampleRate = 22050
                val durationSeconds = 2
                val totalSamples = sampleRate * durationSeconds
                val buffer = ShortArray(totalSamples)
                val lower = sfxName.lowercase()

                for (i in 0 until totalSamples) {
                    val t = i.toDouble() / sampleRate
                    val raw = when {
                        lower.contains("thunder") || lower.contains("explosion") -> {
                            val rumble = (Random.nextDouble(-1.0, 1.0) * 0.6 + sin(2.0 * PI * 55.0 * t) * 0.4)
                            rumble * exp(-1.8 * t)
                        }
                        lower.contains("rain") || lower.contains("ocean") || lower.contains("crowd") -> {
                            val waveMod = 0.5 + 0.4 * sin(2.0 * PI * 0.5 * t)
                            Random.nextDouble(-0.5, 0.5) * waveMod
                        }
                        lower.contains("birds") -> {
                            val chirpFreq = 1800.0 + 600.0 * sin(2.0 * PI * 12.0 * t)
                            sin(2.0 * PI * chirpFreq * t) * (if ((i / 2500) % 2 == 0) 0.5 else 0.05)
                        }
                        lower.contains("footsteps") || lower.contains("door") -> {
                            val stepPeriod = sampleRate / 2
                            val localT = (i % stepPeriod).toDouble() / sampleRate
                            sin(2.0 * PI * 110.0 * localT) * exp(-18.0 * localT) * 0.8
                        }
                        else -> { // Traffic / City / Default
                            (sin(2.0 * PI * 140.0 * t) * 0.3 + sin(2.0 * PI * 280.0 * t) * 0.2) * exp(-0.8 * t)
                        }
                    }
                    buffer[i] = (raw * Short.MAX_VALUE * 0.7).toInt()
                        .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (_: Exception) {
            } finally {
                onComplete()
            }
        }
    }

    private fun playPcmBuffer(buffer: ShortArray, sampleRate: Int) {
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        activeTrack = track
        track.write(buffer, 0, buffer.size)
        track.play()
    }

    fun stopAllAudio() {
        try {
            tts?.stop()
        } catch (_: Exception) {}
        try {
            activeAudioJob?.cancel()
            activeTrack?.stop()
            activeTrack?.release()
            activeTrack = null
        } catch (_: Exception) {}
    }
}
