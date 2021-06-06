package am.project.imageeditor

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CollageActivity : AppCompatActivity() {
    private lateinit var collageImages: Array<String>
    private lateinit var layout: ConstraintLayout
    private lateinit var makeCollageButton: Button
    private lateinit var changeColourButton: Button
    private lateinit var colourChangedButton: Button
    private lateinit var redSeekBar: SeekBar
    private lateinit var greenSeekBar: SeekBar
    private lateinit var blueSeekBar: SeekBar
    private lateinit var alphaSeekBar: SeekBar
    private var red: Int = 0
    private var green: Int = 0
    private var blue: Int = 0
    private var alpha: Int = 0
    private val requestWriteExternalStorage = 1

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collage)

        if(ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), requestWriteExternalStorage)

        layout = findViewById(R.id.collageLayout)

        makeCollageButton = findViewById(R.id.makeCollage)
        makeCollageButton.setOnClickListener {
            makeCollage(layout)
        }

        changeColourButton = findViewById(R.id.changeColour)
        changeColourButton.setOnClickListener {
            changeBackgroundColour()
        }

        colourChangedButton = findViewById(R.id.colourChanged)
        colourChangedButton.isVisible = false
        colourChangedButton.setOnClickListener {
            doneChangingColour()
        }

        redSeekBar = findViewById(R.id.redSeekBar)
        redSeekBar.isVisible = false
        redSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                red = progress
                layout.setBackgroundColor(Color.argb(alpha, red, green, blue))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        red = redSeekBar.progress

        greenSeekBar = findViewById(R.id.greenSeekBar)
        greenSeekBar.isVisible = false
        greenSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                green = progress
                layout.setBackgroundColor(Color.argb(alpha, red, green, blue))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        green = greenSeekBar.progress

        blueSeekBar = findViewById(R.id.blueSeekBar)
        blueSeekBar.isVisible = false
        blueSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                blue = progress
                layout.setBackgroundColor(Color.argb(alpha, red, green, blue))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        blue = blueSeekBar.progress

        alphaSeekBar = findViewById(R.id.alphaSeekBar)
        alphaSeekBar.isVisible = false
        alphaSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                alpha = progress
                layout.setBackgroundColor(Color.argb(alpha, red, green, blue))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        alpha = alphaSeekBar.progress

        val bundle: Bundle? = intent.extras
        collageImages = bundle?.getStringArray(SELECTED_IMAGES)!!

        for(i in collageImages.indices) {
            val imageView = ImageView(this)
            imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            imageView.x = 0f
            imageView.y = 300f * i
            imageView.adjustViewBounds = true
            imageView.setOnTouchListener(View.OnTouchListener { view, event ->
                view.bringToFront()
                view.x = event.rawX - view.width / 2
                view.y = event.rawY - view.height / 2
                return@OnTouchListener true
            })
            Glide.with(this).load(collageImages[i].toUri()).into(imageView)
            layout.addView(imageView)
        }
    }

    private fun changeBackgroundColour() {
        changeColourButton.isVisible = false
        makeCollageButton.isVisible = false
        colourChangedButton.isVisible = true
        colourChangedButton.bringToFront()
        redSeekBar.isVisible = true
        greenSeekBar.isVisible = true
        blueSeekBar.isVisible = true
        alphaSeekBar.isVisible = true
    }

    private fun doneChangingColour() {
        makeCollageButton.isVisible = true
        changeColourButton.isVisible = true
        colourChangedButton.isVisible = false
        redSeekBar.isVisible = false
        greenSeekBar.isVisible = false
        blueSeekBar.isVisible = false
        alphaSeekBar.isVisible = false
    }

    private fun makeCollage(view: View) {
        makeCollageButton.isVisible = false
        changeColourButton.isVisible = false
        val finalBitmap: Bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(finalBitmap)
        val background: Drawable = view.background
        background.draw(canvas)
        view.draw(canvas)

        val pathname = getString(R.string.pathname)
        val folder = File(pathname)

        if(!folder.exists()) folder.mkdirs()

        val filename = "edited${System.currentTimeMillis()}.png"

        try {
            val out = FileOutputStream(File("$pathname/$filename"))
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            Toast.makeText(this, "Collage created successfully!", Toast.LENGTH_SHORT).show()
        } catch(e: IOException) {
            Log.e("am2021", e.message.toString())
        }

        finish()
    }
}
