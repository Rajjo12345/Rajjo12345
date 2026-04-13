package com.example.multifunctionapp.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.multifunctionapp.databinding.FragmentCameraBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CameraFragment : Fragment() {
    
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    
    private var currentPhotoPath: String? = null
    private var capturedImageUri: Uri? = null
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }
    
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            capturedImageUri?.let { uri ->
                displayCapturedImage(uri)
                saveImageToGallery(uri)
            }
        }
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.buttonTakePhoto.setOnClickListener {
            checkCameraPermission()
        }
        
        binding.buttonOpenGallery.setOnClickListener {
            openGallery()
        }
    }
    
    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(requireContext(), "Camera access is needed to take photos", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    
    private fun launchCamera() {
        val photoFile = createImageFile()
        if (photoFile != null) {
            capturedImageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile
            )
            takePictureLauncher.launch(capturedImageUri)
        }
    }
    
    private fun createImageFile(): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
                currentPhotoPath = absolutePath
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error creating image file", Toast.LENGTH_SHORT).show()
            null
        }
    }
    
    private fun displayCapturedImage(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            binding.imageviewCapturedPhoto.setImageBitmap(bitmap)
            binding.imageviewCapturedPhoto.visibility = View.VISIBLE
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error displaying image", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun saveImageToGallery(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val fileName = "IMG_${System.currentTimeMillis()}.jpg"
            val outputFile = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
            
            val outputStream = FileOutputStream(outputFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            
            Toast.makeText(requireContext(), "Image saved to ${outputFile.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error saving image", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun openGallery() {
        // Placeholder for gallery functionality
        Toast.makeText(requireContext(), "Gallery feature - Coming soon!", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
