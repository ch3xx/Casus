package compose

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Button(onClick: () -> Unit, modifier: Modifier, text: String) {
    androidx.compose.material.Button(
        onClick = onClick,
        shape = RoundedCornerShape(size = 6.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Colors.BLUE),
        modifier = modifier
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}