package com.xclusive43.heatlthcare.service;

import com.google.api.core.ApiFuture;
import com.google.firebase.database.*;
import com.xclusive43.heatlthcare.model.Patient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class PatientService {

    private FirebaseDatabase firebaseDatabase;

    public CompletableFuture<String> savePatient(Patient patient) {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        CompletableFuture<Boolean> existsFuture = patientExists(patient.getId());

        return existsFuture.thenCompose(exists -> {
            if (exists) {
                return CompletableFuture.completedFuture("Patient record exists");
            } else {
                DatabaseReference patientsRef = firebaseDatabase.getReference("patients");
                boolean done =  patientsRef.child(patient.getId()).setValueAsync(patient).isDone();
                if(!done){
                  return   CompletableFuture.completedFuture("Patient record saved successfully");
                }else{
                    return  CompletableFuture.completedFuture("Patient record failed to saved!");
                }

            }
        });
    }

    public CompletableFuture<String> savePatients(List<Patient> patients) {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        List<CompletableFuture<String>> saveFutures = new ArrayList<>();

        for (Patient patient : patients) {
            CompletableFuture<Boolean> existsFuture = patientExists(patient.getId());

            CompletableFuture<String> saveFuture = existsFuture.thenCompose(exists -> {
                if (exists) {
                    return CompletableFuture.completedFuture("Patient with ID " + patient.getId() + " already exists");
                } else {
                    DatabaseReference patientsRef = firebaseDatabase.getReference("patients");
                    CompletableFuture<String> patientSaveFuture = new CompletableFuture<>();

                    patientsRef.child(patient.getId()).setValue(patient, (databaseError, databaseReference) -> {
                        if (databaseError != null) {
                            patientSaveFuture.completeExceptionally(databaseError.toException());
                        } else {
                            patientSaveFuture.complete("Patient with ID " + patient.getId() + " saved successfully");
                        }
                    });

                    return patientSaveFuture;
                }
            });

            saveFutures.add(saveFuture);
        }

        // Combine all CompletableFuture objects into a single CompletableFuture
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                saveFutures.toArray(new CompletableFuture[0])
        );

        // Return a CompletableFuture that completes when all saves are done
        return allFutures.thenApply(voidResult -> {
            StringBuilder result = new StringBuilder();
            for (CompletableFuture<String> saveFuture : saveFutures) {
                try {
                    result.append(saveFuture.get()).append("\n");
                } catch (InterruptedException | ExecutionException e) {
                    result.append("Failed to save patient: ").append(e.getMessage()).append("\n");
                }
            }
            return result.toString();
        });
    }


    public CompletableFuture<Patient> getPatientById(String id) {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference("patients").child(id);
        CompletableFuture<Patient> future = new CompletableFuture<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Patient patient = dataSnapshot.getValue(Patient.class);
                future.complete(patient);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });

        return future;
    }

    public boolean deletePatient(String id){
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference("patients").child(id);
        ref.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                System.out.println("removed"+ databaseReference.toString());
            }
        });
        return  true;
    }

    public CompletableFuture<List<Patient>> getAllPatients() {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference("patients");
        CompletableFuture<List<Patient>> future = new CompletableFuture<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Patient> patients = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Patient patient = snapshot.getValue(Patient.class);
                    patients.add(patient);
                }
                future.complete(patients);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });

        return future;
    }

    public CompletableFuture<Boolean> patientExists(String id) {
        DatabaseReference ref = firebaseDatabase.getReference("patients").child(id);
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                future.complete(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });

        return future;
    }

    public CompletableFuture<String> updatePatient(String id, Patient updatedPatient) {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference("patients").child(id);
        CompletableFuture<String> future = new CompletableFuture<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    updatedPatient.setId(id);
                    ApiFuture<Void> updateFuture = ref.setValueAsync(updatedPatient);
                    updateFuture.addListener(() -> {
                        try {
                            updateFuture.get();
                            future.complete("Patient record updated successfully");
                        } catch (Exception e) {
                            future.completeExceptionally(e);
                        }
                    }, Runnable::run);
                } else {
                    future.complete("Patient record does not exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });

        return future;
    }

}
