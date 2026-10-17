package com.example.pruebas.presentation.detailScreen.detailsScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pruebas.domain.Ticket

@Composable
fun EurodreamsDetails(ticket: Ticket) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val dreams = ticket.dreams ?: emptyList()
        NumberRow(
            bets = ticket.numbers,
            contentAfterNumbers = { index ->
                if (index < dreams.size) {
                    NumberCircle(
                        number = dreams[index],
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        textColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        )
    }
}