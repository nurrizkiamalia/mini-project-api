package com.mini_project.miniproject.dashboard.service;

import com.mini_project.miniproject.dashboard.dto.ComprehensiveRevenueDTO;
import com.mini_project.miniproject.dashboard.dto.RevenueByEventDTO;
import com.mini_project.miniproject.dashboard.dto.SalePerEventCategoryDTO;
import org.springframework.security.core.Authentication;

public interface DashboardService {
    SalePerEventCategoryDTO getSalePerEventCategory(Authentication authentication);
    RevenueByEventDTO getRevenueByEvent(Authentication authentication);

    ComprehensiveRevenueDTO getComprehensiveRevenue(Authentication authentication);

}
