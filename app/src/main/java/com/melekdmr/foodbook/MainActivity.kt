package com.melekdmr.foodbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.melekdmr.foodbook.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var foodList:ArrayList<Food>
    private lateinit var foodAdapter : FoodAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        foodList=ArrayList<Food>()

        foodAdapter = FoodAdapter(foodList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = foodAdapter

        try{  val database=openOrCreateDatabase("Foods", MODE_PRIVATE,null)
            val cursor=database.rawQuery("SELECT*FROM foods",null)
            val foodNameIx=cursor.getColumnIndex("foodname")
            val idIx=cursor.getColumnIndex("id")
             while(cursor.moveToNext()){
                 val name=cursor.getString(foodNameIx)
              val id=cursor.getInt(idIx)
                 val food=Food(name,id)
                 foodList.add(food)
             }
            cursor.close()

        } catch(e:Exception){
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.food_menu,menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.add_food_item){
            val intent= Intent(this,FoodActivity::class.java)

            intent.putExtra("info","new")
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
}