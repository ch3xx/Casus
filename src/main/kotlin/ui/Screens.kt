package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAwtImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.*
import steganography.Steganography
import util.Resource
import java.awt.FileDialog
import java.awt.FileDialog.SAVE
import java.io.File
import javax.imageio.ImageIO

@Composable
fun SelectImageScreen(
    onSelectedImageChange: (ImageBitmap) -> Unit,
    onShowingAlert: (String) -> Unit,
    selectedImage: ImageBitmap,
    imageSelected: Boolean
) {
    var selectFileDialog by rememberSaveable { mutableStateOf(false) }
    var fileName by rememberSaveable { mutableStateOf("") }

    if (selectFileDialog) {
        FileDialog(
            null,
            FileDialog.LOAD,
            onCloseRequest = { fileResponse ->
                selectFileDialog = false
                val path = fileResponse.fullPath()
                path?.let {
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
                            onSelectedImageChange(bitmap)
                            fileResponse.fileName?.let {
                                fileName = it
                            }
                        } catch (e: Exception) {
                            onShowingAlert("Resim se??ilirken bir hata olu??tu")
                        }
                    } else {
                        fileResponse.fileName?.let {
                            if (it.isNotEmpty()) onShowingAlert("L??tfen resim dosyas?? se??in")
                        }
                    }
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxHeight(0.97f)
            .width(430.dp)
            .padding(top = 4.dp, start = 8.dp)
    ) {
        if (imageSelected && fileName.isNotEmpty()) {
            Text(
                fileName,
                fontWeight = FontWeight.Bold,
                color = Colors.Orange,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp, bottom = 8.dp)
            )
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
        Button(
            onClick = { selectFileDialog = true },
            modifier = Modifier.width(400.dp).fillMaxHeight().padding(top = 16.dp),
            text = "Resim Se??"
        )
    }
}

@Composable
fun HidingScreen(
    onShowingAlert: (String) -> Unit,
    selectedImage: ImageBitmap,
    imageSelected: Boolean
) {
    var message by rememberSaveable { mutableStateOf("") }
    var binaryImage by rememberSaveable { mutableStateOf(false) }
    var saveFileDialog by rememberSaveable { mutableStateOf(false) }

    if (saveFileDialog) {
        FileDialog(
            null,
            SAVE,
            onCloseRequest = {
                saveFileDialog = false
                val path = it.fullPath()
                path?.let {
                    try {
                        val bufferedImage = selectedImage.asAwtImage()
                        val steganography = Steganography()
                        val result = steganography.hideText(bufferedImage, message, binaryImage)
                        ImageIO.write(result.image, "png", File("$path.png"))
                        onShowingAlert("Gizleme ba??ar??l?? bir ??ekilde ger??ekle??ti\nKey: ${result.key}")
                    } catch (e: Exception) {
                        onShowingAlert("Gizleme s??ras??nda bir hata olu??tu")
                    }
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxHeight()
            .width(730.dp)
            .padding(top = 16.dp, end = 16.dp, start = 16.dp)
    ) {
        TextField({ message = it }, "Resime gizlenecek mesaj", message)
        Spacer(modifier = Modifier.padding(8.dp))
        CheckBox({ binaryImage = it }, binaryImage, "Resmi binary (siyah-beyaz) yap")
        Spacer(modifier = Modifier.padding(4.dp))
        Button(
            onClick = {
                if (imageSelected &&
                    message.isNotEmpty()
                ) {
                    saveFileDialog = true
                } else {
                    onShowingAlert("L??tfen bo?? alanlar?? doldurun")
                }
            },
            modifier = Modifier.fillMaxWidth().height(84.dp).padding(top = 8.dp),
            text = "Kaydet"
        )
    }
}

@Composable
fun ExtractionScreen(
    onShowingAlert: (String) -> Unit,
    selectedImage: ImageBitmap,
    imageSelected: Boolean
) {
    var message by rememberSaveable { mutableStateOf("") }
    var key by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxHeight()
            .width(730.dp)
            .padding(top = 16.dp, end = 16.dp, start = 16.dp)
    ) {
        TextField({ key = it }, "Key", key)
        Spacer(modifier = Modifier.padding(4.dp))
        Button(
            onClick = {
                if (imageSelected &&
                    key.isNotEmpty()
                ) {
                    val steganography = Steganography()
                    var keyValue = 0
                    try {
                        keyValue = key.toInt()
                    } catch (e: Exception) {
                        onShowingAlert("Key de??eri say?? olmal??d??r")
                    }
                    when (val resource = steganography.extractText(selectedImage.asAwtImage(), keyValue)) {
                        is Resource.Success -> {
                            message = resource.data!!
                        }
                        is Resource.Error -> {
                            message = ""
                            onShowingAlert("Yanl???? key girdiniz")
                        }
                    }
                } else {
                    onShowingAlert("L??tfen bo?? alanlar?? doldurun")
                }
            },
            modifier = Modifier.fillMaxWidth()
                .height(84.dp)
                .padding(top = 8.dp),
            text = "????kar"
        )
        Spacer(modifier = Modifier.padding(4.dp))
        if (message.isNotEmpty()) {
            Row {
                Text("Resimde gizlenmi?? mesaj:", fontSize = 18.sp)
                Spacer(modifier = Modifier.padding(1.5.dp))
                Text(message, fontWeight = FontWeight.Bold, color = Colors.Orange, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun BruteForceScreen(
    onShowingAlert: (String) -> Unit,
    selectedImage: ImageBitmap,
    imageSelected: Boolean
) {
    var maxTry by rememberSaveable { mutableStateOf("") }

    var results by rememberSaveable { mutableStateOf(mutableListOf<String>()) }
    var bruteForceDone by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxHeight(0.97f)
            .width(730.dp)
            .padding(top = 16.dp, end = 16.dp, start = 16.dp)
    ) {
        TextField({ maxTry = it }, "Deneme say??s?? (max)", maxTry)
        Spacer(modifier = Modifier.padding(4.dp))
        Button(
            onClick = {
                if (imageSelected &&
                    maxTry.isNotEmpty()
                ) {
                    results = mutableListOf()
                    bruteForceDone = false
                    val steganography = Steganography()
                    var maxTryValue = 0
                    try {
                        maxTryValue = maxTry.toInt()
                        val bufferedImage = selectedImage.asAwtImage()

                        for (i in 0 until maxTryValue) {
                            when (val resource = steganography.extractText(bufferedImage, i)) {
                                is Resource.Success -> {
                                    results.add(resource.data!!)
                                }
                                is Resource.Error -> {
                                }
                            }
                        }
                        bruteForceDone = true
                    } catch (e: Exception) {
                        onShowingAlert("Deneme say??s??, ad?? ??st??nde, say?? olmal??d??r")
                    }
                } else {
                    onShowingAlert("L??tfen bo?? alanlar?? doldurun")
                }
            },
            modifier = Modifier.fillMaxWidth().height(84.dp).padding(top = 8.dp),
            text = "Ba??lat"
        )

        if (bruteForceDone) {
            val resultList = results.reversed()
            Text(
                "Sonu??lar",
                fontWeight = FontWeight.Bold,
                color = Colors.Orange,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp, bottom = 8.dp)
            )
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {

                val state = rememberLazyListState()

                LazyColumn(state = state) {
                    items(resultList) { result ->
                        Text(result)
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = state
                    )
                )
            }
        }
    }
}