package sv.edu.utec.etps1.registroincidencias

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sv.edu.utec.etps1.registroincidencias.ui.theme.RegistroIncidenciasTheme
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sqrt
import androidx.compose.ui.platform.LocalInspectionMode
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
    var clienteSitio by remember { mutableStateOf("") }
    var descripcionActividad by remember { mutableStateOf("") }
    var prioridadSeleccionada by remember { mutableStateOf("") }
    var inspeccionIniciada by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val prioridades = listOf("Baja", "Media", "Alta")

    // El mensaje se DERIVA del estado actual en cada recomposición (Semana 10)
    val estadoInspeccion = when {
        !inspeccionIniciada -> "Inspección no iniciada"
        clienteSitio.isBlank() || descripcionActividad.isBlank() ->
            "Completa ambos campos antes de iniciar"
        prioridadSeleccionada.isBlank() ->
            "Selecciona una prioridad antes de iniciar"
        else ->
            "Inspección en curso: $clienteSitio — $descripcionActividad (Prioridad: $prioridadSeleccionada)"
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
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

        // Teclado contextual 1: nombres propios (Words) + acción "Siguiente"
        OutlinedTextField(
            value = clienteSitio,
            onValueChange = { clienteSitio = it },
            label = { Text("Cliente / Sitio") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Teclado contextual 2: oraciones (Sentences) + acción "Listo" que cierra el teclado
        OutlinedTextField(
            value = descripcionActividad,
            onValueChange = { descripcionActividad = it },
            label = { Text("Descripción de la actividad") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Interacción táctil: selección de prioridad mediante Cards clickeables
        Text(
            text = "Prioridad de la actividad",
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            prioridades.forEach { nivel ->
                val seleccionada = prioridadSeleccionada == nivel
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { prioridadSeleccionada = nivel },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (seleccionada)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = nivel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Normal,
                        color = if (seleccionada)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Estado de la inspección:", style = MaterialTheme.typography.labelLarge)
                Text(text = estadoInspeccion, style = MaterialTheme.typography.titleLarge)
                if (prioridadSeleccionada.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Prioridad seleccionada: $prioridadSeleccionada",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Semana 11: lectura del acelerómetro como inclinómetro de módulos FV
        InclinometroModuloFV()

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            focusManager.clearFocus()
            inspeccionIniciada = true
        }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Iniciar bitácora de inspección")
                Text(text = "Semana 11 — Sensor acelerómetro", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

/**
 * Lee el acelerómetro del dispositivo y lo interpreta como inclinómetro:
 * con el teléfono apoyado sobre el módulo FV, el ángulo entre el eje Z del
 * teléfono y la vertical corresponde a la inclinación del módulo.
 */
@Composable
fun InclinometroModuloFV() {
    if (LocalInspectionMode.current) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Inclinómetro: disponible solo en emulador o dispositivo",
                modifier = Modifier.padding(16.dp)
            )
        }
        return
    }
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val acelerometro = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    var x by remember { mutableStateOf(0f) }
    var y by remember { mutableStateOf(0f) }
    var z by remember { mutableStateOf(0f) }
    var hayLectura by remember { mutableStateOf(false) }

    // Registra el listener al entrar en pantalla y lo libera al salir (evita consumo de batería)
    DisposableEffect(acelerometro) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                x = event.values[0]
                y = event.values[1]
                z = event.values[2]
                hayLectura = true
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        if (acelerometro != null) {
            sensorManager.registerListener(listener, acelerometro, SensorManager.SENSOR_DELAY_UI)
        }
        onDispose { sensorManager.unregisterListener(listener) }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Inclinómetro del módulo (acelerómetro)",
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(4.dp))

            when {
                // Caso no disponible: el dispositivo no tiene acelerómetro
                acelerometro == null -> {
                    Text(
                        text = "Sensor no disponible en este dispositivo. " +
                                "La inclinación del módulo debe medirse con un inclinómetro manual.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Sensor existe, pero aún no llega la primera lectura
                !hayLectura -> {
                    Text(text = "Esperando lectura del sensor…", style = MaterialTheme.typography.bodyMedium)
                }

                else -> {
                    val magnitud = sqrt(x * x + y * y + z * z)
                    val inclinacion = if (magnitud > 0f)
                        Math.toDegrees(acos((z / magnitud).coerceIn(-1f, 1f).toDouble()))
                    else 0.0
                    // En reposo la magnitud es ~9.81 m/s² (gravedad); una diferencia grande indica movimiento
                    val enMovimiento = abs(magnitud - SensorManager.GRAVITY_EARTH) > 1.5f

                    Text(
                        text = "Inclinación: %.1f°".format(inclinacion),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "X: %.2f   Y: %.2f   Z: %.2f m/s²".format(x, y, z),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (enMovimiento)
                            "Movimiento detectado: mantenga el teléfono quieto sobre el módulo"
                        else
                            "Lectura estable: válida para registrar la inclinación",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
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