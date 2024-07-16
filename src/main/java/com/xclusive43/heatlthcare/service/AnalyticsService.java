package com.xclusive43.heatlthcare.service;

import com.google.firebase.database.*;
import com.xclusive43.heatlthcare.model.AnalyticsResult;
import com.xclusive43.heatlthcare.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AnalyticsService {

    @Autowired
    private PatientService patientService;

    private FirebaseDatabase firebaseDatabase;

    public CompletableFuture<AnalyticsResult> getRealtimeAnalytics() {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference("patients");
        CompletableFuture<AnalyticsResult> future = new CompletableFuture<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Patient> patients = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Patient patient = snapshot.getValue(Patient.class);
                    patients.add(patient);
                }

                AnalyticsResult result = new AnalyticsResult();
                if (!patients.isEmpty()) {
                    double averageAge = patients.stream().mapToInt(Patient::getAge).average().orElse(0);
                    long maleCount = patients.stream().filter(p -> "Male".equalsIgnoreCase(p.getGender())).count();
                    long femaleCount = patients.stream().filter(p -> "Female".equalsIgnoreCase(p.getGender())).count();

                    result.setAverageAge(averageAge);
                    result.setMaleCount(maleCount);
                    result.setFemaleCount(femaleCount);
                }

                future.complete(result);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }

        });

        return future;
    }

    public CompletableFuture<AnalyticsResult>  getAnalytics() {

        return patientService.getAllPatients().thenApply(patients -> {
        AnalyticsResult result = new AnalyticsResult();

        if (!patients.isEmpty()) {
            // Calculate average age
            double averageAge = patients.stream().mapToInt(Patient::getAge).average().orElse(0);

            // Count male and female patients
            long maleCount = patients.stream().filter(p -> "Male".equalsIgnoreCase(p.getGender())).count();
            long femaleCount = patients.stream().filter(p -> "Female".equalsIgnoreCase(p.getGender())).count();

            // Additional analytics based on newly added parameters
            double averageHeartRate = patients.stream().mapToInt(p -> parseHeartRate(p.getHeartRate())).average().orElse(0);
            double averageSpO2 = patients.stream().mapToInt(p -> parseSpO2(p.getSpO2())).average().orElse(0);
            // Assuming vision value is a numeric value, you can calculate average vision value similarly

            result.setAverageAge(averageAge);
            result.setMaleCount(maleCount);
            result.setFemaleCount(femaleCount);
            result.setAverageHeartRate(averageHeartRate);
            result.setAverageSpO2(averageSpO2);
            // Set other relevant analytics based on the additional parameters
        }

        return result;
        });
    }


    private int parseHeartRate(String heartRate) {
        // Parse heart rate value from string format "X bpm"
        try {
            return Integer.parseInt(heartRate.split(" ")[0]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return 0; // Return default or handle error as needed
        }
    }

    private int parseSpO2(String spO2) {
        // Parse SpO2 value from string format "X%"
        try {
            return Integer.parseInt(spO2.replace("%", ""));
        } catch (NumberFormatException e) {
            return 0; // Return default or handle error as needed
        }
    }
}

