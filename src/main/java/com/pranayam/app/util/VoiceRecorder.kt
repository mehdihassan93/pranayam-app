package com.pranayam.app.util

import android.content.Context
import android.media.MediaRecorder
import java.io.File
import java.util.UUID

class VoiceRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var currentFile: File? = null

    fun startRecording(): File? {
        val fileName = "voice_${UUID.randomUUID()}.m4a"
        currentFile = File(context.cacheDir, fileName)

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(currentFile?.absolutePath)
            prepare()
            start()
        }
        return currentFile
    }

    fun stopRecording(): File? {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        mediaRecorder = null
        return currentFile
    }

    fun cancelRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            currentFile?.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaRecorder = null
        currentFile = null
    }

    // Proxy for getting amplitude if we wanted a waveform animation
    fun getMaxAmplitude(): Int = mediaRecorder?.maxAmplitude ?: 0
}
