package model

data class FileModel(
    val fileName: String?,
    val directory: String?
) {
    fun fullPath(): String? {
        return if (fileName != null && directory != null) {
            directory + fileName
        } else null
    }
}