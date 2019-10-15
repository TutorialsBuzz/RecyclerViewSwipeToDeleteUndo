package com.tutorialsbuzz.recyclerviewswipetodeleteundo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tutorialsbuzz.recyclerview.CustomAdapter
import com.tutorialsbuzz.recyclerview.Model
import com.tutorialsbuzz.recylerviewswipetodelete.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutManager: RecyclerView.LayoutManager? = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        val modelList = readFromAsset();
        val adapter = CustomAdapter(modelList, this)
        recyclerView.adapter = adapter;

        recyclerView.addItemDecoration(SimpleDividerItemDecoration(this))

        val swipeToDeleteCallback =
            object : SwipeToDeleteCallback(this, 0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    adapter.pendingRemoval(viewHolder.adapterPosition)
                }

                override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                    if (adapter.isPendingRemoval(viewHolder.adapterPosition)) {
                        return ItemTouchHelper.ACTION_STATE_IDLE
                    }
                    return super.getSwipeDirs(recyclerView, viewHolder)
                }
            }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun readFromAsset(): MutableList<Model> {
        val modeList = mutableListOf<Model>()
        val bufferReader = application.assets.open("android_version.json").bufferedReader()
        val json_string = bufferReader.use {
            it.readText()
        }
        val jsonArray = JSONArray(json_string);

        for (i in 0..jsonArray.length() - 1) {
            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
            val model = Model(jsonObject.getString("name"), jsonObject.getString("version"))
            modeList.add(model)
        }
        return modeList
    }
}
