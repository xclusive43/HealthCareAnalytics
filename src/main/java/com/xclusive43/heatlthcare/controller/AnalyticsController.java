package com.xclusive43.heatlthcare.controller;

import com.xclusive43.heatlthcare.model.AnalyticsResult;
import com.xclusive43.heatlthcare.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/historical")
//    @ApiOperation(value = "Get historical analytics", response = AnalyticsResult.class)
    public CompletableFuture<AnalyticsResult> getHistoricalAnalytics() {
        return analyticsService.getAnalytics();
    }

    @GetMapping("/realtime")
//    @ApiOperation(value = "Get realtime analytics", response = AnalyticsResult.class)
    public CompletableFuture<AnalyticsResult> getRealtimeAnalytics() {
        return analyticsService.getRealtimeAnalytics();
    }
}
