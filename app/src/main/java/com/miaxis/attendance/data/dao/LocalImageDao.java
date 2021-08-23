package com.miaxis.attendance.data.dao;


import com.miaxis.attendance.data.entity.LocalImage;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

/**
 * @author Admin
 * @version $
 * @des
 * @updateAuthor $
 * @updateDes
 */
@Dao
public interface LocalImageDao {

    @Query("SELECT * FROM LocalImage ORDER BY LocalImage.id DESC")
    List<LocalImage> findAll();

    @Query("SELECT * FROM LocalImage WHERE LocalImage.id=:id")
    List<LocalImage> findByID(long id);

    @Insert()
    long insert(LocalImage localImage);

    @Update
    int update(LocalImage localImage);

    @Delete
    int delete(LocalImage localImage);

    @Query("DELETE FROM LocalImage WHERE LocalImage.id=:id")
    int delete(String id);

    @Query("DELETE FROM LocalImage")
    int deleteAll();

    @Query("SELECT COUNT(*) FROM LocalImage")
    int allCounts();

    @Query("SELECT * FROM LocalImage ORDER BY LocalImage.id DESC LIMIT :pageSize OFFSET :offset")
    List<LocalImage> findPage(int pageSize, int offset);

}
