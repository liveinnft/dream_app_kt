package com.lionido.dream_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lionido.dream_app.storage.DreamStorage
import com.lionido.dream_app.model.Dream

class DreamsListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dreams_list) // Нужно создать activity_dreams_list.xml

        // TODO: Реализация списка снов через RecyclerView
    }
}
