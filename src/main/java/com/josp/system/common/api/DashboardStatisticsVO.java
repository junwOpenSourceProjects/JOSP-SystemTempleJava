package com.josp.system.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsVO {
    private Long userCount;
    private Long menuCount;
    private Long roleCount;
    private Long loginCount;
}
