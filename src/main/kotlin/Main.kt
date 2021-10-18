import androidx.compose.animation.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import kotlinx.coroutines.delay

@Composable
@Preview
fun CasusApp() {
    val selectedImage = painterResource("drawable/image_placeholder.png")
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.White)
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxHeight()
                .width(470.dp)
                .padding(top = 4.dp, start = 8.dp)
        ) {
            Column {
                Image(selectedImage, "", modifier = Modifier.width(400.dp).height(450.dp))
                Button(onClick = {},
                    shape = RoundedCornerShape(size = 4.dp),
                    modifier = Modifier.width(410.dp)
                        .fillMaxHeight()
                        .padding(top = 24.dp)
                ) {
                    Text("Resim Seç")
                }
            }
        }
    }
}

fun main() = application {
    val icon = painterResource("CasusLogoLauncher.png")
    val logo = painterResource("CasusLogo.jpeg")
    val isPerformingTask = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000)
        isPerformingTask.value = false
    }

    Tray(
        icon = icon,
        menu = {
            Item("Çık", onClick = ::exitApplication)
        }
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "Casus",
        icon = icon,
        state = WindowState(
            width = 1200.dp,
            height = 600.dp,
        ),
        resizable = false
    ) {
        if (isPerformingTask.value) {
            Box(Modifier.paint(logo).fillMaxSize())
        } else {
            CasusApp()
        }
    }
}