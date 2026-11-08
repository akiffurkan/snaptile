package com.snaptile.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snaptile.app.R
import com.snaptile.app.ui.theme.ActiveGreen
import com.snaptile.app.ui.theme.ActiveGreenBg
import com.snaptile.app.ui.theme.InactiveRed
import com.snaptile.app.ui.theme.InactiveRedBg

@Composable
fun StatusCard(
    isAccessibilityActive: Boolean,
    isSystemSupported: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.status_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Accessibility Service Status
            StatusRow(
                title = stringResource(R.string.status_accessibility_title),
                statusText = if (isAccessibilityActive) {
                    stringResource(R.string.status_active)
                } else {
                    stringResource(R.string.status_disabled)
                },
                isActive = isAccessibilityActive,
                activeColor = ActiveGreen,
                activeBg = ActiveGreenBg,
                inactiveColor = InactiveRed,
                inactiveBg = InactiveRedBg
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            // 2. Quick Settings Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.status_qs_tile_title),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.status_qs_instruction),
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            // 3. System Compatibility Status
            StatusRow(
                title = stringResource(R.string.status_compatibility_title),
                statusText = if (isSystemSupported) {
                    stringResource(R.string.status_supported)
                } else {
                    stringResource(R.string.status_unsupported)
                },
                isActive = isSystemSupported,
                activeColor = ActiveGreen,
                activeBg = ActiveGreenBg,
                inactiveColor = InactiveRed,
                inactiveBg = InactiveRedBg
            )
        }
    }
}

@Composable
private fun StatusRow(
    title: String,
    statusText: String,
    isActive: Boolean,
    activeColor: Color,
    activeBg: Color,
    inactiveColor: Color,
    inactiveBg: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isActive) Icons.Rounded.CheckCircle else Icons.Rounded.Warning,
                contentDescription = null,
                tint = if (isActive) activeColor else inactiveColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isActive) activeBg else inactiveBg)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                color = if (isActive) activeColor else inactiveColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
