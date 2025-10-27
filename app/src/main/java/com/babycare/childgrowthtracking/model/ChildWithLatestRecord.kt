package com.babycare.childgrowthtracking.model

import androidx.room.Embedded

data class ChildWithLatestRecord(
    @Embedded val child: Child, @Embedded(prefix = "growth_") val growthRecord: GrowthRecord?
) {

    companion object {
        val TempChildWithRecord1 = ChildWithLatestRecord(
            child = Child(0, "Annie", 0, "avatar", 1613034198, "note"), growthRecord = GrowthRecord(
                0, 1, 1613034198L, "50.0", "3", "5", "6.1", "13.0", "20", "45", "note"
            )
        )
        val TempChildWithRecord2 = ChildWithLatestRecord(
            child = Child(1, "Annie", 0, "avatar", 1613034198, "note"), growthRecord = GrowthRecord(
                0, 1, 1613034198L, "50.0", "3", "5", "6.1", "13.0", "20", "45", "note"
            )
        )
        val TempChildWithRecord3 = ChildWithLatestRecord(
            child = Child(2, "Annie", 0, "avatar", 1613034198, "note"), growthRecord = GrowthRecord(
                0, 1, 1613034198L, "50.0", "3", "5", "6.1", "13.0", "20", "45", "note"
            )
        )

        val TempChildWithRecordList =
            listOf(TempChildWithRecord1, TempChildWithRecord2, TempChildWithRecord3)


    }

}