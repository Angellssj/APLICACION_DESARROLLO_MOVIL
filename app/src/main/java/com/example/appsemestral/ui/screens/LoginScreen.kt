package com.example.appsemestral.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appsemestral.ui.viewmodel.SesionViewModel

@Composable
fun LoginScreen(viewModel: SesionViewModel, onIngreso: () -> Unit) {
    val estado by viewModel.loginState.collectAsStateWithLifecycle()
    var verPassword by remember { mutableStateOf(false) }

    val ingresar: () -> Unit = {
        if (viewModel.validar()) viewModel.iniciarSesion("usuario y contraseña") { onIngreso() }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("WIM 100", style = MaterialTheme.typography.headlineLarge)
        Text("DYM Cyber · Selección de candidatas", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = estado.usuario.valor,
            onValueChange = viewModel::onUsuario,
            label = { Text("Usuario") },
            placeholder = { Text("Ej: evaluadora1") },
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
            isError = estado.usuario.error != null,
            supportingText = { estado.usuario.error?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = estado.password.valor,
            onValueChange = viewModel::onPassword,
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { verPassword = !verPassword }) {
                    Icon(
                        if (verPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (verPassword) "Ocultar" else "Mostrar"
                    )
                }
            },
            visualTransformation = if (verPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            isError = estado.password.error != null,
            supportingText = { estado.password.error?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        estado.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (estado.cargando) LinearProgressIndicator(Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Button(onClick = ingresar, enabled = !estado.cargando, modifier = Modifier.fillMaxWidth()) {
            Text("Ingresar")
        }
    }
}
