package com.mini_project.miniproject.dashboard.controller;

import com.mini_project.miniproject.dashboard.dto.ComprehensiveRevenueDTO;
import com.mini_project.miniproject.dashboard.dto.RevenueByEventDTO;
import com.mini_project.miniproject.dashboard.dto.SalePerEventCategoryDTO;
import com.mini_project.miniproject.dashboard.service.DashboardService;
import com.mini_project.miniproject.responses.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService){
        this.dashboardService = dashboardService;
    }

    @GetMapping("/ticketsale")
    public ResponseEntity<Response<Object>> getSalesPerEventCategory(Authentication authentication) {
        SalePerEventCategoryDTO response = dashboardService.getSalePerEventCategory(authentication);
        return Response.success("Sales per event category successfully retrieved.", response);
    }

    @GetMapping("/revenue")
    public ResponseEntity<Response<Object>> getRevenueByEvent(Authentication authentication) {
        RevenueByEventDTO response = dashboardService.getRevenueByEvent(authentication);
        return Response.success("Revenue per event successfully retrieved.", response);
    }

    @GetMapping("/comprehensive-revenue")
    public ResponseEntity<Response<Object>> getComprehensiveRevenue(Authentication authentication) {
        ComprehensiveRevenueDTO response = dashboardService.getComprehensiveRevenue(authentication);
        return Response.success("Comprehensive revenue successfully retrieved.", response);
    }
}
