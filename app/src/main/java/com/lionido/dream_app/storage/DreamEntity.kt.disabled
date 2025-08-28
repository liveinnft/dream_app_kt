package com.lionido.dream_app.storage

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionido.dream_app.model.Dream
import com.lionido.dream_app.model.DreamMood
import com.lionido.dream_app.model.DreamSymbol
import java.util.*

/**
 * Room Entity для хранения снов в локальной базе данных
 */
@Entity(tableName = "dreams")
@TypeConverters(DreamTypeConverters::class)
data class DreamEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val audioPath: String?,
    val dateCreated: Long,
    val emotions: List<String>,
    val symbols: List<DreamSymbol>,
    val interpretation: String,
    val tags: List<String>,
    val mood: DreamMood,
    val lucidDream: Boolean
) {
    fun toDream(): Dream {
        return Dream(
            id = id,
            title = title,
            content = content,
            audioPath = audioPath,
            dateCreated = Date(dateCreated),
            emotions = emotions,
            symbols = symbols,
            interpretation = interpretation,
            tags = tags,
            mood = mood,
            lucidDream = lucidDream
        )
    }

    companion object {
        fun fromDream(dream: Dream): DreamEntity {
            return DreamEntity(
                id = dream.id,
                title = dream.title,
                content = dream.content,
                audioPath = dream.audioPath,
                dateCreated = dream.dateCreated.time,
                emotions = dream.emotions,
                symbols = dream.symbols,
                interpretation = dream.interpretation,
                tags = dream.tags,
                mood = dream.mood,
                lucidDream = dream.lucidDream
            )
        }
    }
}

/**
 * Type converters для Room database
 */
class DreamTypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromDreamSymbolList(value: List<DreamSymbol>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toDreamSymbolList(value: String): List<DreamSymbol> {
        val listType = object : TypeToken<List<DreamSymbol>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromDreamMood(value: DreamMood): String {
        return value.name
    }

    @TypeConverter
    fun toDreamMood(value: String): DreamMood {
        return DreamMood.valueOf(value)
    }
}