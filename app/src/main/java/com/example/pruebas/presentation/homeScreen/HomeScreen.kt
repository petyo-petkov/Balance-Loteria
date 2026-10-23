package com.example.pruebas.presentation.homeScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pruebas.domain.Ticket

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    tickets: List<TicketUiModel>,
    balanceState: BalanceState,
    onClick: (Ticket) -> Unit,
    onDeleteClick: () -> Unit,
    onScannerClick: () -> Unit,
    onJackpotClick: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(
                top = 140.dp,
                start = 12.dp,
                end = 12.dp,
                bottom = 80.dp
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            items(tickets, key = { it.ticket.id }) { uiModel ->
                TicketUI(
                    uiModel = uiModel,
                    onClick = onClick
                )
            }
        }

        BalanceCard(
            balanceState = balanceState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        MyFAB(
            onDeleteClick = onDeleteClick,
            onScannerClick = onScannerClick,
            onJackpotClick = onJackpotClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 16.dp)
        )

    }
}
