package io.oddlot.opensplit

import android.app.Application
import android.content.Context
import androidx.room.Room

class Application : Application() {

    companion object {
        private var db: Database? = null

        fun getDatabase(context: Context): Database {
            return db ?: Room.databaseBuilder(context, Database::class.java, "Database").build().also {
                db = it
            }
        }
    }
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) {
        multiplier *= 10
    }
    return kotlin.math.round(this * multiplier) / multiplier
}