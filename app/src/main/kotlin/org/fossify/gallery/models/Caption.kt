package org.fossify.gallery.models

import androidx.room.*
import java.io.Serializable

@Entity(tableName = "captions", indices = [(Index(value = ["full_path"], unique = true))])
data class Caption(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "filename") var name: String,
    @ColumnInfo(name = "full_path") var path: String,
    @ColumnInfo(name = "type") var type: String,
    @ColumnInfo(name = "content") var content: String
) : Serializable {

    constructor() : this(null, "", "", "", "")

    // 手动处理兼容性问题
    companion object {
        private const val serialVersionUID = -6553149366979911L
    }

    fun getSummary(maxLength: Int = 50): String {
        return if (content.length <= maxLength) {
            content
        } else {
            "${content.take(maxLength)}..."
        }
    }
}
