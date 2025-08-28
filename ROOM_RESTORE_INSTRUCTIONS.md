# Инструкция по восстановлению Room Database

## Проблема
Room Database был временно отключен из-за проблем совместимости KAPT с новыми версиями JDK.

## Текущее состояние
- ✅ Приложение использует SharedPreferences для хранения
- ✅ Все функции работают корректно
- ✅ Room файлы сохранены с расширением `.disabled`

## Как вернуть Room (когда проблема будет решена)

### 1. Восстановить Room файлы:
```bash
cd app/src/main/java/com/lionido/dream_app/storage/
mv DreamDao.kt.disabled DreamDao.kt
mv DreamDatabase.kt.disabled DreamDatabase.kt
mv DreamEntity.kt.disabled DreamEntity.kt
```

### 2. Раскомментировать зависимости в `app/build.gradle.kts`:
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"  // Раскомментировать
}

// Раскомментировать конфигурацию KSP:
android.sourceSets.configureEach {
    kotlin.srcDir("$layout.buildDirectory/generated/ksp/$name/kotlin/")
}

dependencies {
    // Раскомментировать Room зависимости:
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
}
```

### 3. Обновить `DreamStorage.kt`:
- Заменить SharedPreferences реализацию на Room
- Использовать `DreamDatabase.getDatabase(context)`
- Применить async операции с Room

### 4. Обновить `DreamStorageFactory.kt`:
```kotlin
fun createStorage(context: Context): IDreamStorage {
    return try {
        DreamStorageAdapter(DreamStorage(context))  // Room версия
    } catch (e: Exception) {
        DreamStorageCompatAdapter(DreamStorageCompat(context))  // Fallback
    }
}
```

## Альтернативы KAPT

Если KAPT продолжает вызывать проблемы:

1. **KSP (Kotlin Symbol Processing)** - уже настроен в проекте
2. **Обновление JDK** - использовать JDK 11 вместо новых версий
3. **Добавление JVM аргументов**:
   ```kotlin
   android {
       compileOptions {
           sourceCompatibility = JavaVersion.VERSION_11
           targetCompatibility = JavaVersion.VERSION_11
       }
   }
   ```

## Тестирование

После восстановления Room:
1. Запустить `./gradlew build`
2. Проверить генерацию Room кода
3. Убедиться в корректной работе базы данных
4. Протестировать миграцию данных из SharedPreferences

## Заметки

- Текущая версия SharedPreferences полностью функциональна
- Данные пользователя не будут потеряны при переходе
- Room обеспечит лучшую производительность и типобезопасность