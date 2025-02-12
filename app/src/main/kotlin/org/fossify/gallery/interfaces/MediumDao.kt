package org.fossify.gallery.interfaces

import androidx.room.*
import org.fossify.gallery.models.Medium
import org.fossify.gallery.helpers.TYPE_IMAGES

@Dao
interface MediumDao {
    @Query("SELECT filename, full_path, parent_path, last_modified, date_taken, size, type, video_duration, is_favorite, deleted_ts, media_store_id FROM media WHERE deleted_ts = 0 AND parent_path = :path COLLATE NOCASE")
    fun getMediaFromPath(path: String): List<Medium>

    @Query("SELECT filename, full_path, parent_path, last_modified, date_taken, size, type, video_duration, is_favorite, deleted_ts, media_store_id FROM media WHERE deleted_ts = 0 AND is_favorite = 1")
    fun getFavorites(): List<Medium>

    @Query("SELECT COUNT(filename) FROM media WHERE deleted_ts = 0 AND is_favorite = 1")
    fun getFavoritesCount(): Long

    @Query("SELECT filename, full_path, parent_path, last_modified, date_taken, size, type, video_duration, is_favorite, deleted_ts, media_store_id FROM media WHERE deleted_ts != 0")
    fun getDeletedMedia(): List<Medium>

    @Query("SELECT COUNT(filename) FROM media WHERE deleted_ts != 0")
    fun getDeletedMediaCount(): Long

    @Query("SELECT filename, full_path, parent_path, last_modified, date_taken, size, type, video_duration, is_favorite, deleted_ts, media_store_id FROM media WHERE deleted_ts < :timestmap AND deleted_ts != 0")
    fun getOldRecycleBinItems(timestmap: Long): List<Medium>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(medium: Medium)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(media: List<Medium>)

    @Delete
    fun deleteMedia(vararg medium: Medium)

    @Query("DELETE FROM media WHERE full_path = :path COLLATE NOCASE")
    fun deleteMediumPath(path: String)

    @Query("UPDATE OR REPLACE media SET filename = :newFilename, full_path = :newFullPath, parent_path = :newParentPath WHERE full_path = :oldPath COLLATE NOCASE")
    fun updateMedium(oldPath: String, newParentPath: String, newFilename: String, newFullPath: String)

    @Query("UPDATE OR REPLACE media SET full_path = :newPath, deleted_ts = :deletedTS WHERE full_path = :oldPath COLLATE NOCASE")
    fun updateDeleted(newPath: String, deletedTS: Long, oldPath: String)

    @Query("UPDATE media SET date_taken = :dateTaken WHERE full_path = :path COLLATE NOCASE")
    fun updateFavoriteDateTaken(path: String, dateTaken: Long)

    @Query("UPDATE media SET is_favorite = :isFavorite WHERE full_path = :path COLLATE NOCASE")
    fun updateFavorite(path: String, isFavorite: Boolean)

    @Query("UPDATE media SET is_favorite = 0")
    fun clearFavorites()

    @Query("DELETE FROM media WHERE deleted_ts != 0")
    fun clearRecycleBin()

    @Query("""
    SELECT filename, full_path, parent_path, last_modified, date_taken, size, type, video_duration, is_favorite, deleted_ts, media_store_id, caption 
    FROM media 
    WHERE type = :imageType AND deleted_ts = 0 
    ORDER BY date_taken DESC, last_modified DESC
    """)
    fun getAllImages(imageType: Int = TYPE_IMAGES): List<Medium>

    @Query("UPDATE media SET caption = :newCaption WHERE full_path = :path COLLATE NOCASE")
    fun updateCaption(path: String, newCaption: String)

    // 批量更新caption，使用@Transaction优化更新效率
    @Transaction
    fun updateCaptions(captionUpdates: List<Pair<String, String>>) {
        captionUpdates.forEach { (path, newCaption) ->
            updateCaption(path, newCaption)
        }
    }

    // 查询关键词，并优先展示更近时间的相片
    // 请写出表达式
    @Query("""
    SELECT filename, full_path, parent_path, last_modified, date_taken, size, type, video_duration, is_favorite, deleted_ts, media_store_id, caption
    FROM media
    WHERE deleted_ts = 0 
      AND (filename LIKE '%' || :keyword || '%' COLLATE NOCASE 
           OR caption LIKE '%' || :keyword || '%' COLLATE NOCASE)
    ORDER BY date_taken DESC, last_modified DESC
    """)
    fun getTargetImages(keyword: String): List<Medium>
}
