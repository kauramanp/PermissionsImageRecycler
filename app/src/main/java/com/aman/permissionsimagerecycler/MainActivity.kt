package com.aman.permissionsimagerecycler

import android.content.Intent
import android.content.pm.PackageManager
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

    private val gridLayoutManager: GridLayoutManager by lazy{
        GridLayoutManager(this, 4)
    }
    var images = arrayListOf<String>("1","2")

    private val imagesAdapter = ImagesAdapter(images ,::onDeleteClick, ::onAddClick)

    private  val TAG = "MainActivity"

    val readStoragePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_IMAGES
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val writeStoragePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_IMAGES
    } else {
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (
            permissions[readStoragePermission] == true &&
            permissions[writeStoragePermission] == true
        ) {
            Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            openAppSettings()
        }
    }

    private fun onDeleteClick(position: Int){
        images.removeAt(position)
        imagesAdapter.notifyDataSetChanged()
    }
    private fun onAddClick(){
        images.add(" ")
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
        if(hasPermissions() == false){
            requestPermissionsWithRationale()
        }
    }

    private fun initViews(){
        binding.rvImages.layoutManager = gridLayoutManager
        binding.rvImages.adapter = imagesAdapter
    }

    private fun hasPermissions(): Boolean {
        val readStoragePermission = ContextCompat.checkSelfPermission(
            this, readStoragePermission
        )
        val writeStoragePermission = ContextCompat.checkSelfPermission(
            this, writeStoragePermission
        )
        return  readStoragePermission == PackageManager.PERMISSION_GRANTED &&
                writeStoragePermission == PackageManager.PERMISSION_GRANTED
    }

    // Request permissions with rationale handling
    private fun requestPermissionsWithRationale() {
        val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this, readStoragePermission
        ) || ActivityCompat.shouldShowRequestPermissionRationale(
            this, writeStoragePermission
        )
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
            arrayOf(
                writeStoragePermission,
                readStoragePermission
            )
        )
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = android.net.Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        intent.data = uri
        startActivity(intent)
    }
}