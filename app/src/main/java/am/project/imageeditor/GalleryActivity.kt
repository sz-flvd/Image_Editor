package am.project.imageeditor

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import kotlin.collections.ArrayList

const val SELECTED_IMAGES = "SELECTED_IMAGES"

class GalleryActivity : AppCompatActivity() {
    private lateinit var imageList: MutableList<ImageData>
    private var selectedImages: MutableList<String> = ArrayList()
    private lateinit var recView: RecyclerView
    private lateinit var nextButton: Button
    private lateinit var adapter: GalleryAdapter
    private val requestReadExternalStorage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        nextButton = findViewById(R.id.toCollage)

        if(ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), requestReadExternalStorage)

        recView = findViewById(R.id.galleryRecyclerView)

        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            recView.layoutManager = GridLayoutManager(this, 3)
        else recView.layoutManager = GridLayoutManager(this, 6)

        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(recView)

        nextButton.setOnClickListener {
            selectedImages.clear()
            selectedImages.addAll(adapter.getSelectedImages())
            adapter.clearSelectedImages()
            if(selectedImages.isEmpty()) Toast.makeText(this, "No images have been selected\nSelect images by long-clicking", Toast.LENGTH_SHORT).show()
            else {
                val imageUris = selectedImages.toTypedArray()
                val collageIntent = Intent(this, CollageActivity::class.java)
                collageIntent.putExtra(SELECTED_IMAGES, imageUris)
                startActivity(collageIntent)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        imageList = getImagePaths()
        adapter = GalleryAdapter(imageList, this)
        if(imageList.isNotEmpty()) recView.adapter = adapter
    }

    private fun getImagePaths(): ArrayList<ImageData> {
        val images: ArrayList<ImageData> = ArrayList()

        val allImages: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection: Array<String> = arrayOf(
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media._ID)

        val cursor: Cursor? = this.contentResolver.query(allImages, projection, null, null, null)

        try {
            cursor?.moveToFirst()
            do {
                val name: String = cursor!!.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                val date: String = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED))
                val id: Int = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val contentUri: Uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())

                val image = ImageData(name, contentUri, date)

                images.add(image)

            } while(cursor!!.moveToNext())
        } catch(e: Exception) {
            Log.d("am2021", e.message.toString())
        }

        images.sortByDescending {
            it.getDateAdded()
        }

        return images
    }
}
