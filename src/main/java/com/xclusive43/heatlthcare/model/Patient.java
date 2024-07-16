package com.xclusive43.heatlthcare.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Patient {
    private String id;
    public String name;
    private int age;
    private String gender;
    private String medicalHistory;
    private String dob;
    private String phone;
    private String bloodPressure;
    private String heartRate;
    private String spO2;
    private String visionValue;
    private List<BloodParameter> bloodParameters;
}
