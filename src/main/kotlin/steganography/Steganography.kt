package steganography

import model.Result
import util.rgbToBinaryImage

import java.awt.image.BufferedImage

class Steganography {
    private val mask = 0x00000001

    fun hideText(image: BufferedImage, text: String, binaryImage: Boolean): Result {
        val coverImage = when (binaryImage) {
            true -> rgbToBinaryImage(image)
            false -> image
        }
        var bit: Int
        var x = 0
        var y = 0
        for (element in text) {
            bit = element.code
            for (j in 0..7) {
                val f = bit and mask
                if (f == 1) {
                    if (x < coverImage
                            .width) {
                        coverImage
                            .setRGB(x, y, coverImage
                                .getRGB(x, y) or 0x00000001)
                        x++
                    } else {
                        x = 0
                        y++
                        coverImage
                            .setRGB(x, y, coverImage
                                .getRGB(x, y) or 0x00000001)
                    }
                } else {
                    if (x < coverImage
                            .width) {
                        coverImage
                            .setRGB(x, y, coverImage
                                .getRGB(x, y) and -0x2)
                        x++
                    } else {
                        x = 0
                        y++
                        coverImage
                            .setRGB(x, y, coverImage
                                .getRGB(x, y) and -0x2)
                    }
                }
                bit = bit shr 1
            }
        }
        return Result(coverImage, text.length)
    }

    fun extractText(coverImage: BufferedImage, key: Int): String {
        var x = 0
        var y = 0
        var f: Int
        val c = CharArray(key)
        var message = ""
        for (i in 0 until key) {
            var bit = 0
            for (j in 0..7) {
                if (x < coverImage.width) {
                    f = coverImage.getRGB(x, y) and mask
                    x++
                } else {
                    x = 0
                    y++
                    f = coverImage.getRGB(x, y) and mask
                }

                if (f == 1) {
                    bit = bit shr 1
                    bit = bit or 0x80
                } else {
                    bit = bit shr 1
                }
            }
            c[i] = bit.toChar()
            message += c[i]
        }
        return message
    }
}