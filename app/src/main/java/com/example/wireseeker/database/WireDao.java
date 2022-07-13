package com.example.wireseeker.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao    //Database access object
public interface WireDao {
    @Insert
    void  insertWords(Wire... words);
    @Update
    int updateWords(Wire... words);
    @Delete
    void deleteWords(Wire...words);

    @Query("SELECT * FROM wire")
    LiveData<List<Wire>> getAll();

    @Query("SELECT * FROM wire")
    List<Wire> getAllList();

    @Query("SELECT * FROM wire WHERE id IN (:userIds)")
    List<Wire> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM wire WHERE wire_num In(:wireNum)")
    List<Wire> findByNum(String wireNum);

//    @Query("SELECT * FROM wire")
//    List<Wire> matchQuery();
//
    @Query("SELECT * FROM wire WHERE wire_num LIKE :matchNum || '%'")
    List<Wire> matchQuery(String matchNum);

}