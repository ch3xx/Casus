import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import compose.AlertDialog
import compose.Colors
import compose.SelectFileDialog
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
    var message by rememberSaveable { mutableStateOf("") }
    var fileName by rememberSaveable { mutableStateOf("") }
    var binaryImage by rememberSaveable { mutableStateOf(false) }

    var selectedImage by rememberSaveable { mutableStateOf(ImageBitmap(0, 0)) }
    var imageSelected by rememberSaveable { mutableStateOf(false) }

    var selectFileDialog by rememberSaveable { mutableStateOf(false) }
    var saveFileDialog by rememberSaveable { mutableStateOf(false) }

    var showingAlert by rememberSaveable { mutableStateOf(false) }
    var alertMessage by rememberSaveable { mutableStateOf("") }

    if (showingAlert) {
        AlertDialog({
            showingAlert = false
            alertMessage = ""
        }, alertMessage)
    }

    if (selectFileDialog) {
        SelectFileDialog(
            onCloseRequest = {
                selectFileDialog = false
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
                            imageSelected = true
                        } catch (e: Exception) {
                            println(e)
                        }
                    } else {
                        showingAlert = true
                        alertMessage = "Lütfen resim dosyası seçin"
                    }
                }
            }
        )
    }

    if (saveFileDialog) {
        SaveFileDialog(
            onCloseRequest = {
                saveFileDialog = !saveFileDialog
                it?.let { saveDirectory ->
                    try {
                        val bufferedImage = selectedImage.asAwtImage()
                        val steganography = Steganography()
                        val result = steganography.hideText(bufferedImage, message, binaryImage)
                        ImageIO.write(result.image, "png", File("$saveDirectory\\${fileName}.png"))
                        showingAlert = true
                        alertMessage = "Gizleme başarılı bir şekilde gerçekleşti\nKey: ${result.key}"
                    } catch (e: Exception) {
                        showingAlert = true
                        alertMessage = "Gizleme sırasında bir hata oluştu"
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
        SelectPictureScreen({ selectFileDialog = true }, imageSelected, selectedImage)

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
            compose.TextField({ message = it }, "Resime gizlenecek mesaj", message)
            Spacer(modifier = Modifier.padding(6.dp))
            compose.TextField({ fileName = it }, "Dosya ismi", fileName)
            Spacer(modifier = Modifier.padding(8.dp))
            Row {
                Checkbox(
                    checked = binaryImage,
                    {
                        binaryImage = it
                    },
                    colors = CheckboxDefaults.colors(checkedColor = Colors.Blue)
                )
                Text(
                    "Resmi Binary (siyah-beyaz) yap",
                    modifier = Modifier.padding(top = 1.dp, start = 3.dp)
                )
            }
            compose.Button(
                onClick = {
                    if (imageSelected &&
                        message.isNotEmpty() &&
                        fileName.isNotEmpty()
                    ) {
                        saveFileDialog = true
                    } else {
                        showingAlert = true
                        alertMessage = "Lütfen boş alanları doldurun"
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
    var imageSelected by rememberSaveable { mutableStateOf(false) }

    var selectFileDialog by rememberSaveable { mutableStateOf(false) }

    var alert by rememberSaveable { mutableStateOf(false) }
    var alertMessage by rememberSaveable { mutableStateOf("") }

    if (alert) {
        AlertDialog({
            alert = false
            alertMessage = ""
        }, alertMessage)
    }

    if (selectFileDialog) {
        SelectFileDialog(
            onCloseRequest = {
                selectFileDialog = false
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
                            imageSelected = true
                        } catch (e: Exception) {
                            println(e)
                        }
                    } else {
                        alert = true
                        alertMessage = "Lütfen resim dosyası seçin"
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
            selectDialog = { selectFileDialog = true },
            imageSelected,
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
            compose.TextField({ key = it }, "Key", key)
            compose.Button(
                onClick = {
                    if (imageSelected &&
                        key.isNotEmpty()
                    ) {
                        val steganography = Steganography()
                        var keyValue = 0
                        try {
                            keyValue = key.toInt()
                        } catch (e: Exception) {
                            alert = true
                            alertMessage = "Key değeri sayı olmalıdır"
                        }
                        when (val resource = steganography.extractText(selectedImage.asAwtImage(), keyValue)) {
                            is Resource.Success -> {
                                message = resource.data!!
                            }
                            is Resource.Error -> {
                                message = ""
                                alert = true
                                alertMessage = "Yanlış key girdiniz"
                            }
                        }
                    } else {
                        alert = true
                        alertMessage = "Lütfen boş alanları doldurun"
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
                    Text("Resimde gizlenmiş mesaj:", fontSize = 18.sp)
                    Spacer(modifier = Modifier.padding(1.5.dp))
                    Text(message, fontWeight = FontWeight.Bold, color = Colors.Blue, fontSize = 18.sp)
                }
            }
        }
    }
}

fun main() = application {
    val icon = painterResource("CasusLogoLauncher.png")
    val logo = painterResource("CasusLogo.jpeg")

    var performingTask by rememberSaveable { mutableStateOf(true) }
    var screen by rememberSaveable { mutableStateOf(SCREEN) }

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
                        screen = SCREEN
                    })
                    Item("Çıkarma", icon = painterResource("drawable/extract.png"), onClick = {
                        screen = SOLVER_SCREEN
                    })
                    Item("Çık", icon = painterResource("drawable/exit.png"), onClick = ::exitApplication)
                }
            }
        }

        if (performingTask) {
            Box(Modifier.paint(logo).fillMaxSize())
        } else {
            when (screen) {
                SCREEN -> CasusApp()
                SOLVER_SCREEN -> CasusAppSolver()
                else -> exitApplication()
            }
        }
    }
}

@Composable
fun SelectPictureScreen(selectDialog: () -> Unit, imageSelected: Boolean, selectedImage: ImageBitmap) {
    Column(
        modifier = Modifier.fillMaxHeight(0.97f)
            .width(430.dp)
            .padding(top = 4.dp, start = 8.dp)
    ) {
        if (imageSelected) {
            Image(
                selectedImage,
                "Image",
                modifier = Modifier.width(400.dp).fillMaxHeight(0.83f),
                contentScale = ContentScale.Fit
            )
        } else {
            Image(
                painterResource("drawable/image_placeholder.png"),
                "Placeholder",
                modifier = Modifier.width(400.dp).fillMaxHeight(0.83f),
                contentScale = ContentScale.Fit
            )
        }
        compose.Button(
            onClick = selectDialog,
            modifier = Modifier.width(400.dp).fillMaxHeight().padding(top = 16.dp),
            text = "Resim Seç"
        )
    }
}