package com.example.deepsea.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class AudioPlaybackManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    /**
     * Play audio from a URL
     */
    fun playAudioFromUrl(url: String, playbackSpeed: Float = 1.0f) {
        coroutineScope.launch {
            releaseMediaPlayer()

            try {
                withContext(Dispatchers.IO) {
                    mediaPlayer = MediaPlayer().apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .build()
                        )
                        setDataSource(context, Uri.parse(url))
                        prepare()
                        playbackParams = playbackParams.setSpeed(playbackSpeed)
                        start()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Play letter-by-letter pronunciation of a word
     */
    fun playSpelling(word: String) {
        coroutineScope.launch {
            try {
                // Break the word into characters
                val characters = word.toCharArray()

                // Play each character sound sequentially
                for (char in characters) {
                    val charUrl = getCharacterAudioUrl(char.toString())
                    playAudioAndWait(charUrl)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Play a single audio file and wait for it to complete
     */
    private suspend fun playAudioAndWait(url: String) {
        return withContext(Dispatchers.IO) {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                setDataSource(context, Uri.parse(url))
                prepare()
                start()
            }

            // Wait for playback to complete
            while (player.isPlaying) {
                Thread.sleep(50)
            }
            player.release()
        }
    }

    /**
     * Get the audio URL for a specific character
     */
    private fun getCharacterAudioUrl(character: String): String {
        // In a real app, you'd make an API call or use a predefined mapping
        // For now we'll assume the Spring Boot backend has endpoints for individual characters
        return "http://your-spring-boot-server.com/api/audio/character/$character"
    }

    /**
     * Release media player resources
     */
    fun releaseMediaPlayer() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }
}