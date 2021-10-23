package compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Button(onClick: () -> Unit, modifier: Modifier, text: String) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(size = 6.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Colors.Pink),
        modifier = modifier
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TextField(onTextChanged: (String) -> Unit, label: String, text: String) {
    OutlinedTextField(
        text,
        onValueChange = onTextChanged,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(label)
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            cursorColor = Colors.Orange,
            focusedLabelColor = Colors.Orange,
            focusedBorderColor = Colors.Orange
        )
    )
}

@Composable
fun CheckBox(onChecked: (Boolean) -> Unit, checked: Boolean, text: String) {
    Row {
        Checkbox(
            checked = checked,
            onCheckedChange = onChecked,
            colors = CheckboxDefaults.colors(checkedColor = Colors.Orange)
        )
        Text(
            text,
            modifier = Modifier.padding(top = 1.dp, start = 3.dp)
        )
    }
}