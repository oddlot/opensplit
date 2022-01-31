package io.oddlot.opensplit.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "member", indices = [Index(value = ["name"], unique = true)])
data class Member(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    var name: String
)