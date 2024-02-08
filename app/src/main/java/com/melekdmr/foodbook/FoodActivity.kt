package com.melekdmr.foodbook

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.melekdmr.foodbook.databinding.ActivityFoodBinding
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.Exception

class FoodActivity : AppCompatActivity() {
    private lateinit var binding:ActivityFoodBinding
    private lateinit var activityResultLauncher:ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher:ActivityResultLauncher<String>
    var selectedBitmap : Bitmap?= null
    private lateinit var database:SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding=ActivityFoodBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        database=this.openOrCreateDatabase("Foods", Context.MODE_PRIVATE,null)
         registerLauncher()
        val intent=intent
        val info=intent.getStringExtra("info")
       if(info.equals("new")){
           binding.nameText.setText("")
           binding.chefName.setText("")
           binding.recipeName.setText("")
           binding.save.visibility=View.VISIBLE
           binding.selectImage.setImageResource(R.drawable.indir)

       }else{
           binding.save.visibility=View.INVISIBLE
           val selectedId=intent.getIntExtra("id",1)
           //dizi içinde istendiği için id yi dizi içinde verdik
           val cursor =database.rawQuery("SELECT * FROM arts WHERE id=?",arrayOf(selectedId.toString()))

           val foodNameIx=cursor.getColumnIndex("foodname")
           val chefNameIx =cursor.getColumnIndex("chefname")
           val recipeNameIx=cursor.getColumnIndex("recipe")
           val imageIx=cursor.getColumnIndex("image")

           while(cursor.moveToNext()){
               binding.nameText.setText(cursor.getString(foodNameIx))
               binding.chefName.setText(cursor.getString(chefNameIx))
               binding.recipeName.setText(cursor.getString(recipeNameIx))


               val byteArray=cursor.getBlob(imageIx)
               val bitmap=BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
               binding.selectImage.setImageBitmap(bitmap)

           }

         cursor.close()
       }


    }

    fun save(view:View){

        val foodName=binding.nameText.text.toString()
        val chefName=binding.chefName.text.toString()
        val recipeText=binding.recipeName.text.toString()

        if(selectedBitmap!=null) {
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!, 300)
            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val byteArray = outputStream.toByteArray()

            //VERİTABANI
            try {

                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS foods(id INTEGER PRIMARY KEY ,foodname VARCHAR,chefname VARCHAR,recipe VARCHAR," +
                            "image BLOB)"
                )
                val sqlString = "INSERT INTO foods(foodname,chefname,recipe,image) VALUES(?,?,?,?) "
                val statement = database.compileStatement(sqlString)
                statement.bindString(1, foodName)
                statement.bindString(2, chefName)
                statement.bindString(3, recipeText)
                statement.bindBlob(4, byteArray)

                statement.execute()

            } catch (e: Exception) {
                e.printStackTrace()
            }
            val intent=Intent(this@FoodActivity,MainActivity::class.java)

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    fun makeSmallerBitmap (image: Bitmap, maximumSize:Int) : Bitmap {
        // resmin genişliğini ve yüksekliğini aldık
        var width=image.width
        var height=image.height
        // bir oran oluşturuyoruz
        var bitmapRadio:Double =width.toDouble()/height.toDouble()
        if(bitmapRadio>1) {
            // resim yataydır(landscape)
            width=maximumSize
            val scaleHeight=width/bitmapRadio
            height=scaleHeight.toInt()

        }else{
            // resim dikeydir(portrait)
            height=maximumSize
            val scalewidth=height*bitmapRadio
            width=scalewidth.toInt()
        }

        // resim boyutunu büyüt ya da küçült
        return Bitmap.createScaledBitmap(image,100,100,true)

    }
    fun selectImage(view: View){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
  if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_MEDIA_IMAGES)!=PackageManager.PERMISSION_GRANTED)
  {
      if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){

          Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {

              permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
          }).show()
      }else {
          permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
      }
    }else{
      val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
      activityResultLauncher.launch(intentToGallery)

  }    }else{
      if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
      {
          if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
              Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                  permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
              }).show()
          }else{
              permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
          }
      }else{
          val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
          activityResultLauncher.launch(intentToGallery)
      }
  }
    }

    private fun registerLauncher(){

        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    val imageData = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(
                                this@FoodActivity.contentResolver,
                                imageData!!
                            )
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.selectImage.setImageBitmap(selectedBitmap)
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(
                                this@FoodActivity.contentResolver,
                                imageData
                            )
                            binding.selectImage.setImageBitmap(selectedBitmap)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }


        }
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if(result){
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }else{
                Toast.makeText(this@FoodActivity,"Permisson needed!",Toast.LENGTH_LONG).show()
            }
        }

    }
}