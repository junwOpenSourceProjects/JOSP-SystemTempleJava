package com.josp.system.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private List<T> records;
    private long total;

    public PageResult(List<T> records) {
        this.records = records;
        this.total = records.size();
    }
}
