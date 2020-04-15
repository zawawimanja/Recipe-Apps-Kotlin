package com.zawawi.recipe.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zawawi.recipe.*
import com.zawawi.recipe.DBHelper.MyDbHelper
import com.zawawi.recipe.Model.ModelRecipe
import com.zawawi.recipe.UI.DetailRecipeActivity
import com.zawawi.recipe.UI.ManageRecipeActivity

class AdapterRecipe() : RecyclerView.Adapter<AdapterRecipe.HolderRecipe>(){

    private var context: Context?=null
    private var recipeList:ArrayList<ModelRecipe>?=null
    lateinit var dbHelper: MyDbHelper

    constructor(context: Context?, recipeList: ArrayList<ModelRecipe>?) : this() {
        this.context = context
        this.recipeList = recipeList

        dbHelper = MyDbHelper(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRecipe {
        return HolderRecipe(
            LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return recipeList!!.size
    }

    override fun onBindViewHolder(holder: HolderRecipe, position: Int) {
        val model = recipeList!!.get(position)
        val id = model.id
        val image = model.image
        val type = model.type
        val name = model.name
        val ingredients = model.ingredients
        val steps = model.steps


        holder.itemType.text = type
        holder.itemName.text = name


        if (image == "null") {
            holder.itemImage.setImageResource(R.drawable.ic_launcher_background)
        }
        else {
            holder.itemImage.setImageURI(Uri.parse(image))
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailRecipeActivity::class.java)
            intent.putExtra("RECIPE_ID", id)
            context!!.startActivity(intent)
        }

        holder.itemEdit.setOnClickListener {
            val intent = Intent(context, ManageRecipeActivity::class.java)
            intent.putExtra("ID", id)
            intent.putExtra("IMAGE", image)
            intent.putExtra("TYPE", type)
            intent.putExtra("NAME", name)
            intent.putExtra("INGREDIENTS", ingredients)
            intent.putExtra("STEPS", steps)
            intent.putExtra("TypeAction", true)
            context!!.startActivity(intent)
        }

        holder.itemDelete.setOnClickListener{
            dbHelper.deleteRecipe(id)
            (context as MainActivity)!!.onResume();
        }
    }


    inner class HolderRecipe(itemView: View): RecyclerView.ViewHolder(itemView) {

        var itemImage:ImageView = itemView.findViewById(R.id.itemImage)
        var itemType:TextView = itemView.findViewById(R.id.itemType)
        var itemName:TextView = itemView.findViewById(R.id.itemName)
        var itemEdit: Button = itemView.findViewById(R.id.itemEdit)
        var itemDelete: Button = itemView.findViewById(R.id.itemDelete)
    }
}