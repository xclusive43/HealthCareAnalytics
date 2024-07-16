package com.xclusive43.heatlthcare.controller;

import com.xclusive43.heatlthcare.model.Patient;
import com.xclusive43.heatlthcare.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/patients")
public class Gateway {

    @Autowired
    PatientService patientService;

    @PostMapping
//    @ApiOperation(value = "Add a new patient")
    public CompletableFuture<String> savePatient(@RequestBody Patient patient) {
        return patientService.savePatient(patient);
    }

    @PostMapping("/saveAll")
//    @ApiOperation(value = "Add List of new patient")
    public ResponseEntity<String> saveAllPatients(@RequestBody List<Patient> patients) {
        CompletableFuture<String> saveResultFuture = patientService.savePatients(patients);

        try {
            String saveResult = saveResultFuture.get(); // Blocking call to wait for completion
            return ResponseEntity.ok(saveResult);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save patients: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
//    @ApiOperation(value = "Get a patient by ID")
    public CompletableFuture<Patient> getPatient(@PathVariable String id) {
        return patientService.getPatientById(id);
    }

    @DeleteMapping("/{id}")
//    @ApiOperation(value = "Delete a patient by ID")
    public boolean deletePatient(@PathVariable String id){
        return patientService.deletePatient(id);
    }

    @GetMapping
//    @ApiOperation(value = "Get all patients")
    public CompletableFuture<List<Patient>> getAllPatients() {
        return patientService.getAllPatients();
    }

    @PutMapping("/{id}")
//    @ApiOperation(value = "Update an existing patient")
    public CompletableFuture<String> updatePatient(@PathVariable String id, @RequestBody Patient patient) {
        // Ensure the ID in the URL path is used, not from the request body
        patient.setId(id);
        return patientService.updatePatient(id, patient);
    }
}
