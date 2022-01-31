package io.oddlot.opensplit

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.oddlot.opensplit.models.*

@Database(entities = [Group::class, Expense::class, Member::class, Membership::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun groupDao(): GroupDao

    companion object {

    }
}