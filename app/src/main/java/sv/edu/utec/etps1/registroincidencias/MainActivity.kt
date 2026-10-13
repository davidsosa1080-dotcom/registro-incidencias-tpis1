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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
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

        Spacer(modifier = Modifier.height(16.dp))

        // Semana 12: tablero de diagnóstico del dispositivo
        DiagnosticoDispositivo()

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            focusManager.clearFocus()
            inspeccionIniciada = true
        }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Iniciar bitácora de inspección")
                Text(text = "Semana 12 — Diagnóstico del dispositivo", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

/**
 * Semana 11. Lee el acelerómetro y lo interpreta como inclinómetro:
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

    var x by remember { mutableFloatStateOf(0f) }
    var y by remember { mutableFloatStateOf(0f) }
    var z by remember { mutableFloatStateOf(0f) }
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
                acelerometro == null -> {
                    Text(
                        text = "Sensor no disponible en este dispositivo. " +
                                "La inclinación del módulo debe medirse con un inclinómetro manual.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

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

/**
 * Semana 12. Tablero de diagnóstico del dispositivo:
 * 1) Comprueba qué sensores existen (disponibilidad).
 * 2) Al iniciar el diagnóstico, escucha luz y proximidad y muestra una interpretación.
 * 3) Al detenerlo, el bloque de lecturas sale de la composición y onDispose libera los sensores.
 */
@Composable
fun DiagnosticoDispositivo() {
    if (LocalInspectionMode.current) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Diagnóstico del dispositivo: disponible solo en emulador o dispositivo",
                modifier = Modifier.padding(16.dp)
            )
        }
        return
    }

    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    var diagnosticoActivo by remember { mutableStateOf(false) }

    // Sensores que se revisan en el tablero (tipo de Android + nombre visible)
    val sensoresRevisados = listOf(
        Sensor.TYPE_ACCELEROMETER to "Acelerómetro",
        Sensor.TYPE_LIGHT to "Luz ambiental",
        Sensor.TYPE_PROXIMITY to "Proximidad",
        Sensor.TYPE_MAGNETIC_FIELD to "Magnetómetro",
        Sensor.TYPE_GYROSCOPE to "Giroscopio"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Diagnóstico del dispositivo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 1) Disponibilidad: getDefaultSensor devuelve null si el sensor no existe
            sensoresRevisados.forEach { (tipo, nombre) ->
                val disponible = sensorManager.getDefaultSensor(tipo) != null
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = nombre, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = if (disponible) "✔ Disponible" else "✖ No disponible",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // 2) Control del ciclo: el usuario decide cuándo se escuchan los sensores
            OutlinedButton(
                onClick = { diagnosticoActivo = !diagnosticoActivo },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (diagnosticoActivo) "Detener diagnóstico" else "Iniciar diagnóstico")
            }
            Text(
                text = if (diagnosticoActivo)
                    "Estado: escuchando sensores de luz y proximidad"
                else
                    "Estado: diagnóstico detenido — sensores liberados",
                style = MaterialTheme.typography.bodySmall
            )

            // 3) Solo mientras está activo existe el bloque que registra los listeners
            if (diagnosticoActivo) {
                Spacer(modifier = Modifier.height(8.dp))
                LecturasDiagnostico(sensorManager)
            }
        }
    }
}

@Composable
fun LecturasDiagnostico(sensorManager: SensorManager) {
    val sensorLuz = remember { sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) }
    val sensorProximidad = remember { sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) }

    var lux by remember { mutableFloatStateOf(-1f) }        // -1 = aún sin lectura
    var distancia by remember { mutableFloatStateOf(-1f) }  // -1 = aún sin lectura

    DisposableEffect(sensorLuz, sensorProximidad) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_LIGHT -> lux = event.values[0]
                    Sensor.TYPE_PROXIMITY -> distancia = event.values[0]
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorLuz?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
        sensorProximidad?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }

        // Al detener el diagnóstico (o salir de la pantalla) se liberan ambos sensores
        onDispose { sensorManager.unregisterListener(listener) }
    }

    // Luz ambiental con interpretación orientada a la inspección de campo
    val textoLuz = when {
        sensorLuz == null -> "Luz ambiental: sensor no disponible en este dispositivo"
        lux < 0f -> "Luz ambiental: esperando lectura…"
        else -> {
            val interpretacion = when {
                lux < 50f -> "ambiente oscuro, use linterna para la inspección"
                lux < 1000f -> "interior o poca luz"
                lux < 10000f -> "exterior nublado o en sombra"
                else -> "sol directo, condición adecuada para inspeccionar módulos FV"
            }
            "Luz ambiental: %.0f lx — %s".format(lux, interpretacion)
        }
    }

    // Proximidad: valor menor al rango máximo = objeto cerca (por ejemplo, teléfono en el bolsillo)
    val textoProximidad = when {
        sensorProximidad == null -> "Proximidad: sensor no disponible en este dispositivo"
        distancia < 0f -> "Proximidad: esperando lectura…"
        distancia < sensorProximidad.maximumRange ->
            "Proximidad: %.1f cm — objeto cercano, pantalla cubierta".format(distancia)
        else ->
            "Proximidad: %.1f cm — sin objetos cerca".format(distancia)
    }

    Text(text = textoLuz, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(4.dp))
    Text(text = textoProximidad, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
}

@Preview(showBackground = true)
@Composable
fun BitacoraTecnicaPVPreview() {
    RegistroIncidenciasTheme {
        BitacoraTecnicaPV()
    }
}
