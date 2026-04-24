package com.josp.system.controller;

import com.josp.system.common.api.Result;
import com.josp.system.entity.DictData;
import com.josp.system.entity.DictType;
import com.josp.system.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Dictionary Controller providing read-only endpoints for dictionary data.
 * Used by frontend to fetch dictionary types and data for dropdowns and displays.
 *
 * @author JOSP System
 * @version 1.0
 */
@Tag(name = "Dictionary Data")
@RestController
@RequestMapping("/api/v1/dict")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    /**
     * Gets all dictionary types.
     *
     * @return list of all dictionary types
     */
    @Operation(summary = "Get all dictionary types")
    @GetMapping("/types")
    public Result<List<DictType>> getAllDictTypes() {
        return Result.success(dictService.listAllDictTypes());
    }

    /**
     * Gets dictionary data by dictionary code.
     *
     * @param dictCode dictionary type code
     * @return list of dictionary data items
     */
    @Operation(summary = "Get dictionary data by code")
    @GetMapping("/data/{dictCode}")
    public Result<List<DictData>> getDictDataByCode(@PathVariable String dictCode) {
        return Result.success(dictService.getDictDataByCode(dictCode));
    }

    /**
     * Gets dictionary data by dictionary type ID.
     *
     * @param dictTypeId dictionary type ID
     * @return list of dictionary data items
     */
    @Operation(summary = "Get dictionary data by type ID")
    @GetMapping("/data/type/{dictTypeId}")
    public Result<List<DictData>> getDictDataByTypeId(@PathVariable Long dictTypeId) {
        return Result.success(dictService.getDictDataByTypeId(dictTypeId));
    }

    /**
     * Gets all dictionary data grouped by dictionary type.
     *
     * @return map of dictionary code to list of dictionary data
     */
    @Operation(summary = "Get all dictionary data grouped")
    @GetMapping("/data/all")
    public Result<Map<String, List<DictData>>> getAllDictData() {
        return Result.success(dictService.getAllDictDataGrouped());
    }
}
