package com.example.notetakingapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.notetakingapp.NoteEdit.Companion.appmode


class MainActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val listView = findViewById<ListView>(R.id.listView)
        val sharedPreferences =
            applicationContext.getSharedPreferences("com.example.notes", MODE_PRIVATE)
        val set = sharedPreferences.getStringSet("notes", null) as HashSet<String?>?
        if (set == null) {
            notes.add("Example note")
        } else {
            notes = ArrayList<String?>(set)
        }

        // Using custom listView Provided by Android Studio
        arrayAdapter =
            ArrayAdapter<String?>(this, android.R.layout.simple_expandable_list_item_1, notes)
        listView.adapter = arrayAdapter
        listView.onItemClickListener =
            OnItemClickListener { _, _, i, _ -> // Going from MainActivity to NotesEditorActivity
                val intent = Intent(applicationContext, NoteEdit::class.java)
                intent.putExtra("noteId", i)
                startActivity(intent)
            }
        listView.onItemLongClickListener =
            OnItemLongClickListener { adapterView, view, i, l ->
                val itemToDelete = i
                // To delete the data from the App
                AlertDialog.Builder(this@MainActivity)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Are you sure?")
                    .setMessage("Do you want to delete this note?")
                    .setPositiveButton(
                        "Yes"
                    ) { dialogInterface, i ->
                        notes.removeAt(itemToDelete)
                        (arrayAdapter as ArrayAdapter<Any?>).notifyDataSetChanged()
                        val sharedPreferences = applicationContext.getSharedPreferences(
                            "com.example.notes",
                            MODE_PRIVATE
                        )
                        val set: Any? =
                            HashSet<Any?>(notes)
                        sharedPreferences.edit().putStringSet("notes", set as MutableSet<String>?).apply()
                    }.setNegativeButton("No", null).show()
                true
            }
    }

    fun onClickMic(v: View){
        val intent = Intent(applicationContext, NoteEdit::class.java)
        startActivity(intent)
    }
    fun switch(v: View){

        if(appmode=="urdu"){
            appmode="english"
            Toast.makeText(this,"Language set to English",Toast.LENGTH_LONG).show()
        }else{
            appmode="urdu"
            Toast.makeText(this,"Language set to Urdu",Toast.LENGTH_LONG).show()
        }

    }
    companion object {
        var notes = ArrayList<String?>()
        var arrayAdapter: ArrayAdapter<*>? = null
    }
}
