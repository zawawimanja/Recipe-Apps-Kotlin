package com.zawawi.recipe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zawawi.recipe.Adapter.AdapterRecipe
import com.zawawi.recipe.DBHelper.MyDbHelper
import com.zawawi.recipe.UI.ManageRecipeActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Pack200.Packer.LATEST

class MainActivity : AppCompatActivity() {

    lateinit var dbHelper: MyDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        dbHelper = MyDbHelper(this)

        loadRecipe()

        mainAddButton.setOnClickListener {
            val intent = Intent(this, ManageRecipeActivity::class.java)
            startActivity(intent)
        }

    }

    private fun loadRecipe() {

        val adapterRecipe = AdapterRecipe(
            this,
            dbHelper.getAllRecipe()
        )

        mainRecyclerRecipe.adapter = adapterRecipe
    }


    public override fun onResume() {
        super.onResume()
        loadRecipe()
    }

}
