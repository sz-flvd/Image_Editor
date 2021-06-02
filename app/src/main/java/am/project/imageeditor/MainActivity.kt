package am.project.imageeditor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.goToGalleryButton)
        button.setOnClickListener { goToGallery() }
    }

    private fun goToGallery() {
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
    }
}
