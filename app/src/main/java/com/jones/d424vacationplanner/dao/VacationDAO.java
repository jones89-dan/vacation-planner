package com.jones.d424vacationplanner.dao;

import android.database.sqlite.SQLiteConstraintException;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.jones.d424vacationplanner.entitities.Excursion;
import com.jones.d424vacationplanner.entitities.Vacation;

import java.util.Date;
import java.util.List;

// Database component with the functionality to securely add, modify, and delete the data
// Vacation DAO
@Dao
public interface VacationDAO {
    @Insert
    void insert(Vacation vacation);

    @Insert
    void insertExcursion(Excursion excursion);

    @Query("SELECT * FROM vacations")
    List<Vacation> getAllVacations();

    @Delete
    void deleteVacation(Vacation vacation);

    @Query("DELETE FROM vacations WHERE id = :id")
    void deleteVacationById(int id);

    @Query("SELECT COUNT(*) FROM excursions WHERE vacationId = :vacationId")
    int getExcursionCount(int vacationId);

    @Query("SELECT * FROM vacations WHERE id = :id LIMIT 1")
    Vacation getVacationById(int id);

    @Query("UPDATE vacations SET title = :title, place = :place, startDate = :startDate, endDate = :endDate WHERE id = :id")
    void updateVacation(int id, String title, String place, Date startDate, Date endDate);

    @Transaction
    default void deleteVacationIfNoExcursions(Vacation vacation) {
        if (getExcursionCount(vacation.getId()) > 0) {
            throw new SQLiteConstraintException("Cannot delete vacation with excursions.");
        }
        deleteVacation(vacation);
    }

    // Vacation search query
    @Query("SELECT * FROM vacations WHERE title LIKE :keyword OR place LIKE :keyword")
    List<Vacation> searchVacations(String keyword);
}