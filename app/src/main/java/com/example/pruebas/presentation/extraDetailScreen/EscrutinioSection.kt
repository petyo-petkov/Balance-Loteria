package com.example.pruebas.presentation.extraDetailScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pruebas.data.network.webView.webViewModels.Escrutinio
import com.example.pruebas.data.toCurrencyFormat


@Composable
fun EscrutinioSection(prize: List<Escrutinio?>) {

    val cabecera = listOf("Categoria:", "Ganadores:", "Premio:")

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Escrutinio:", style = MaterialTheme.typography.headlineSmall)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            cabecera.forEach {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                )
            }
        }

        prize.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    12.dp,
                    alignment = Alignment.CenterHorizontally
                ),
            ) {
                Text(
                    text = item?.tipo ?: "" ,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = item?.ganadores.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(0.5f)
                )
                Text(
                    text = "${item?.premio?.toCurrencyFormat()}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(0.5f),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}