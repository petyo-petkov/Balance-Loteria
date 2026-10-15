package com.example.pruebas.presentation.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BalanceCard(
    balanceState: BalanceState,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 24.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BalanceData(
                nombre = "GASTADO",
                color = Color.Red,
                data = balanceState.gastado
            )
            BalanceData(
                nombre = "BALANCE",
                color = Color(0xFF1976D2),
                data = balanceState.balance,
                extraData = balanceState.porcentaje,
            )
            BalanceData(
                nombre = "GANADO",
                color = Color(0xFF388E3C),
                data = balanceState.ganado,
            )
        }
    }
}

@Composable
fun BalanceData(
    nombre: String,
    data: String,
    color: Color,
    extraData: String? = null,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = nombre,
            color = color,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = data,
            style = MaterialTheme.typography.headlineSmall
        )
        if (extraData != null) {
            Text(
                text = extraData,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}