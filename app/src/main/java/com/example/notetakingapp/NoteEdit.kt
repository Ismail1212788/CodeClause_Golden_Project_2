package com.example.notetakingapp

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import java.util.*
import kotlin.collections.HashSet


class NoteEdit : AppCompatActivity() {
    var noteId = 0

    companion object {
        const val REQUEST_CODE_STT = 1
        var appmode:String ="urdu"
    }
    private val textToSpeechEngine: TextToSpeech by lazy {
        TextToSpeech(this,
            TextToSpeech.OnInitListener { status ->
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechEngine.language = Locale.ENGLISH
                }
            })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_edit)
        val editText = findViewById<EditText>(R.id.editText)

        // Fetch data that is passed from MainActivity
        val intent = intent

        // Accessing the data using key and value
        noteId = intent.getIntExtra("noteId", -1)
        if (noteId != -1) {
            editText.setText(MainActivity.notes.get(noteId))
        } else {
            MainActivity.notes.add("")
            noteId = MainActivity.notes.size - 1
            MainActivity.arrayAdapter?.notifyDataSetChanged()
        }
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                // add your code here
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                MainActivity.notes.set(noteId, charSequence.toString())
                MainActivity.arrayAdapter?.notifyDataSetChanged()
                // Creating Object of SharedPreferences to store data in the phone
                val sharedPreferences = applicationContext.getSharedPreferences("com.example.notes", MODE_PRIVATE)
                val set: HashSet<String?> = HashSet(MainActivity.notes)
                sharedPreferences.edit().putStringSet("notes", set).apply()
            }

            override fun afterTextChanged(editable: Editable) {
                // add your code here
            }
        })
        getInput()
    }

    fun getInputEnglish(){
        val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        sttIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)

        try {
            startActivityForResult(sttIntent, NoteEdit.REQUEST_CODE_STT)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "Your device does not support STT.", Toast.LENGTH_LONG).show()
        }
    }

    fun getInputUrdu(){
        val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        sttIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ur-PK") // Set language to Urdu (Pakistan)
        sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now") // Prompt message for the user


        try {
            startActivityForResult(sttIntent, NoteEdit.REQUEST_CODE_STT)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "Your device does not support STT.", Toast.LENGTH_LONG).show()
        }
    }

    fun getInput() {

        if(appmode=="urdu"){
            getInputUrdu()
        }else{
            getInputEnglish()
        }

    }
    fun Speek(text:String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeechEngine.speak(text, TextToSpeech.QUEUE_ADD, null, "tts1")
        } else {
            textToSpeechEngine.speak(text, TextToSpeech.QUEUE_ADD, null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            NoteEdit.REQUEST_CODE_STT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    result?.let {
                        val recognizedText = it[0]
                        val i=findViewById<EditText>(R.id.editText)
                        i.text.append(" "+recognizedText)
                        getInput()
                    }
                }
                else if(resultCode != Activity.RESULT_OK || data == null){
                    val handler = Handler()
                    handler.postDelayed({
                       getInput()
                    }, 4000)
                }
            }
        }
    }
}
