package org.fossify.gallery.interfaces

import org.fossify.gallery.models.Caption
import androidx.room.*

@Dao
interface CaptionDao {
    @Query("SELECT filename, full_path, type, content FROM captions WHERE full_path = :path COLLATE NOCASE")
    fun getCaption(path: String): List<Caption>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(caption: Caption)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(captions: List<Caption>)

    @Delete
    fun deleteCaption(vararg caption: Caption)
}
