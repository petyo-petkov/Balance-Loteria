package com.example.pruebas.presentation.homeScreen

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// drawWithCache: Path y Paint se crean una sola vez y se reutilizan mientras
// el tamaño del composable no cambie. Antes (drawWithContent) se allocaban en cada frame.
fun Modifier.outsideShadow(
    shape: Shape,
    color: Color,
    blur: Dp,
) = this.drawWithCache {
    val outline = shape.createOutline(size, layoutDirection, this)
    val path = Path().apply { addOutline(outline) }
    val frameworkPaint = android.graphics.Paint().apply {
        this.color = color.copy(alpha = 0.35f).toArgb()
        maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
    }
    onDrawWithContent {
        clipPath(path, clipOp = ClipOp.Difference) {
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawPath(path.asAndroidPath(), frameworkPaint)
            }
        }
        drawContent()
    }
}

@Composable
fun BalanceCard(
    balanceState: BalanceState,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(24.dp)

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .outsideShadow(
                shape = cardShape,
                color = Color(0xFF006AC9),
                blur = 16.dp,
            ),
        shape = cardShape,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f),
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)
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