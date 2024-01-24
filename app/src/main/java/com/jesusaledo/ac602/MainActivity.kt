package com.jesusaledo.ac602

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jesusaledo.ac602.ui.theme.AC602Theme
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxSize()
            ) {
                AppNotas()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppNotas() {
    val context = LocalContext.current
    var notas by remember { mutableStateOf(loadNotas(context)) }
    var nuevaNota by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Notas") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            exitProcess(0)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Pseudo EditText para notas (no es Composable)
                NotasInputField(notas, onNotasChanged = { notasList ->
                    notas = notasList
                },)

                // Campo para nueva nota
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = nuevaNota,
                    onValueChange = {
                        nuevaNota = it
                    },
                    label = { Text("Nueva Nota") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                // Botón para guardar nueva nota
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (nuevaNota.isNotBlank()) {
                            notas = notas + "\n" + nuevaNota
                            saveNotas(context, notas)
                            nuevaNota = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Guardar Nota")
                }
            }
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NotasInputField(notas: String, onNotasChanged: (String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = notas,
        onValueChange = {
            onNotasChanged(it)
        },
        label = { Text("Notas") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .padding(8.dp)
            .padding(top = 50.dp)
    )
}

// Funciones auxiliares para guardar y cargar notas usando un archivo de texto

fun saveNotas(context: Context, notas: String) {
    try {
        context.openFileOutput("notas.txt", Context.MODE_PRIVATE).use {
            it.write(notas.toByteArray())
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun loadNotas(context: Context): String {
    return try {
        context.openFileInput("notas.txt").bufferedReader().use {
            it.readText()
        }
    } catch (e: FileNotFoundException) {
        ""
    }
}
