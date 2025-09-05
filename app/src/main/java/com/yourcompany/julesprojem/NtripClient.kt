package com.yourcompany.julesprojem

import java.io.IOException
import java.net.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NtripClient {

    private var socket: Socket? = null

    suspend fun connect(host: String, port: Int) {
        withContext(Dispatchers.IO) {
            try {
                socket = Socket(host, port)
                // Further logic for sending initial requests will go here
            } catch (e: IOException) {
                // Handle connection exception
                e.printStackTrace()
            }
        }
    }

    suspend fun sendGgaSentence(gga: String) {
        withContext(Dispatchers.IO) {
            try {
                socket?.getOutputStream()?.write(gga.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    suspend fun readData(): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val buffer = ByteArray(1024)
                val bytesRead = socket?.getInputStream()?.read(buffer) ?: -1
                if (bytesRead > 0) {
                    buffer.copyOf(bytesRead)
                } else {
                    null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }

    fun disconnect() {
        try {
            socket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
