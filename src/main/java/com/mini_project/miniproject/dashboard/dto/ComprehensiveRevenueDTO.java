package com.mini_project.miniproject.dashboard.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ComprehensiveRevenueDTO {
    private List<YearlyRevenueDTO> yearlyRevenue;
    private List<MonthlyRevenueDTO> monthlyRevenue;
    private List<DailyRevenueDTO> dailyRevenue;
    private BigDecimal totalYearlyRevenue;
    private BigDecimal totalMonthlyRevenue;
    private BigDecimal totalDailyRevenue;

    @Data
    public static class YearlyRevenueDTO {
        private int year;
        private BigDecimal revenue;

    }

    @Data
    public static class MonthlyRevenueDTO {
        private String month;
        private BigDecimal revenue;
    }

    @Data
    public static class DailyRevenueDTO {
        private LocalDate date;
        private BigDecimal revenue;
    }
}

//    @Data
//    public static class HourlyRevenueDTO{
//        private String hour;
//        private BigDecimal revenue;
//    }


//}
