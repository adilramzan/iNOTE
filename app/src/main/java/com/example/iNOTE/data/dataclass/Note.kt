package com.example.iNOTE.data.dataclass

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    val id: String = "",
    var title: String = "",
    var content: String = "",
    var imageUrl: String = "",
    var timeStamp: Long = System.currentTimeMillis(),
    var isPinned: Boolean = false,
    var isArchived: Boolean = false,
    val tags: List<String> = listOf()
) : Parcelable {

    constructor() : this(
        id = "",
        title = "",
        content = "",
        imageUrl = "",
        timeStamp = 0L,
        isPinned = false,
        isArchived = false,
        tags = listOf()
    )
}
