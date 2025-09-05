package com.yourcompany.julesprojem

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class GnssViewModelTest {

    @Mock
    private lateinit var mockBluetoothService: BluetoothService

    @Mock
    private lateinit var mockNtripClient: NtripClient

    private lateinit var viewModel: GnssViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Mockito.`when`(mockBluetoothService.getPairedDevices()).thenReturn(emptySet())
        viewModel = GnssViewModel(mockBluetoothService, mockNtripClient)
    }

    @Test
    fun testInitialState() {
        assertEquals("South", viewModel.selectedManufacturer)
        assertEquals("ALPS2", viewModel.selectedModel)
        assertEquals("Bağlı Değil", viewModel.connectionStatus)
        assertEquals(0, viewModel.bluetoothDevices.value.size)
    }
}
