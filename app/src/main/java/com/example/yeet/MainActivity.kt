package com.example.yeet


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    private val RecipeApiCall by lazy {
        RecipeApiService.create()
    }

    private val ingredientList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_search.setOnClickListener {
            if (ingredientList.isNotEmpty()) {
                beginFilter(ingredientList.joinToString(separator = ","))
            }
        }

        btn_add.setOnClickListener {
            if(edit_search.text.toString().isNotEmpty()) {
                ingredientList.add(edit_search.text.toString())
                txt_ingredient_list.text = ingredientList.toString()
                edit_search.text.clear()
            }
        }
        btn_remove.setOnClickListener {
            ingredientList.removeAt(ingredientList.size-1)
            txt_ingredient_list.text = ingredientList.toString()
        }
    }


    private fun beginFilter(searchString: String) {
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

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}