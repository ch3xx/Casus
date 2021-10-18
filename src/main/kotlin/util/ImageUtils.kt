package util

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun getImage(path: String): BufferedImage = ImageIO.read(File(path))

fun rgbToBinaryImage(image: BufferedImage): BufferedImage {
    val h = image.height
    val w = image.width
    val bufferedImage = BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
    for (iW in 0 until w) {
        for (iH in 0 until h) {
            val value = image.getRGB(iW, iH)
            val r = 0x00ff0000 and value shr 16
            val g = 0x0000ff00 and value shr 8
            val b = 0x000000ff and value

            val m = r + g + b
            if (m >= 383) {
                bufferedImage.setRGB(iW, iH, Color.WHITE.rgb)
            } else {
                bufferedImage.setRGB(iW, iH, 0)
            }
        }
    }
    return bufferedImage
}