package com.miaxis.attendance.data;

import android.app.Application;

import com.miaxis.attendance.data.dao.AttendanceDao;
import com.miaxis.attendance.data.dao.FaceDao;
import com.miaxis.attendance.data.dao.LocalImageDao;
import com.miaxis.attendance.data.dao.PersonDao;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * @author Tank
 * @date 2021/8/19 5:43 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class AppDataBase {

    private DB mDB;

    private AppDataBase() {
    }

    public static AppDataBase getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final AppDataBase instance = new AppDataBase();
    }

    /**
     * ================================ 静态内部类单例写法 ================================
     **/

    public synchronized void init(String databaseName, Application application) {
        this.mDB = Room.databaseBuilder(application, DB.class, databaseName)
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                    }

                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                    }
                })
                //.addMigrations(MIGRATION_9_10)
                .fallbackToDestructiveMigration()
                .build();
    }

    //    static final Migration MIGRATION_9_10 = new Migration(9, 10) {
    //        @Override
    //        public void migrate(SupportSQLiteDatabase database) {
    //            database.execSQL("ALTER TABLE Express ADD COLUMN phone TEXT");
    //        }
    //    };

    public FaceDao FaceDao() {
        return this.mDB.FaceDao();
    }

    public AttendanceDao AttendanceDao() {
        return this.mDB.AttendanceDao();
    }

    public PersonDao PersonDao() {
        return this.mDB.PersonDao();
    }

    public LocalImageDao LocalImageDao() {
        return this.mDB.LocalImageDao();
    }


}
