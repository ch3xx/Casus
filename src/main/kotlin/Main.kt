import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import compose.*
import kotlinx.coroutines.delay
import ui.BruteForceScreen
import ui.ExtractionScreen
import ui.HidingScreen
import ui.SelectImageScreen
import util.Constants.BRUTE_FORCE_SCREEN
import util.Constants.HIDING_SCREEN
import util.Constants.EXTRACTION_SCREEN

@Composable
@Preview
private fun CasusApp(screen: Int) {
    var selectedImage by rememberSaveable { mutableStateOf(ImageBitmap(0, 0)) }
    var imageSelected by rememberSaveable { mutableStateOf(false) }

    var showingAlert by rememberSaveable { mutableStateOf(false) }
    var alertMessage by rememberSaveable { mutableStateOf("") }

    if (showingAlert) {
        AlertDialog({
            showingAlert = false
            alertMessage = ""
        }, alertMessage)
    }

    Row(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.White)
            .padding(10.dp)
    ) {
        SelectImageScreen(
            {
                selectedImage = it
                imageSelected = true
            },
            {
                showingAlert = true
                alertMessage = it
            },
            selectedImage,
            imageSelected
        )

        Image(
            painterResource("drawable/line.png"),
            "Line",
            modifier = Modifier.fillMaxHeight(0.95f).width(1.5.dp).align(Alignment.CenterVertically)
        )

        if (screen == HIDING_SCREEN) {
            HidingScreen({
                showingAlert = true
                alertMessage = it
            }, selectedImage, imageSelected)
        } else if(screen == EXTRACTION_SCREEN) {
            ExtractionScreen({
                showingAlert = true
                alertMessage = it
            }, selectedImage, imageSelected)
        } else if(screen == BRUTE_FORCE_SCREEN) {
            BruteForceScreen({
                showingAlert = true
                alertMessage = it
            }, selectedImage, imageSelected)
        }
    }
}

fun main() = application {
    val icon = painterResource("CasusLogoLauncher.png")
    val logo = painterResource("CasusLogo.jpeg")

    var performingTask by rememberSaveable { mutableStateOf(true) }
    var screen by rememberSaveable { mutableStateOf(HIDING_SCREEN) }

    LaunchedEffect(Unit) {
        delay(2000)
        performingTask = false
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
        MenuBar {
            if (!performingTask) {
                Menu("Dosya") {
                    Item("Gizleme", icon = painterResource("drawable/hide.png"), onClick = {
                        screen = HIDING_SCREEN
                    })
                    Item("Çıkarma", icon = painterResource("drawable/extract.png"), onClick = {
                        screen = EXTRACTION_SCREEN
                    })
                    Item("Brute Force", icon = painterResource("drawable/brute_force.png"), onClick = {
                        screen = BRUTE_FORCE_SCREEN
                    })
                    Item("Çık", icon = painterResource("drawable/exit.png"), onClick = ::exitApplication)
                }
            }
        }

        if (performingTask) {
            Box(Modifier.paint(logo).fillMaxSize())
        } else {
            CasusApp((screen))
        }
    }
}