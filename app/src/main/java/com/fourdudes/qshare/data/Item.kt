package com.fourdudes.qshare.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Item(@PrimaryKey val id: UUID = UUID.randomUUID(),
                var name: String = "",
                var description: String = "",
                var date: Date = Date(),
                var link: String = "",
                var isSent: Boolean = true)