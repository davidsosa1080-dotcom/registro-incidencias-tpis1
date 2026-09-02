package sv.edu.utec.etps1.registroincidencias


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sv.edu.utec.etps1.registroincidencias.ui.theme.RegistroIncidenciasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegistroIncidenciasTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BitacoraTecnicaPV()
                }
            }
        }
    }
}

@Composable
fun BitacoraTecnicaPV() {
    var estadoInspeccion by remember { mutableStateOf("Inspección no iniciada") }
    var clienteSitio by remember { mutableStateOf("") }
    var descripcionActividad by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bitácora Técnica de Campo — Sistemas FV",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Bitácora para el levantamiento de datos técnicos en campo durante actividades de O&M e " +
                    "instalación de sistemas fotovoltaicos: parámetros ambientales (irradiancia, temperatura ambiente, " +
                    "geolocalización), mediciones eléctricas AC/DC, estado operativo de equipos, tendencias gráficas y evidencia fotográfica.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = clienteSitio,
            onValueChange = { clienteSitio = it },
            label = { Text("Cliente / Sitio") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = descripcionActividad,
            onValueChange = { descripcionActividad = it },
            label = { Text("Descripción de la actividad") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Estado de la inspección:", style = MaterialTheme.typography.labelLarge)
                Text(text = estadoInspeccion, style = MaterialTheme.typography.titleLarge)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            estadoInspeccion = if (clienteSitio.isBlank() || descripcionActividad.isBlank()) {
                "Completa ambos campos antes de iniciar"
            } else {
                "Inspección en curso: $clienteSitio — $descripcionActividad"
            }
        }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Iniciar bitácora de inspección")
                Text(text = "Prototipo inicial — Unidad 1", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BitacoraTecnicaPVPreview() {
    RegistroIncidenciasTheme {
        BitacoraTecnicaPV()
    }
}