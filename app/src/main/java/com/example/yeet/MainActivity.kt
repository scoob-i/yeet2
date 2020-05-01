package com.example.yeet


import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    //Api Stuff

    private var disposable: Disposable? = null

    private val RecipeApiCall by lazy {
        RecipeApiService.create()
    }

    private val ingredientList = mutableListOf<String>()

    var ringredients: Array<IngModel.Meals>? = null

    //Autocomplete stuff

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_search.setOnClickListener {
            if (ingredientList.isNotEmpty()) {
                beginFilter(ingredientList.joinToString(separator = ","))
                // [chicken, dog,cat]  = "chicken,dog,cat"
            }
        }

        btn_add.setOnClickListener {
            if (edit_search.text.toString().isNotEmpty()) {
                ingredientList.add(edit_search.text.toString())
                txt_ingredient_list.text = ingredientList.toString()
                edit_search.text.clear()
            }
        }
        btn_remove.setOnClickListener {
            if(ingredientList.isNotEmpty())
                ingredientList.removeAt(ingredientList.size - 1)
                txt_ingredient_list.text = ingredientList.toString()
        }

        // Initialize a new array with elements

        getIngredients()
//        var IngArray = arrayOfNulls<String>(ringredients.size)
//        ringredients.toCollection(IngArray)


        // Initalize a new array adapter object

        val adapter = ArrayAdapter<String>(
            this, // context
            android.R.layout.simple_dropdown_item_1line, //layout
            ringredients //array
        )


        //set the adapter

        edit_search.setAdapter(adapter)

        //auto complete threshold
        //the minimum number of characters to type to show the drop down

        edit_search.threshold = 1


        // Set an item click listener for auto complete text view

        edit_search.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position).toString()
                // Display the clicked item using toast
                Toast.makeText(applicationContext, "selected: $selectedItem", Toast.LENGTH_SHORT)
                    .show()
            }


        // Set a dismiss listener for auto complete text view
        edit_search.setOnDismissListener {
            Toast.makeText(applicationContext, "suggestion closed.", Toast.LENGTH_SHORT).show()
        }


        //set a click listener for root layout

        edit_search.setOnClickListener {

            val text = edit_search.text

            Toast.makeText(applicationContext, "Inputted : $text", Toast.LENGTH_SHORT).show()
        }


        // Set a focus change listener for auto complete text view


        edit_search.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if (b) {
                // Display the suggestion dropdown on focus
                edit_search.showDropDown()
            }
        }
    }


    private fun beginFilter(searchString: String) {
        //instance of interface being called
        disposable = RecipeApiCall.recipeFilter(searchString)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> if(result.meals !== null) {
                    txt_search_result.text = "${result.meals.size} recipes found"}
                        else {txt_search_result.text = "No meals found"}},
                { error -> Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
            )
    }

    private fun getIngredients(){
        disposable = RecipeApiCall.ingredientGet("list")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    ringredients = result.ingredients

                },
                { error -> Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
            )
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}