package com.miaxis.attendance.data.dao;


import com.miaxis.attendance.data.entity.Finger;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
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
public interface FingerDao {

    @Query("SELECT * FROM Finger ORDER BY Finger.id DESC")
    List<Finger> findAll();

    @Query("SELECT * FROM Finger WHERE Finger.userID=:userID")
    List<Finger> findByUserID(String userID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Finger finger);

    @Update
    int update(Finger finger);

    @Delete
    int delete(Finger finger);

    @Query("DELETE FROM Finger WHERE Finger.userID=:userId")
    int delete(String userId);

    @Query("DELETE FROM Finger WHERE Finger.id=:id")
    int delete(long id);

    @Query("DELETE FROM Finger")
    int deleteAll();

    @Query("SELECT COUNT(*) FROM Finger")
    int allCounts();

    @Query("SELECT * FROM Finger ORDER BY Finger.id DESC LIMIT :pageSize OFFSET :offset")
    List<Finger> findPage(int pageSize, int offset);

}
