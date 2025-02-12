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
    SELECT filename, full_path, parent_path, last_modified, date_taken, size, type, video_duration, is_favorite, deleted_ts, media_store_id
    FROM media 
    WHERE type = :imageType AND deleted_ts = 0 
    ORDER BY date_taken DESC, last_modified DESC
    """)
    fun getAllImages(imageType: Int = TYPE_IMAGES): List<Medium>

    @Query("""
    SELECT m.filename, m.full_path, m.parent_path, m.last_modified, m.date_taken, 
        m.size, m.type, m.video_duration, m.is_favorite, m.deleted_ts, m.media_store_id
    FROM media m
    WHERE m.type = :imageType AND m.deleted_ts = 0
    AND NOT EXISTS (
        SELECT 1 
        FROM captions c 
        WHERE c.full_path = m.full_path AND c.type = :captionType
    )
    ORDER BY m.date_taken DESC, m.last_modified DESC
    """)
    fun getAllImagesNotHaveCaption(imageType: Int = TYPE_IMAGES, captionType: String="ml_kit_ocr"): List<Medium>


    // 查询关键词，并优先展示更近时间的相片
    @Query("""
    SELECT m.filename, m.full_path, m.parent_path, m.last_modified, m.date_taken, 
            m.size, m.type, m.video_duration, m.is_favorite, m.deleted_ts, m.media_store_id
    FROM media m
    INNER JOIN captions c ON m.full_path = c.full_path
    WHERE (c.filename LIKE '%' || :keyword || '%' OR c.content LIKE '%' || :keyword || '%')
    AND m.deleted_ts = 0
    ORDER BY m.date_taken DESC, m.last_modified DESC
    """)
    fun getTargetImages(keyword: String): List<Medium>
}
