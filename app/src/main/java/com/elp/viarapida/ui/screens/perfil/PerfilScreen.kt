package com.elp.viarapida.ui.screens.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elp.viarapida.ui.components.*
import com.elp.viarapida.ui.theme.Primary
import com.elp.viarapida.viewmodel.AuthViewModel
import com.elp.viarapida.viewmodel.PerfilViewModel

/**
 * Pantalla de perfil de usuario
 */
@Composable
fun PerfilScreen(
    onNavigateToEditarPerfil: () -> Unit,
    onNavigateToConfiguracion: () -> Unit,
    onNavigateToAyuda: () -> Unit,
    onNavigateToTerminos: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    perfilViewModel: PerfilViewModel = viewModel()
) {
    val usuario by authViewModel.usuario.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            VRTopBar(
                title = "Mi Perfil",
                actions = {
                    IconButton(onClick = onNavigateToEditarPerfil) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar perfil"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Header con foto y datos básicos
            item {
                ProfileHeader(
                    nombre = usuario?.nombreCompleto() ?: "Usuario",
                    email = usuario?.email ?: "",
                    telefono = usuario?.telefono ?: ""
                )
            }

            // Estadísticas
            item {
                ProfileStats()
            }

            // Opciones del menú
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "CONFIGURACIÓN",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                MenuOption(
                    icon = Icons.Default.Settings,
                    title = "Configuración de cuenta",
                    subtitle = "Privacidad, seguridad y más",
                    onClick = onNavigateToConfiguracion
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "SOPORTE",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                MenuOption(
                    icon = Icons.Default.Help,
                    title = "Centro de ayuda",
                    subtitle = "Preguntas frecuentes y soporte",
                    onClick = onNavigateToAyuda
                )
            }

            item {
                MenuOption(
                    icon = Icons.Default.Description,
                    title = "Términos y condiciones",
                    subtitle = "Políticas de uso y privacidad",
                    onClick = onNavigateToTerminos
                )
            }

            item {
                MenuOption(
                    icon = Icons.Default.Info,
                    title = "Acerca de Vía Rápida",
                    subtitle = "Versión 1.0.0",
                    onClick = { }
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Botón de cerrar sesión
            item {
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(16.dp))

                VRButton(
                    text = "Cerrar Sesión",
                    onClick = { showLogoutDialog = true },
                    variant = ButtonVariant.Outline,
                    fullWidth = true,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }

    // Diálogo de confirmación de cierre de sesión
    if (showLogoutDialog) {
        VRConfirmDialog(
            title = "Cerrar Sesión",
            message = "¿Estás seguro de que deseas cerrar sesión?",
            confirmText = "Cerrar sesión",
            cancelText = "Cancelar",
            onConfirm = {
                authViewModel.logout()
                onLogout()
                showLogoutDialog = false
            },
            onDismiss = {
                showLogoutDialog = false
            }
        )
    }
}

@Composable
private fun ProfileHeader(
    nombre: String,
    email: String,
    telefono: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Primary.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Surface(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                color = Primary
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre
            Text(
                text = nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Email
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Teléfono
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = telefono,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun ProfileStats() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            icon = Icons.Default.ConfirmationNumber,
            value = "0",
            label = "Viajes"
        )
        StatItem(
            icon = Icons.Default.Star,
            value = "0",
            label = "Puntos"
        )
        StatItem(
            icon = Icons.Default.LocalOffer,
            value = "0",
            label = "Ofertas"
        )
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = Primary.copy(alpha = 0.1f),
            modifier = Modifier.size(56.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun MenuOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Primary.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
        }
    }
}