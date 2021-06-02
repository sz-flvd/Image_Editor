package am.project.imageeditor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

const val EXTRA_URI = "IMAGE_URI"
const val EXTRA_NAME = "IMAGE_NAME"

class GalleryAdapter(private val data: List<ImageData>, private val context: Context): RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    private var selectedImages: MutableList<String> = ArrayList()

    class ViewHolder(view: View, list: MutableList<String>): RecyclerView.ViewHolder(view) {
        lateinit var context: Context

        val imageView: ImageView = view.findViewById(R.id.imageView)

        lateinit var imageName: String
        lateinit var imageUri: Uri

        init {
            view.setOnClickListener {
                val intent = Intent(context, ImageActivity::class.java)
                intent.putExtra(EXTRA_URI, imageUri.toString())
                intent.putExtra(EXTRA_NAME, imageName)
                ContextCompat.startActivity(context as Activity, intent, null)
            }

            view.setOnLongClickListener {
                if(imageView.background == null) {
                    imageView.setBackgroundResource(R.drawable.selected_item_background)
                    list.add(imageUri.toString())
                }
                else {
                    imageView.background = null
                    imageView.setPadding(0, 0, 0, 0)
                    list.remove(imageUri.toString())
                }
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_view, parent, false)
        return ViewHolder(view, selectedImages)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.context = context
        holder.imageName = data[position].getImageName()
        holder.imageUri = data[position].getImageUri()

        holder.imageView.contentDescription = holder.imageName

        Glide.with(context)
            .load(holder.imageUri)
            .override(100, 100)
            .centerCrop()
            .thumbnail(0.3f)
            .into(holder.imageView)
    }

    fun getSelectedImages(): List<String> {
        return selectedImages
    }
}
