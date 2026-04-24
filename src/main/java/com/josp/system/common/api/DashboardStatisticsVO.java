package com.josp.system.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Summary counts returned by /api/v1/dashboard/statistics.
 * Used by the admin overview to display aggregate system figures.
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsVO {
    /** Total registered users */
    private Long userCount;

    /** Total menu/permission records */
    private Long menuCount;

    /** Total active roles */
    private Long roleCount;

    /** Total login events recorded */
    private Long loginCount;
}
