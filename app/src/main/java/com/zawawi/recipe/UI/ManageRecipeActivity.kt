package com.zawawi.recipe.UI

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.zawawi.recipe.DBHelper.MyDbHelper
import com.zawawi.recipe.MainActivity
import com.zawawi.recipe.R
import kotlinx.android.synthetic.main.activity_manage_recipe.*

class ManageRecipeActivity : AppCompatActivity() {

    private val CAMERA_REQUEST_CODE = 100
    private val STORAGE_REQUEST_CODE = 101

    private val IMAGE_PICK_CAMERA_CODE = 102
    private val IMAGE_PICK_GALLERY_CODE = 103

    private lateinit var cameraPermissions:Array<String>
    private lateinit var storagePermissions:Array<String>

    private var imageUri: Uri? = null

    private var id:String? = ""
    private var manageType:String? = ""
    private var manageName:String? = ""
    private var manageServes:String? = ""
    private var manageIngredients:String? = ""
    private var manageSteps:String? = ""
    private var manageDateTime:String? = ""

    private var TypeAction = false

    private var actionBar: ActionBar? = null;

    lateinit var dbHelper: MyDbHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_recipe)

        actionBar = supportActionBar
        actionBar!!.title = "Manage Recipe"
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.setDisplayShowHomeEnabled(true)

        val intent = intent
       TypeAction = intent.getBooleanExtra("TypeAction",false)

        if (TypeAction) {
            actionBar!!.title = "Update Recipe"

            id = intent.getStringExtra("ID")
            manageType = intent.getStringExtra("TYPE")
            manageName = intent.getStringExtra("NAME")
            manageServes = intent.getStringExtra("SERVES")
            manageIngredients = intent.getStringExtra("INGREDIENTS")
            manageSteps = intent.getStringExtra("STEPS")
            manageDateTime = intent.getStringExtra("ADDTIME")
            imageUri = Uri.parse(intent.getStringExtra("IMAGE"))

            if (imageUri.toString() == "null") {
                manageRecipeImage.setImageResource(R.drawable.ic_launcher_background)
            }
            else {
                manageRecipeImage.setImageURI(imageUri)
            }

            manageRecipeType.setText(manageType)
            manageRecipeName.setText(manageName)

            manageRecipeIngredients.setText(manageIngredients)
            manageRecipeSteps.setText(manageSteps)
        }


        dbHelper = MyDbHelper(this)

        cameraPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        manageRecipeImage.setOnClickListener {
            imagePickDialog()
        }

        manageRecipeSave.setOnClickListener {
            inputData()
        }
    }

    private fun inputData() {
        manageType = "" + manageRecipeType.text.toString().trim()
        manageName = "" + manageRecipeName.text.toString().trim()

        manageIngredients = "" + manageRecipeIngredients.text.toString().trim()
        manageSteps = "" + manageRecipeSteps.text.toString().trim()

        if(TypeAction) {
            // Update
            dbHelper?.updateRecipe(
                "$id",
                "$imageUri",
                "$manageType",
                "$manageName",
                "$manageIngredients",
                "$manageSteps"

            )
            Toast.makeText(this, "Recipe updated successfully!", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, MainActivity::class.java))
        }
        else {
            // Add New
            val id = dbHelper.insertRecipe(
                ""+imageUri,
                ""+manageType,
                ""+manageName,
                ""+manageIngredients,
                ""+manageSteps

            )
            Toast.makeText(this, "Recipe added successfully with Id $id!", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun imagePickDialog() {

        val i = Intent()
        i.action = Intent.ACTION_OPEN_DOCUMENT
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "image/*"
        startActivityForResult(i, 1)
    }



    private fun pickFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE)
    }

    private fun pickFromCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Image Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image Description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            CAMERA_REQUEST_CODE-> {
                if (grantResults.isNotEmpty()) {
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera()
                    }
                    else {
                        Toast.makeText(this, "Please grant permission for Camera and Storage!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            STORAGE_REQUEST_CODE-> {
                if (grantResults.isNotEmpty()) {
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    if (storageAccepted) {
                        pickFromGallery()
                    }
                    else {
                        Toast.makeText(this, "Please grant permission for Storage!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data

            manageRecipeImage.setImageURI(imageUri)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }






}
