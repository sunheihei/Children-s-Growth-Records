package com.babycare.childgrowthtracking.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Entity(
    tableName = "children",
)
@Serializable
data class Child(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    var name: String,
    var gender: Int,
    var avatar: String,
    var age: Long,
    var note: String
) {

    companion object {
        val TempChild = Child(
            0, "Annie", 0, "avatar", 1613034198, "note"
        )
    }

}
