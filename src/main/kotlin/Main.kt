import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAwtImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import compose.AlertDialog
import compose.Colors
import compose.FileDialog
import compose.SaveFileDialog
import kotlinx.coroutines.delay
import steganography.Steganography
import util.Constants.SCREEN
import util.Constants.SOLVER_SCREEN
import util.Resource
import java.io.File
import javax.imageio.ImageIO

@Composable
@Preview
fun CasusApp() {
    var selectedImage by rememberSaveable { mutableStateOf(ImageBitmap(0, 0)) }
    var isPictureSelected by rememberSaveable { mutableStateOf(false) }

    var message by rememberSaveable { mutableStateOf("") }
    var fileName by rememberSaveable { mutableStateOf("") }
    var binaryImage by rememberSaveable { mutableStateOf(false) }

    var isDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isSaveDialogOpen by rememberSaveable { mutableStateOf(false) }

    var emptySpacesAlert by rememberSaveable { mutableStateOf(false) }
    var fileFormatAlert by rememberSaveable { mutableStateOf(false) }

    if (emptySpacesAlert) {
        AlertDialog({
            emptySpacesAlert = false
        }, "Lütfen boş alanları doldurun")
    }

    if (fileFormatAlert) {
        AlertDialog({
            fileFormatAlert = false
        }, "Lütfen resim dosyası seçin")
    }

    if (isDialogOpen) {
        FileDialog(
            onCloseRequest = {
                isDialogOpen = false
                it?.let { path ->
                    if (
                        path.endsWith(".png") ||
                        path.endsWith(".jpg") ||
                        path.endsWith(".jpeg") ||
                        path.endsWith(".bmp")
                    ) {
                        try {
                            val file = File(path)
                            val inputStream = file.inputStream()
                            val bitmap = loadImageBitmap(inputStream)
                            selectedImage = bitmap
                            isPictureSelected = true
                        } catch (e: Exception) {
                            println(e)
                        }
                    } else {
                        fileFormatAlert = true
                    }
                }
            }
        )
    }

    if (isSaveDialogOpen) {
        SaveFileDialog(
            onCloseRequest = {
                isSaveDialogOpen = !isSaveDialogOpen
                it?.let { saveDirectory ->
                    try {
                        val bufferedImage = selectedImage.asAwtImage()
                        val steganography = Steganography()
                        val result = steganography.hideText(bufferedImage, message, binaryImage)
                        ImageIO.write(result.image, "png", File("$saveDirectory\\${fileName}.png"))
                    } catch (e: Exception) {
                        println(e)
                    }
                }
            }
        )
    }

    Row(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.White)
            .padding(10.dp)
    ) {
        SelectPictureScreen({ isDialogOpen = true }, isPictureSelected, selectedImage)

        Image(
            painterResource("drawable/line.png"),
            "Line",
            modifier = Modifier.fillMaxHeight(0.95f).width(1.5.dp).align(Alignment.CenterVertically)
        )

        Column(
            modifier = Modifier.fillMaxHeight(0.8f)
                .width(730.dp)
                .padding(top = 16.dp, end = 16.dp, start = 16.dp)
        ) {
            OutlinedTextField(
                message,
                {
                    message = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Resime gizlenecek mesaj")
                }
            )
            Spacer(modifier = Modifier.padding(6.dp))
            OutlinedTextField(
                fileName,
                {
                    fileName = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Dosya ismi")
                }
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Row {
                Checkbox(
                    checked = binaryImage,
                    {
                        binaryImage = it
                    },
                    colors = CheckboxDefaults.colors(checkedColor = Colors.BLUE)
                )
                Text(
                    "Resmi Binary (siyah-beyaz) yap",
                    modifier = Modifier.padding(top = 1.dp, start = 3.dp)
                )
            }
            compose.Button(
                onClick = {
                    if (isPictureSelected &&
                        message.isNotEmpty() &&
                        fileName.isNotEmpty()
                    ) {
                        isSaveDialogOpen = true
                    } else {
                        emptySpacesAlert = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(100.dp).padding(top = 24.dp),
                text = "Kaydet"
            )
        }
    }
}

@Composable
@Preview
fun CasusAppSolver() {
    var message by rememberSaveable { mutableStateOf("") }
    var key by rememberSaveable { mutableStateOf("") }
    var selectedImage by rememberSaveable { mutableStateOf(ImageBitmap(0, 0)) }
    var isPictureSelected by rememberSaveable { mutableStateOf(false) }

    var isDialogOpen by rememberSaveable { mutableStateOf(false) }

    var emptySpacesAlert by rememberSaveable { mutableStateOf(false) }
    var fileFormatAlert by rememberSaveable { mutableStateOf(false) }
    var wrongKeyAlert by rememberSaveable { mutableStateOf(false) }

    if (emptySpacesAlert) {
        AlertDialog({
            emptySpacesAlert = false
        }, "Lütfen boş alanları doldurun")
    }

    if (fileFormatAlert) {
        AlertDialog({
            fileFormatAlert = false
        }, "Lütfen resim dosyası seçin")
    }

    if (wrongKeyAlert) {
        AlertDialog({
            wrongKeyAlert = false
        }, "Yanlış key girdiniz")
    }

    if (isDialogOpen) {
        FileDialog(
            onCloseRequest = {
                isDialogOpen = false
                it?.let { path ->
                    if (
                        path.endsWith(".png") ||
                        path.endsWith(".jpg") ||
                        path.endsWith(".jpeg") ||
                        path.endsWith(".bmp")
                    ) {
                        try {
                            val file = File(path)
                            val inputStream = file.inputStream()
                            val bitmap = loadImageBitmap(inputStream)
                            selectedImage = bitmap
                            isPictureSelected = true
                        } catch (e: Exception) {
                            println(e)
                        }
                    } else {
                        fileFormatAlert = true
                    }
                }
            }
        )
    }

    Row(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.White)
            .padding(10.dp)
    ) {
        SelectPictureScreen(
            onDialogOpenChange = { isDialogOpen = true },
            isPictureSelected,
            selectedImage
        )

        Image(
            painterResource("drawable/line.png"),
            "Line",
            modifier = Modifier.fillMaxHeight(0.95f).width(1.5.dp).align(Alignment.CenterVertically)
        )

        Column(
            modifier = Modifier.fillMaxHeight(0.8f)
                .width(730.dp)
                .padding(top = 16.dp, end = 16.dp, start = 16.dp)
        ) {
            OutlinedTextField(
                key,
                {
                    key = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Key")
                }
            )
            compose.Button(
                onClick = {
                    if (isPictureSelected &&
                        key.isNotEmpty()
                    ) {
                        val steganography = Steganography()
                        when (val resource = steganography.extractText(selectedImage.asAwtImage(), key.toInt())) {
                            is Resource.Success -> {
                                message = resource.data!!
                            }
                            is Resource.Error -> {
                                wrongKeyAlert = true
                            }
                        }
                    } else {
                        emptySpacesAlert = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 24.dp),
                text = "Çıkar"
            )
            Spacer(modifier = Modifier.padding(8.dp))
            if (message.isNotEmpty()) {
                Row {
                    Text("Resimde gizlenmiş mesaj:")
                    Spacer(modifier = Modifier.padding(1.5.dp))
                    Text(message, fontWeight = FontWeight.Bold, color = Colors.BLUE)
                }
            }
        }
    }
}

fun main() = application {
    val icon = painterResource("CasusLogoLauncher.png")
    val logo = painterResource("CasusLogo.jpeg")
    val isPerformingTask = remember { mutableStateOf(true) }
    val screen = remember { mutableStateOf(SCREEN) }

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
        MenuBar {
            if (!isPerformingTask.value) {
                Menu("Dosya") {
                    Item("Gizleme", icon = painterResource("drawable/hide.png"), onClick = {
                        screen.value = SCREEN
                    })
                    Item("Çıkarma", icon = painterResource("drawable/extract.png"), onClick = {
                        screen.value = SOLVER_SCREEN
                    })
                    Item("Çık", icon = painterResource("drawable/exit.png"), onClick = ::exitApplication)
                }
            }
        }

        if (isPerformingTask.value) {
            Box(Modifier.paint(logo).fillMaxSize())
        } else {
            when (screen.value) {
                SCREEN -> CasusApp()
                SOLVER_SCREEN -> CasusAppSolver()
                else -> isPerformingTask.value = true
            }
        }
    }
}

@Composable
fun SelectPictureScreen(onDialogOpenChange: () -> Unit, isPictureSelected: Boolean, selectedImage: ImageBitmap) {
    Column(
        modifier = Modifier.fillMaxHeight()
            .width(430.dp)
            .padding(top = 4.dp, start = 8.dp)
    ) {
        if (isPictureSelected) {
            Image(
                selectedImage,
                "Image",
                modifier = Modifier.width(400.dp).height(450.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            Image(
                painterResource("drawable/image_placeholder.png"),
                "Placeholder",
                modifier = Modifier.width(400.dp).height(450.dp), contentScale = ContentScale.Fit
            )
        }
        compose.Button(
            onClick = onDialogOpenChange,
            modifier = Modifier.width(400.dp).fillMaxHeight().padding(top = 16.dp),
            text = "Resim Seç"
        )
    }
}