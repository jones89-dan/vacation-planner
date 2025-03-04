package com.jones.d424vacationplanner.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.jones.d424vacationplanner.entitities.Excursion;

import java.util.Date;
import java.util.List;
// DAO for Excursions
// Database component with the functionality to securely add, modify, and delete the data

@Dao
public interface ExcursionDAO {
    @Insert
    void insert(Excursion excursion);

    @Query("SELECT * FROM excursions WHERE vacationId = :vacationId")
    List<Excursion> getExcursionsByVacation(int vacationId);

    @Query("SELECT * FROM excursions WHERE id = :id LIMIT 1")
    Excursion getExcursionById(int id);

    @Query("UPDATE excursions SET title = :title, excursionDate = :excursionDate WHERE id = :id")
    void updateExcursion(int id, String title, Date excursionDate);

    @Query("DELETE FROM excursions WHERE id = :id")
    void deleteExcursionById(int id);

    // Vacation search query
    @Query("SELECT * FROM excursions WHERE title LIKE :keyword")
    List<Excursion> searchExcursions(String keyword);

}
