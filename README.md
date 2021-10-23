# Casus

<p align="center">
  <img src="https://github.com/ch3xx/Casus/blob/main/src/main/resources/CasusLogoLauncher.png" alt="Casus"/>
</p>

[en] Casus is a steganography tool based on Kotlin. Main goal on this project is making steganography as simple as possible.

[tr] Casus, Kotlin temelli bir "Steganografi" tooludur. Bu projedeki amaç resime veri gizleme işlemlerini olabildiğince basit hale getirmektir.


## Kullanım için [tutorials](https://github.com/ch3xx/Casus/blob/main/tutorials/README.md) içine bakın

 
# Resime veri ekleme ve çıkarma algoritmaları

Resmin içine mesaj gizleme:

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

Resmin içine gizlenen mesajı çıkarma:

```kotlin
fun extractText(coverImage: BufferedImage, key: Int): Resource<String> {
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
        return try {
            Resource.Success(base64Util.decode(message))
        } catch (e: Exception) {
            Resource.Error(ERROR_WRONG_KEY)
        }
    }
```
