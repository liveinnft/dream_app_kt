package com.lionido.dream_app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lionido.dream_app.adapter.DreamsListAdapter
import com.lionido.dream_app.model.Dream
import com.lionido.dream_app.storage.DreamStorage

class DreamsListActivity : AppCompatActivity() {

    private lateinit var dreamsRecycler: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var emptyState: LinearLayout
    private lateinit var dreamsCount: TextView

    private lateinit var dreamsAdapter: DreamsListAdapter
    private lateinit var dreamStorage: DreamStorage
    private var allDreams: List<Dream> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_dreams)

        dreamStorage = DreamStorage(this)

        initViews()
        setupRecyclerView()
        setupSearch()
        setupClickListeners()
        loadDreams()
    }

    override fun onResume() {
        super.onResume()
        loadDreams() // Перезагружаем список при возврате на экран
    }

    private fun initViews() {
        dreamsRecycler = findViewById(R.id.dreams_recycler)
        searchInput = findViewById(R.id.search_input)
        emptyState = findViewById(R.id.empty_state)
        dreamsCount = findViewById(R.id.dreams_count)
    }

    private fun setupRecyclerView() {
        dreamsAdapter = DreamsListAdapter { dream ->
            openDreamAnalysis(dream)
        }
        dreamsRecycler.apply {
            adapter = dreamsAdapter
            layoutManager = LinearLayoutManager(this@DreamsListActivity)
        }
    }

    private fun setupSearch() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterDreams(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupClickListeners() {
        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }
    }

    private fun loadDreams() {
        allDreams = dreamStorage.getAllDreams()
        updateDreamsCount()
        filterDreams(searchInput.text.toString())
    }

    private fun updateDreamsCount() {
        dreamsCount.text = allDreams.size.toString()
    }

    private fun filterDreams(query: String) {
        val filteredDreams = if (query.trim().isEmpty()) {
            allDreams
        } else {
            dreamStorage.searchDreams(query)
        }

        dreamsAdapter.updateDreams(filteredDreams)

        // Показываем/скрываем пустое состояние
        if (filteredDreams.isEmpty()) {
            dreamsRecycler.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        } else {
            dreamsRecycler.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
        }
    }

    private fun openDreamAnalysis(dream: Dream) {
        val intent = Intent(this, DreamAnalysisActivity::class.java).apply {
            putExtra("dreamId", dream.id)
        }
        startActivity(intent)
    }
}