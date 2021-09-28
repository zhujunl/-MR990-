package com.miaxis.attendance.data.model;


import com.miaxis.attendance.data.AppDataBase;
import com.miaxis.attendance.data.entity.Person;
import com.miaxis.common.utils.ListUtils;

import java.util.List;

import timber.log.Timber;

public class PersonModel {

    public static long insert(Person person) {
        long insert = AppDataBase.getInstance().PersonDao().insert(person);
        person.id = insert;
        return insert;
    }

    public static long update(Person person) {
       return AppDataBase.getInstance().PersonDao().update(person);
    }

    public static void delete(Person person) {
        AppDataBase.getInstance().PersonDao().delete(person);
    }

    public static void delete(String userId) {
        AppDataBase.getInstance().PersonDao().delete(userId);
    }

    public static void delete(long id) {
        AppDataBase.getInstance().PersonDao().delete(id);
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

    public static Person findByUserID(String userId) {
        List<Person> byUserID = AppDataBase.getInstance().PersonDao().findByUserID(userId);
        if (ListUtils.isNullOrEmpty(byUserID)) {
            return null;
        }
        return byUserID.get(0);
    }

    public static List<Person> findPage(int pageSize, int page) {
        Timber.e("findPage  pageSize:%s   page:%s", pageSize, page);
        List<Person> page1 = AppDataBase.getInstance().PersonDao().findPage(pageSize, pageSize * (page - 1));
        Timber.e("findPage  counts:%s", page1.size());
        return page1;
    }

}
