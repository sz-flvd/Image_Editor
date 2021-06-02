package am.project.imageeditor

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

const val STATE_FRAME_VALUES = "frameColourValues"
const val STATE_VISIBILITY_VALUES = "uiVisibilityValues"

class ImageActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var addFrameButton: Button
    private lateinit var saveFrameButton: Button
    private lateinit var cancelFrameButton: Button
    private lateinit var redSeekBar: SeekBar
    private lateinit var greenSeekBar: SeekBar
    private lateinit var blueSeekBar: SeekBar
    private lateinit var alphaSeekBar: SeekBar
    private lateinit var paddingSeekBar: SeekBar
    private var red: Int = 0
    private var green: Int = 0
    private var blue: Int = 0
    private var alpha: Int = 0
    private var padding: Int = 0
    private var settingFrame: Boolean = false
    private val requestWriteExternalStorage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        if(ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), requestWriteExternalStorage)

        val uri: Uri? = intent.getStringExtra(EXTRA_URI)?.toUri()
        val desc: String? = intent.getStringExtra(EXTRA_NAME)

        imageView = findViewById(R.id.expandedImage)
        Glide.with(this).load(uri).into(imageView)
        imageView.contentDescription = desc
        imageView.setOnClickListener {
            if(!settingFrame) addFrameButton.isVisible = !addFrameButton.isVisible
            else changeUIState(!saveFrameButton.isVisible)
        }

        addFrameButton = findViewById(R.id.addFrame)
        addFrameButton.setOnClickListener {
            changeUIState(true)
            addFrameButton.isVisible = false
            settingFrame = true
        }

        saveFrameButton = findViewById(R.id.saveFrame)
        saveFrameButton.isVisible = false
        saveFrameButton.setOnClickListener {
            changeUIState(false)
            addFrameButton.isVisible = false
            settingFrame = false
            saveImage()
        }

        cancelFrameButton = findViewById(R.id.cancelFrame)
        cancelFrameButton.isVisible = false
        cancelFrameButton.setOnClickListener {
            settingFrame = false
            changeUIState(false)
            addFrameButton.isVisible = true
            cancelFrameChanges()
        }

        redSeekBar = findViewById(R.id.redFrameSeekBar)
        redSeekBar.isVisible = false
        redSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                red = progress
                imageView.setBackgroundColor(Color.argb(alpha, red, green, blue))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        red = redSeekBar.progress

        greenSeekBar = findViewById(R.id.greenFrameSeekBar)
        greenSeekBar.isVisible = false
        greenSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                green = progress
                imageView.setBackgroundColor(Color.argb(alpha, red, green, blue))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        green = greenSeekBar.progress

        blueSeekBar = findViewById(R.id.blueFrameSeekBar)
        blueSeekBar.isVisible = false
        blueSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                blue = progress
                imageView.setBackgroundColor(Color.argb(alpha, red, green, blue))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        blue = blueSeekBar.progress

        alphaSeekBar = findViewById(R.id.alphaFrameSeekBar)
        alphaSeekBar.isVisible = false
        alphaSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                alpha = progress
                imageView.setBackgroundColor(Color.argb(alpha, red, green, blue))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        alpha = alphaSeekBar.progress

        paddingSeekBar = findViewById(R.id.paddingSeekBar)
        paddingSeekBar.isVisible = false
        paddingSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                padding = progress
                imageView.setPadding(padding)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        padding = paddingSeekBar.progress
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val frameColourValues = intArrayOf(red, green, blue, alpha, padding)
        val uiVisibilityValues = booleanArrayOf(settingFrame, addFrameButton.isVisible, saveFrameButton.isVisible,
            cancelFrameButton.isVisible, redSeekBar.isVisible,
            greenSeekBar.isVisible, blueSeekBar.isVisible, alphaSeekBar.isVisible, paddingSeekBar.isVisible)

        outState.run {
            putIntArray(STATE_FRAME_VALUES, frameColourValues)
            putBooleanArray(STATE_VISIBILITY_VALUES, uiVisibilityValues)
        }

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        var restoredFrameColourValues: IntArray
        var restoredVisibilityValues: BooleanArray

        savedInstanceState.run {
            restoredFrameColourValues = this.getIntArray(STATE_FRAME_VALUES)!!
            restoredVisibilityValues = this.getBooleanArray(STATE_VISIBILITY_VALUES)!!
        }

        red = restoredFrameColourValues[0]
        green = restoredFrameColourValues[1]
        blue = restoredFrameColourValues[2]
        alpha = restoredFrameColourValues[3]
        padding = restoredFrameColourValues[4]

        redSeekBar.progress = red
        greenSeekBar.progress = green
        blueSeekBar.progress = blue
        alphaSeekBar.progress = alpha
        paddingSeekBar.progress = padding

        settingFrame = restoredVisibilityValues[0]
        addFrameButton.isVisible = restoredVisibilityValues[1]
        saveFrameButton.isVisible = restoredVisibilityValues[2]
        cancelFrameButton.isVisible = restoredVisibilityValues[3]
        redSeekBar.isVisible = restoredVisibilityValues[4]
        greenSeekBar.isVisible = restoredVisibilityValues[5]
        blueSeekBar.isVisible = restoredVisibilityValues[6]
        alphaSeekBar.isVisible = restoredVisibilityValues[7]
        paddingSeekBar.isVisible = restoredVisibilityValues[8]
    }

    private fun cancelFrameChanges() {
        redSeekBar.progress = 0
        greenSeekBar.progress = 0
        blueSeekBar.progress = 0
        alphaSeekBar.progress = 0
        paddingSeekBar.progress = 0
        alpha = 0
        red = 0
        green = 0
        padding = 0
        imageView.setBackgroundColor(Color.argb(alpha, red, green, blue))
        imageView.setPadding(0)
    }

    private fun changeUIState(state: Boolean) {
        saveFrameButton.isVisible = state
        cancelFrameButton.isVisible = state
        redSeekBar.isVisible = state
        greenSeekBar.isVisible = state
        blueSeekBar.isVisible = state
        alphaSeekBar.isVisible = state
        paddingSeekBar.isVisible = state
    }

    private fun saveImage() {
        val finalBitmap: Bitmap = Bitmap.createBitmap(imageView.width, imageView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(finalBitmap)
        val background: Drawable = imageView.background
        background.draw(canvas)
        imageView.draw(canvas)

        val pathname = getString(R.string.pathname)
        val folder = File(pathname)

        if(!folder.exists()) folder.mkdirs()

        val filename = "edited${System.currentTimeMillis()}.png"

        try {
            val out = FileOutputStream(File("$pathname/$filename"))
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            Toast.makeText(this, "Frame added successfully!", Toast.LENGTH_SHORT).show()
        } catch(e: IOException) {
            Log.e("am2021", e.message.toString())
        }

        finish()
    }
}
