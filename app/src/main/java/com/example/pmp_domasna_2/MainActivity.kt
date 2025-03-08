package com.example.pmp_domasna_2

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var searchQuery: EditText
    private lateinit var saveButton: Button
    private lateinit var clearTagsButton: Button
    private lateinit var makedonski: EditText
    private lateinit var angliski: EditText
    private lateinit var taggedList: ListView
    private lateinit var adapter: ArrayAdapter<String>

    private val fileName = "recnik.txt"
    private val dictionary = mutableMapOf<String, String>()
    private val searchResults = mutableListOf<String>() // Only store search results

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchQuery = findViewById(R.id.searchQuery)
        saveButton = findViewById(R.id.saveButton)
        clearTagsButton = findViewById(R.id.clearTagsButton)
        makedonski = findViewById(R.id.makedonski)
        angliski = findViewById(R.id.angliski)
        taggedList = findViewById(R.id.taggedList)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, searchResults)
        taggedList.adapter = adapter

        loadDictionary()

        saveButton.setOnClickListener { addWord() }
        clearTagsButton.setOnClickListener { clearSearchResults() }
        searchQuery.setOnEditorActionListener { _, _, _ ->
            searchWord()
            true
        }
    }

    private fun loadDictionary() {
        val file = File(filesDir, fileName)
        if (!file.exists()) {
            file.createNewFile()
        }

        dictionary.clear()

        file.forEachLine {
            val parts = it.split("=")
            if (parts.size == 2) {
                dictionary[parts[0].trim().lowercase()] = parts[1].trim().lowercase()
            }
        }
    }

    private fun addWord() {
        val mkWord = makedonski.text.toString().trim()
        val enWord = angliski.text.toString().trim()
        if (mkWord.isNotEmpty() && enWord.isNotEmpty()) {
            dictionary[mkWord.lowercase()] = enWord.lowercase()
            val file = File(filesDir, fileName)
            file.appendText("$mkWord=$enWord\n")

            makedonski.text.clear()
            angliski.text.clear()
            Toast.makeText(this, "Зборот е додаден!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Пополнете ги двете полиња!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchWord() {
        val query = searchQuery.text.toString().trim().lowercase()
        searchResults.clear()

        if (query.isNotEmpty()) {
            if (dictionary.containsKey(query)) {
                searchResults.add("$query → ${dictionary[query]}")
            } else if (dictionary.containsValue(query)) {
                val result = dictionary.entries.find { it.value == query }?.key
                searchResults.add("$result → $query")
            } else {
                searchResults.add("Зборот не е пронајден.")
            }
        }

        adapter.notifyDataSetChanged()
    }

    private fun clearSearchResults() {
        searchResults.clear()
        adapter.notifyDataSetChanged()
    }
}
