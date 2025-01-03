package com.aman.permissionsimagerecycler

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.aman.permissionsimagerecycler.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val gridLayoutManager: GridLayoutManager by lazy {
        GridLayoutManager(this, 4)
    }
    var images = arrayListOf<Uri>()

    private val imagesAdapter = ImagesAdapter(images, ::onDeleteClick, ::onAddClick)

    private val TAG = "MainActivity"

    private val storagePermissions: Array<String> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
            pickImage.launch("image/*")
        } else {
            Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            openAppSettings()
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            images.add(it)
            imagesAdapter.notifyDataSetChanged()
        }
    }

    private fun onDeleteClick(position: Int) {
        images.removeAt(position)
        imagesAdapter.notifyDataSetChanged()
    }

    private fun onAddClick() {
        if (!hasPermissions()) {
            requestPermissionsWithRationale()
        } else
            imagesAdapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()

        if (!hasPermissions()) {
            requestPermissionsWithRationale()
        }
    }

    private fun initViews() {
        binding.rvImages.layoutManager = gridLayoutManager
        binding.rvImages.adapter = imagesAdapter
    }

    private fun hasPermissions(): Boolean {
        return storagePermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissionsWithRationale() {
        val shouldShowRationale = storagePermissions.any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
        }

        if (shouldShowRationale) {
            Toast.makeText(
                this,
                "Permissions are required for the app to function properly",
                Toast.LENGTH_LONG
            ).show()
            openAppSettings()
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        requestPermissionLauncher.launch(
            storagePermissions
        )
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = android.net.Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        intent.data = uri
        startActivity(intent)
    }
}