package com.skillmentor.root.controller;

import com.skillmentor.root.common.Constants;
import com.skillmentor.root.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/academic")
@Tag(name = "Dashboard detail", description = "Endpoints for viewing dashboard details")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Operation(
            summary = "Get all dashboard details",
            description = "Retrieves all dashboard-based entries"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No found anything"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    @PreAuthorize(Constants.ADMIN_ROLE_PERMISSION)
    @GetMapping(value = "/dashboard", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> dashboard() {
        Map data = dashboardService.getDashboard();
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

}
