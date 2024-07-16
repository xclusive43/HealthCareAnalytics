package com.xclusive43.heatlthcare.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
// AnalyticsResult.java
public class AnalyticsResult {
    private double averageAge;
    private long maleCount;
    private long femaleCount;
    private double AverageHeartRate;
    private double AverageSpO2;

    // Getters and Setters
}
