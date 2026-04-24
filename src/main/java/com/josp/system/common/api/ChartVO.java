package com.josp.system.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic chart data structure for ECharts series.
 *
 * <p>Provides a standardized payload format for line, bar, and area charts.
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartVO {
    /** Labels for the X-axis (e.g. month names, category names) */
    private List<String> labels;

    /** Series definitions, each containing a name, chart type, and Y-axis data */
    private List<Series> series;

    /**
     * A single series in a chart.
     *
     * @param name  series label (e.g. "Actual Sales", "Target")
     * @param type  chart type: "line", "bar", "pie", "area" (handled by ECharts)
     * @param data  Y-axis values aligned with the labels array
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Series {
        private String name;
        private String type;
        private List<Object> data;
    }
}
