package com.example.atmatsumtodoapp

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File


class MainActivity : AppCompatActivity() {

    var button: Button? = null
    var inputTitle: EditText? = null
    var inputDescription: EditText? = null
    var listView: ListView? = null
    var items: List<Map<String, String>> = ArrayList()
    val filename = "myfile"

    fun onClickAdd(v: View?) {
        val title = inputTitle!!.text.toString()
        val description = inputDescription!!.text.toString()
        val item = mapOf("title" to title, "description" to description);
        items = items + item
        render()

        // save
        openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(Gson().toJson(items).toByteArray())
        }

        // set clear
        inputTitle!!.editableText.clear()
        inputDescription!!.editableText.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.addButton)
        inputTitle = findViewById(R.id.inputTitle)
        inputDescription = findViewById(R.id.inputDescription)
        listView = findViewById(R.id.listView)

        // data 読み出し
        val file = File(filesDir, filename)
        val itemType = object : TypeToken<List<Map<String, String>>>() {}.type
        if (file.isFile) {
            val savedString = file.readText()
            Log.d("savedString", savedString)
            items = Gson().fromJson(savedString, itemType)
            render()
        }

        listView!!.setOnItemClickListener { _, _, position, _ ->
            AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                .setTitle("タスク削除")
                .setMessage("指定したタスクを削除しますか？")
                .setPositiveButton("OK") { _, _ ->
                    // Yesが押された時の挙動
                    items = items.filter { element -> element != items[position] }
                    render()
                }.setNegativeButton("No") { _, _ ->
                    // Noが押された時は何もしない
                }.show()
        }
    }

    private fun render() {
        val adapter = SimpleAdapter(
            this,
            items,
            android.R.layout.simple_list_item_2,
            arrayOf("title", "description"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )
        listView!!.adapter = adapter
    }
}
