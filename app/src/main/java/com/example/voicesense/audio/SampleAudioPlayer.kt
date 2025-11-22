package com.example.voicesense.audio

import android.media.MediaPlayer
import android.util.Log

class SimpleAudioPlayer {

    private var mediaPlayer: MediaPlayer? = null

    fun play(path: String, onCompletion: () -> Unit = {}) {
        stop()
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(path)
                prepare()
                setOnCompletionListener {
                    onCompletion()
                    stop()
                }
                start()
            } catch (e: Exception) {
                Log.e("SimpleAudioPlayer", "play failed", e)
                stop()
            }
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {
        } finally {
            mediaPlayer = null
        }
    }
}