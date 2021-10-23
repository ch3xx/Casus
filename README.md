# Casus

<p align="center">
  <img src="https://github.com/ch3xx/Casus/blob/main/src/main/resources/CasusLogoLauncher.png" alt="Casus"/>
</p>

[en] Casus is a steganography tool based on Kotlin. Main goal on this project is making steganography as simple as possible.

[tr] Casus, Kotlin temelli bir "Steganografi" tooludur. Bu projedeki amaç resime veri gizleme işlemlerini olabildiğince basit hale getirmektir.
 
# Resime veri ekleme ve çıkarma algoritmaları

```kotlin
fun hideText(image: BufferedImage, inputText: String, binaryImage: Boolean): Result {
        val coverImage = when (binaryImage) {
            true -> rgbToBinaryImage(image)
            false -> image
        }
        val result = base64Util.encode(inputText)
        val text = result.base64
        val key = result.key

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
        return Result(coverImage, key)
    }
```
