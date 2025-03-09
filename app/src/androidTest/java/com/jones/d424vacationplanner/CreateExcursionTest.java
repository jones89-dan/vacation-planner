package com.jones.d424vacationplanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.room.Dao;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.jones.d424vacationplanner.dao.ExcursionDAO;
import com.jones.d424vacationplanner.dao.VacationDAO;
import com.jones.d424vacationplanner.database.AppDatabase;
import com.jones.d424vacationplanner.entitities.Excursion;
import com.jones.d424vacationplanner.entitities.Vacation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateExcursionTest {
    private AppDatabase db;
    private ExcursionDAO excursionDAO;
    private String excursionDatestr;
    private Date excursionDate;
    private VacationDAO vacationDAO;
    private  String startDateStr;
    private String endDateStr;
    private Date startDate;
    private Date endDate;

    @Before
    public void createDb() {
        // Get application context
        Context context = ApplicationProvider.getApplicationContext();

        // Create an in-memory version of the Room database (data is lost after tests)
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries() // ONLY for testing purposes!
                .build();

        // Get DAO
        excursionDAO = db.excursionDAO();
        vacationDAO = db.vacationDAO();
    }

    @Test
    public void insertExcursionWithVacation() throws ParseException {

        // Set up dates for test vacation
        startDateStr = "06/10/2025";
        endDateStr = "06/20/2025";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        startDate = dateFormat.parse(startDateStr);
        endDate = dateFormat.parse(endDateStr);
        // Create a new vacation to test excursion to vacation association
        Vacation vacation = new Vacation("Hawaii Trip", "Moana Surfrider", startDate, endDate);

        // Insert into database
        vacationDAO.insert(vacation);
        // Retrieve all vacations
        List<Vacation> vacations = vacationDAO.getAllVacations();
        // getVacation id
        int vacationID = vacations.get(0).getId();

        // Set up dates for test excursion
        excursionDatestr = "06/10/2025";
        excursionDate = dateFormat.parse(excursionDatestr);
        // Create a new excursion
        Excursion excursion = new Excursion(vacationID, "Surf Lesson", excursionDate);
        // Insert excursion
        excursionDAO.insert(excursion);
        // Retrieve excursion by the test vacation id
        List<Excursion> testExcursion = excursionDAO.getExcursionsByVacation(vacationID);

        // Check that the excursion was inserted and contains all attributes
        assertNotNull(testExcursion);
        assertEquals(1, testExcursion.size());
        assertEquals("Surf Lesson", testExcursion.get(0).getTitle());
        assertNotNull(testExcursion.get(0).getExcursionDate());
    }

    @After
    public void closeDb() {
        db.close();
    }

}
