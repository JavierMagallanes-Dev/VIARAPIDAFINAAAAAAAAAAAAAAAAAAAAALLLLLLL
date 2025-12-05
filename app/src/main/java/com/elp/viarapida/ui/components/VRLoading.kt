package com.elp.viarapida.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elp.viarapida.ui.theme.*

/**
 * Indicador de carga
 */
@Composable
fun VRLoadingIndicator(
    modifier: Modifier = Modifier,
    message: String = "Cargando..."
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = Primary,
                strokeWidth = 4.dp,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = GrayMedium
            )
        }
    }
}

/**
 * Estado vacío
 */
@Composable
fun VREmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            tint = GrayMedium,
            modifier = Modifier.size(80.dp)
        )
    },
    action: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            icon()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMedium,
                textAlign = TextAlign.Center
            )

            if (action != null) {
                Spacer(modifier = Modifier.height(24.dp))
                action()
            }
        }
    }
}

/**
 * Estado de error
 */
@Composable
fun VRErrorState(
    title: String = "Error",
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = Error,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Error,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMedium,
                textAlign = TextAlign.Center
            )

            if (onRetry != null) {
                Spacer(modifier = Modifier.height(24.dp))
                VRButton(
                    text = "Reintentar",
                    onClick = onRetry,
                    variant = ButtonVariant.Primary
                )
            }
        }
    }
}

/**
 * Banner de información/alerta
 */
@Composable
fun VRInfoBanner(
    message: String,
    type: BannerType = BannerType.Info,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
) {
    val (backgroundColor, iconColor, icon) = when (type) {
        BannerType.Info -> Triple(Info.copy(alpha = 0.1f), Info, Icons.Default.Info)
        BannerType.Success -> Triple(Success.copy(alpha = 0.1f), Success, Icons.Default.CheckCircle)
        BannerType.Warning -> Triple(Warning.copy(alpha = 0.1f), Warning, Icons.Default.Warning)
        BannerType.Error -> Triple(Error.copy(alpha = 0.1f), Error, Icons.Default.Error)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = iconColor,
                modifier = Modifier.weight(1f)
            )

            if (onDismiss != null) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Diálogo de confirmación
 */
@Composable
fun VRConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "Confirmar",
    cancelText: String = "Cancelar",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDestructive: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = if (isDestructive) Icons.Default.Warning else Icons.Default.Info,
                contentDescription = null,
                tint = if (isDestructive) Error else Primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            VRButton(
                text = confirmText,
                onClick = onConfirm,
                variant = if (isDestructive) ButtonVariant.Primary else ButtonVariant.Primary
            )
        },
        dismissButton = {
            VRTextButton(
                text = cancelText,
                onClick = onDismiss
            )
        }
    )
}

/**
 * Snackbar personalizado
 */
@Composable
fun VRSnackbar(
    message: String,
    type: BannerType = BannerType.Info,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val backgroundColor = when (type) {
        BannerType.Success -> Success
        BannerType.Error -> Error
        BannerType.Warning -> Warning
        BannerType.Info -> Primary
    }

    Snackbar(
        action = actionLabel?.let {
            {
                TextButton(onClick = { onAction?.invoke() }) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        dismissAction = {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        containerColor = backgroundColor,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text = message)
    }
}

enum class BannerType {
    Info,
    Success,
    Warning,
    Error
}