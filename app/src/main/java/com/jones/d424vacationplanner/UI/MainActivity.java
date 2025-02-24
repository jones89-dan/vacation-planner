package com.jones.d424vacationplanner.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jones.d424vacationplanner.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the button by its ID
        Button createVacationLink = findViewById(R.id.vacation_page_link);
        Button listVacationsLink = findViewById(R.id.list_vacation_page_link);

        // Set up the create vacation button's click listener
        createVacationLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the create Vacation Page
                Intent intent = new Intent(MainActivity.this, CreateVacation.class);
                startActivity(intent);
            }
        });

        // Set up the create vacation button's click listener
        listVacationsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the create Vacation Page
                Intent intent = new Intent(MainActivity.this, ListVacations.class);
                startActivity(intent);
            }
        });
    }
}