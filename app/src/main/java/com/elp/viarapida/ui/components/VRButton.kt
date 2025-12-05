package com.elp.viarapida.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elp.viarapida.ui.theme.Primary

/**
 * Botón principal de la aplicación Vía Rápida
 */
@Composable
fun VRButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Primary,
    fullWidth: Boolean = false
) {
    val buttonModifier = if (fullWidth) {
        modifier.fillMaxWidth()
    } else {
        modifier
    }

    Button(
        onClick = onClick,
        modifier = buttonModifier.height(50.dp),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = when (variant) {
                ButtonVariant.Primary -> Primary
                ButtonVariant.Secondary -> Color.White
                ButtonVariant.Outline -> Color.Transparent
            },
            contentColor = when (variant) {
                ButtonVariant.Primary -> Color.White
                ButtonVariant.Secondary -> Primary
                ButtonVariant.Outline -> Primary
            },
            disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
            disabledContentColor = Color.Gray
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (variant == ButtonVariant.Outline) 0.dp else 4.dp
        ),
        border = if (variant == ButtonVariant.Outline) {
            androidx.compose.foundation.BorderStroke(2.dp, Primary)
        } else null
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = if (variant == ButtonVariant.Primary) Color.White else Primary
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Botón de texto (sin fondo)
 */
@Composable
fun VRTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Text(
            text = text,
            color = Primary,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Botón de icono
 */
@Composable
fun VRIconButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = Primary
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp),
        enabled = enabled
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = backgroundColor,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                icon()
            }
        }
    }
}

enum class ButtonVariant {
    Primary,
    Secondary,
    Outline
}