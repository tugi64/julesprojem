package com.yourcompany.julesprojem

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.io.InputStream
import java.util.*

@SuppressLint("MissingPermission") // Permissions are handled in MainActivity
class BluetoothService(private val context: Context) {

    private val bluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private var onDeviceFound: ((BluetoothDevice) -> Unit)? = null
    private var socket: BluetoothSocket? = null
    private val sppUuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Standard SPP UUID

    private val discoveryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == BluetoothDevice.ACTION_FOUND) {
                val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                } else {
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }
                device?.let { onDeviceFound?.invoke(it) }
            }
        }
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    fun getPairedDevices(): Set<BluetoothDevice> {
        return bluetoothAdapter?.bondedDevices ?: emptySet()
    }

    fun startDiscovery(onDeviceFound: (BluetoothDevice) -> Unit) {
        this.onDeviceFound = onDeviceFound
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(discoveryReceiver, filter)
        bluetoothAdapter?.startDiscovery()
    }

    fun cancelDiscovery() {
        bluetoothAdapter?.cancelDiscovery()
        try {
            context.unregisterReceiver(discoveryReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver was not registered, ignore.
        }
    }

    fun connect(deviceAddress: String): Result<InputStream> {
        cancelDiscovery()
        val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
        return try {
            socket = device?.createRfcommSocketToServiceRecord(sppUuid)
            socket?.connect()
            val inputStream = socket?.inputStream
            if (inputStream != null) {
                Result.success(inputStream)
            } else {
                Result.failure(IOException("Failed to get input stream."))
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Result.failure(e)
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
