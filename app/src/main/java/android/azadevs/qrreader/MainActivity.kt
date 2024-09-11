package android.azadevs.qrreader

import android.azadevs.qrreader.ui.theme.QRReaderTheme
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class MainActivity : ComponentActivity() {

    private var isScannerInitialized = false

    lateinit var valueScan: MutableState<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QRReaderTheme {

                valueScan = remember {
                    mutableStateOf("")
                }

                configureModule()

                gmsBarcodeScannerOptions()

                QRReader()

            }
        }
    }

    private fun configureModule() {
        val moduleInstall = ModuleInstall.getClient(this)
        val moduleRequest = ModuleInstallRequest.newBuilder()
            .addApi(GmsBarcodeScanning.getClient(this))
            .build()

        moduleInstall.installModules(moduleRequest)
            .addOnSuccessListener {
                isScannerInitialized = true
            }
            .addOnFailureListener {
                isScannerInitialized = false
            }
    }

    private fun gmsBarcodeScannerOptions() = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_CODE_128
        )
        .enableAutoZoom()
        .build()


    private fun resultScanner() {
        valueScan.value = ""
        if (isScannerInitialized) {
            val scanner =
                GmsBarcodeScanning.getClient(this@MainActivity, gmsBarcodeScannerOptions())
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    valueScan.value = barcode.rawValue ?: "No result"
                    Toast.makeText(this@MainActivity, valueScan.value, Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this@MainActivity, it.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }
        } else {
            Toast.makeText(this@MainActivity, "Scanner not initialized", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @Composable
    fun QRReader(modifier: Modifier = Modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Scan value: ${valueScan.value}",
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
            )

            Button(onClick = {
                resultScanner()
            }, modifier = modifier.fillMaxWidth()) {
                Text(text = "Scanner", fontSize = 16.sp)
            }
        }
    }
}