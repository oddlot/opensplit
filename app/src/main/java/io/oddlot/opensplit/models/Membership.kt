package io.oddlot.opensplit.models

import androidx.room.Entity

@Entity(tableName = "membership", primaryKeys = ["groupId", "memberId"])
data class Membership(
    val groupId: Long,
    val memberId: Long
)