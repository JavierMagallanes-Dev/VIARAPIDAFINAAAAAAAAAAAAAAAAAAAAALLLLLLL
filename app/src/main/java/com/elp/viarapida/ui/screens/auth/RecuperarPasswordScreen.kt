package com.elp.viarapida.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elp.viarapida.ui.components.*
import com.elp.viarapida.ui.theme.Primary
import com.elp.viarapida.viewmodel.AuthState
import com.elp.viarapida.viewmodel.AuthViewModel

/**
 * Pantalla de recuperación de contraseña
 */
@Composable
fun RecuperarPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var emailEnviado by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Observar estado de recuperación
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.PasswordResetSent -> {
                emailEnviado = true
                showError = false
            }
            is AuthState.Error -> {
                errorMessage = state.message
                showError = true
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            VRTopBar(
                title = "Recuperar Contraseña",
                onNavigationClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!emailEnviado) {
                // Formulario de recuperación
                Spacer(modifier = Modifier.height(32.dp))

                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "¿Olvidaste tu contraseña?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ingresa tu correo electrónico y te enviaremos un enlace para restablecer tu contraseña",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Mensaje de error
                if (showError) {
                    VRInfoBanner(
                        message = errorMessage,
                        type = BannerType.Error,
                        onDismiss = { showError = false }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Campo de email
                VRTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo electrónico",
                    placeholder = "ejemplo@correo.com",
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done,
                    onImeAction = {
                        if (email.isNotBlank()) {
                            viewModel.recuperarPassword(email)
                        }
                    },
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botón de enviar
                VRButton(
                    text = "Enviar Enlace",
                    onClick = {
                        showError = false
                        viewModel.recuperarPassword(email)
                    },
                    fullWidth = true,
                    isLoading = isLoading,
                    enabled = email.isNotBlank()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Volver al login
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    VRTextButton(
                        text = "Volver al inicio de sesión",
                        onClick = onNavigateBack,
                        enabled = !isLoading
                    )
                }

            } else {
                // Confirmación de email enviado
                Spacer(modifier = Modifier.height(60.dp))

                Icon(
                    imageVector = Icons.Default.MarkEmailRead,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "¡Correo Enviado!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Hemos enviado un enlace de recuperación a:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                VRInfoBanner(
                    message = "Revisa tu bandeja de entrada y la carpeta de spam",
                    type = BannerType.Info
                )

                Spacer(modifier = Modifier.height(32.dp))

                VRButton(
                    text = "Volver al Inicio de Sesión",
                    onClick = onNavigateBack,
                    fullWidth = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                VRButton(
                    text = "Reenviar Correo",
                    onClick = {
                        emailEnviado = false
                        viewModel.resetAuthState()
                    },
                    variant = ButtonVariant.Outline,
                    fullWidth = true
                )
            }
        }
    }
}