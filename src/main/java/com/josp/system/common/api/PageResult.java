package com.josp.system.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic paginated list result wrapper.
 *
 * <p>Used as the data payload when returning a page of records
 * from list endpoints (user, role, menu, etc.).
 *
 * @param <T> the type of each record in the list
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    /** The list of records for the current page */
    private List<T> records;

    /** Total number of records across all pages */
    private long total;

    /**
     * Convenience constructor assuming the list contains all records (no pagination).
     * Sets total = records.size().
     *
     * @param records the full list of records
     */
    public PageResult(List<T> records) {
        this.records = records;
        this.total = records.size();
    }
}
