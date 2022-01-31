package io.oddlot.opensplit.models

import androidx.room.*

@Entity(tableName = "group", indices = [Index(value = ["name"], unique = true)])
data class Group(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    var name: String
)

@Dao
interface GroupDao {
    @Query("SELECT * FROM `group`")
    fun getAll(): List<Group>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(group: Group): Long
}

//data class GroupWithMembers(
//    @Embedded val group: Group,
//    @Relation(
//        parentColumn = "groupId",
//        entityColumn = "memberId",
//        associateBy = Junction(Membership::class)
//    )
//    val members: List<Member>
//)