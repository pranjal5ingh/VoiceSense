package com.example.voicesense.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File

data class RecordingResult(
    val filePath: String,
    val durationSec: Int
)

class AudioRecorder(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var startTimeMs: Long = 0L

    /**
     * @return true if recording started successfully, false otherwise
     */
    fun startRecording(): Boolean {
        stopIfRunning()

        return try {
            val fileName = "audio_${System.currentTimeMillis()}.m4a"
            outputFile = File(context.cacheDir, fileName)

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128_000)
                setAudioSamplingRate(44_100)
                setOutputFile(outputFile!!.absolutePath)
                prepare()
                start()
            }

            startTimeMs = System.currentTimeMillis()
            true
        } catch (e: Exception) {
            Log.e("AudioRecorder", "startRecording failed", e)
            try {
                mediaRecorder?.reset()
                mediaRecorder?.release()
            } catch (_: Exception) {}
            mediaRecorder = null
            outputFile = null
            false
        }
    }

    fun stopRecording(): RecordingResult? {
        val recorder = mediaRecorder ?: return null
        return try {
            recorder.stop()
            recorder.reset()
            recorder.release()
            mediaRecorder = null

            val durationSec = ((System.currentTimeMillis() - startTimeMs) / 1000).toInt()
            val path = outputFile?.absolutePath ?: return null
            RecordingResult(filePath = path, durationSec = durationSec)
        } catch (e: Exception) {
            Log.e("AudioRecorder", "stopRecording failed", e)
            mediaRecorder = null
            outputFile = null
            null
        }
    }

    fun stopIfRunning() {
        try {
            mediaRecorder?.apply {
                stop()
                reset()
                release()
            }
        } catch (_: Exception) {
        } finally {
            mediaRecorder = null
            outputFile = null
        }
    }
}