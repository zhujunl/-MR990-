package com.miaxis.attendance.data.model;


import com.miaxis.attendance.data.AppDataBase;
import com.miaxis.attendance.data.entity.Person;

import java.util.List;

public class PersonModel {

    public static long insert(Person person) {
        return AppDataBase.getInstance().PersonDao().insert(person);
    }

    public static void update(Person person) {
        AppDataBase.getInstance().PersonDao().update(person);
    }

    public static void delete(Person person) {
        AppDataBase.getInstance().PersonDao().delete(person);
    }

    public static void delete(String userId) {
        AppDataBase.getInstance().PersonDao().delete(userId);
    }

    public static void deleteAll() {
        AppDataBase.getInstance().PersonDao().deleteAll();
    }

    public static List<Person> findAll() {
        return AppDataBase.getInstance().PersonDao().findAll();
    }

    public static int allCounts() {
        return AppDataBase.getInstance().PersonDao().allCounts();
    }

    public static List<Person> findByUserID(String userID) {
        return AppDataBase.getInstance().PersonDao().findByUserID(userID);
    }

    public static List<Person> findPage(int pageSize, int offset) {
        return AppDataBase.getInstance().PersonDao().findPage(pageSize, offset);
    }

}
