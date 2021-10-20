import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAwtImage
import androidx.compose.ui.input.key.Key.Companion.Menu
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.useResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import compose.Colors
import kotlinx.coroutines.delay
import steganography.Steganography
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import javax.imageio.ImageIO

const val SCREEN = 1
const val SOLVER_SCREEN = 2

@Composable
@Preview
fun CasusApp() {
    val selectedImage = remember { mutableStateOf(ImageBitmap(0, 0)) }
    val isPictureSelected = remember { mutableStateOf(false) }

    val message = remember { mutableStateOf("") }
    val fileName = remember { mutableStateOf("") }
    val binaryImage = remember { mutableStateOf(false) }

    val isDialogOpen = remember { mutableStateOf(false) }
    val isSaveDialogOpen = remember { mutableStateOf(false) }

    val emptySpacesAlert = remember { mutableStateOf(false) }
    val fileFormatAlert = remember { mutableStateOf(false) }

    if (emptySpacesAlert.value) {
        Dialog(
            onCloseRequest = {
                emptySpacesAlert.value = false
            },
            title = "Uyarı",
            resizable = false
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    "Lütfen boş alanları doldurun",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    if (fileFormatAlert.value) {
        Dialog(
            onCloseRequest = {
                fileFormatAlert.value = false
            },
            title = "Uyarı",
            resizable = false
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    "Lütfen resim dosyası seçin",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    if (isDialogOpen.value) {
        FileDialog(
            onCloseRequest = {
                isDialogOpen.value = false
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
                            selectedImage.value = bitmap
                            isPictureSelected.value = true
                        } catch (e: Exception) {
                            println(e)
                        }
                    } else {
                        fileFormatAlert.value = true
                    }
                }
            }
        )
    }

    if (isSaveDialogOpen.value) {
        SaveFileDialog(
            onCloseRequest = {
                isSaveDialogOpen.value = !isSaveDialogOpen.value
                it?.let { saveDirectory ->
                    print(saveDirectory)
                    try {
                        val bufferedImage = selectedImage.value.asAwtImage()
                        val steganography = Steganography()
                        val result = steganography.hideText(bufferedImage, message.value, binaryImage.value)
                        ImageIO.write(result.image, "png", File("$saveDirectory\\${fileName.value}.png"))
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
        Column(
            modifier = Modifier.fillMaxHeight()
                .width(430.dp)
                .padding(top = 4.dp, start = 8.dp)
        ) {
            if (isPictureSelected.value) {
                Image(
                    selectedImage.value,
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
            Button(
                onClick = {
                    isDialogOpen.value = true
                },
                shape = RoundedCornerShape(size = 6.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Colors.BLUE),
                modifier = Modifier.width(400.dp)
                    .fillMaxHeight()
                    .padding(top = 16.dp)
            ) {
                Text("Resim Seç", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

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
                message.value,
                {
                    message.value = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Resime gizlenecek mesaj")
                }
            )
            Spacer(modifier = Modifier.padding(8.dp))
            OutlinedTextField(
                fileName.value,
                {
                    fileName.value = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Dosya ismi")
                }
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Row {
                Checkbox(
                    checked = binaryImage.value,
                    {
                        binaryImage.value = it
                    },
                    colors = CheckboxDefaults.colors(checkedColor = Colors.BLUE)
                )
                Text(
                    "Resmi Binary (siyah-beyaz) yap",
                    modifier = Modifier.padding(top = 1.dp, start = 3.dp)
                )
            }
            Button(
                onClick = {
                    if (isPictureSelected.value &&
                        message.value.isNotEmpty() &&
                        fileName.value.isNotEmpty()
                    ) {
                        isSaveDialogOpen.value = true
                    } else {
                        emptySpacesAlert.value = true
                    }
                },
                shape = RoundedCornerShape(size = 6.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Colors.BLUE),
                modifier = Modifier.fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 32.dp)
            ) {
                Text("Kaydet", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
@Preview
fun CasusAppSolver() {
    val message = remember { mutableStateOf("") }
    val key = remember { mutableStateOf("") }
    val selectedImage = remember { mutableStateOf(ImageBitmap(0, 0)) }
    val isPictureSelected = remember { mutableStateOf(false) }

    val isDialogOpen = remember { mutableStateOf(false) }

    val emptySpacesAlert = remember { mutableStateOf(false) }
    val fileFormatAlert = remember { mutableStateOf(false) }

    if (emptySpacesAlert.value) {
        Dialog(
            onCloseRequest = {
                emptySpacesAlert.value = false
            },
            title = "Uyarı",
            resizable = false
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    "Lütfen boş alanları doldurun",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    if (fileFormatAlert.value) {
        Dialog(
            onCloseRequest = {
                fileFormatAlert.value = false
            },
            title = "Uyarı",
            resizable = false
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    "Lütfen resim dosyası seçin",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    if (isDialogOpen.value) {
        FileDialog(
            onCloseRequest = {
                isDialogOpen.value = false
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
                            selectedImage.value = bitmap
                            isPictureSelected.value = true
                        } catch (e: Exception) {
                            println(e)
                        }
                    } else {
                        fileFormatAlert.value = true
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
        Column(
            modifier = Modifier.fillMaxHeight()
                .width(430.dp)
                .padding(top = 4.dp, start = 8.dp)
        ) {
            if (isPictureSelected.value) {
                Image(
                    selectedImage.value,
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
            Button(
                onClick = {
                    isDialogOpen.value = true
                },
                shape = RoundedCornerShape(size = 6.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Colors.BLUE),
                modifier = Modifier.width(400.dp)
                    .fillMaxHeight()
                    .padding(top = 16.dp)
            ) {
                Text("Resim Seç", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

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
                key.value,
                {
                    key.value = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Key")
                }
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Button(
                onClick = {
                    if (isPictureSelected.value &&
                        key.value.isNotEmpty()
                    ) {
                        val steganography = Steganography()
                        message.value = steganography.extractText(selectedImage.value.asAwtImage(), key.value.toInt())
                    } else {
                        emptySpacesAlert.value = true
                    }
                },
                shape = RoundedCornerShape(size = 6.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Colors.BLUE),
                modifier = Modifier.fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 32.dp)
            ) {
                Text("Çıkar", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.padding(8.dp))
            if (message.value.isNotEmpty()) {
                Text("Resimde gizlenmiş mesaj: ${message.value}", fontWeight = FontWeight.Bold, color = Colors.BLUE)
            }
        }
    }
}

@Composable
private fun FileDialog(
    parent: Frame? = null,
    onCloseRequest: (result: String?) -> Unit
) = AwtWindow(
    create = {
        object : java.awt.FileDialog(parent, "Bir resim seçin", LOAD) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    onCloseRequest(directory + file)
                }
            }
        }
    },
    dispose = FileDialog::dispose
)

@Composable
private fun SaveFileDialog(
    parent: Frame? = null,
    onCloseRequest: (result: String?) -> Unit
) = AwtWindow(
    create = {
        object : java.awt.FileDialog(parent, "Kaydedilecek yeri seçin", SAVE) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    onCloseRequest(directory)
                }
            }
        }
    },
    dispose = FileDialog::dispose
)

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
            Menu("File") {
                Item("Gizleme", onClick = {
                    screen.value = SCREEN
                })
                Item("Çıkarma", onClick = {
                    screen.value = SOLVER_SCREEN
                })
                Item("Çık", onClick = ::exitApplication)
            }
        }

        if (isPerformingTask.value) {
            Box(Modifier.paint(logo).fillMaxSize())
        } else {
            if (screen.value == SCREEN) CasusApp()
            else if (screen.value == SOLVER_SCREEN) CasusAppSolver()
            else isPerformingTask.value = true
        }
    }
}