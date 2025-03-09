package com.jones.d424vacationplanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.jones.d424vacationplanner.dao.VacationDAO;
import com.jones.d424vacationplanner.database.AppDatabase;
import com.jones.d424vacationplanner.entitities.Vacation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateVacationTest {

        private AppDatabase db;
        private VacationDAO vacationDAO;
        private  String startDateStr;
        private String endDateStr;
        private Date startDate;
        private Date endDate;

        @Before
        public void createDb() {
            // Get application context
            Context context = ApplicationProvider.getApplicationContext();

            // Create an in-memory version of the Room database (data does not persist)
            db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                    .allowMainThreadQueries()
                    .build();

            // Get DAO
            vacationDAO = db.vacationDAO();
        }

        @Test
        public void insertVacationAndRetrieve() throws ParseException {

            // Set date formats for start and end dates
            startDateStr = "06/10/2025";
            endDateStr = "06/20/2025";
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            startDate = dateFormat.parse(startDateStr);
            endDate = dateFormat.parse(endDateStr);

            // Create a new vacation
            Vacation vacation = new Vacation("Hawaii Trip", "Moana Surfrider", startDate, endDate);
            // Insert into database
            vacationDAO.insert(vacation);
            // Retrieve all vacations
            List<Vacation> vacations = vacationDAO.getAllVacations();

            // Check that the vacation was inserted and attributes are present.
            assertNotNull(vacations);
            assertEquals(1, vacations.size());
            assertEquals("Hawaii Trip", vacations.get(0).getTitle());
            assertEquals("Moana Surfrider", vacations.get(0).getPlace());
            assertNotNull(vacations.get(0).getStartDate());
            assertNotNull(vacations.get(0).getEndDate());

        }

    @After
    public void closeDb() {
        db.close();
    }

}
