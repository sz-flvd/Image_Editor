package am.project.imageeditor

import android.net.Uri

class ImageData(name: String, uri: Uri, date: String) {
    private var imageName: String = name
    private var imageUri: Uri = uri
    private var dateAdded: String = date

    fun setImageName(name: String) {
        imageName = name
    }

    fun setImageUri(uri: Uri) {
        imageUri = uri
    }

    fun setDateAdded(date: String) {
        dateAdded = date
    }

    fun getImageName(): String {
        return imageName
    }

    fun getImageUri(): Uri {
        return imageUri
    }

    fun getDateAdded(): String {
        return dateAdded
    }
}
