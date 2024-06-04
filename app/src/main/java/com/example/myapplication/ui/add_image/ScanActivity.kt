package com.example.myapplication.ui.add_image

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.http.HttpException
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.ViewModelFactory
import com.example.myapplication.databinding.ActivityScanBinding
import com.example.myapplication.ui.main.MainActivity
import com.example.myapplication.retrofit.ApiConfig
import com.example.myapplication.ui.welcome.WelcomeActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody

class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding
    private val viewModel by viewModels<ScanViewModel> {
        ViewModelFactory.getInstance(this) //belon ada
    }
    private var token = "token"

    private var currentImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //supportActionBar?.title = "Add Your Story"

        if (!PermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                token = user.token
                binding.btGallery.setOnClickListener { startGallery() }
                binding.btUpload.setOnClickListener { uploadImage() }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun backToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Image Selector", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewparu.setImageURI(it)
        }
    }

    private fun PermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED



    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.etDescription.text.toString()
            showLoading(true)

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            lifecycleScope.launch {
                try {
                    val apiService = ApiConfig.getApiService()
                    val successResponse = apiService.uploadImage("Bearer $token", multipartBody, requestBody)
                    Log.d(ContentValues.TAG, "uploadImage token: $token")
                    Log.d(ContentValues.TAG, "uploadImage: berhasil")
                    showToast("Upload Successful")

                    successResponse.enqueue(object : Callback<FileUploadResponse> {
                        override fun onResponse(
                            call: Call<FileUploadResponse>,
                            response: Response<FileUploadResponse>
                        ) {

                            if (response.isSuccessful) {
                                Log.e(ContentValues.TAG, "response Success: ${response.message()}")
                                backToMainActivity()
                            } else {
                                Log.e(ContentValues.TAG, "add response gagal: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {

                            Log.e(ContentValues.TAG, "upload gagal: ${t.message.toString()}")
                        }
                    })
                    showLoading(false)
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
                    showToast(errorResponse.message.toString())
                    showLoading(false)
                }
            }
        } ?: showToast(getString(R.string.emptyImage))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}