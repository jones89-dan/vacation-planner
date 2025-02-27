package com.jones.d424vacationplanner.search;

import com.jones.d424vacationplanner.entitities.Vacation;
import com.jones.d424vacationplanner.dao.VacationDAO;

import java.util.List;

// Part B - Inheritance - Child class that inherits from Search class
public class VacationSearch extends Search<Vacation>{

    private VacationDAO vacationDao;

    // Use the VacationDAO to interact with the database
    public VacationSearch(VacationDAO vacationDao) {
        this.vacationDao = vacationDao;
    }


    // Override from the parent class the parent class
    @Override
    public void performSearch(String keyword, SearchCallback<Vacation> callback) {
        executorService.execute(() -> {
            List<Vacation> results = vacationDao.searchVacations("%" + keyword + "%");
            callback.onResults(results);
        });
    }
}
