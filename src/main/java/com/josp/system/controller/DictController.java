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
 * 字典控制器
 */
@Tag(name = "字典接口")
@RestController
@RequestMapping("/api/v1/dict")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    @Operation(summary = "获取所有字典类型")
    @GetMapping("/types")
    public Result<List<DictType>> getAllDictTypes() {
        return Result.success(dictService.listAllDictTypes());
    }

    @Operation(summary = "根据字典编码获取字典数据")
    @GetMapping("/data/{dictCode}")
    public Result<List<DictData>> getDictDataByCode(@PathVariable String dictCode) {
        return Result.success(dictService.getDictDataByCode(dictCode));
    }

    @Operation(summary = "根据字典类型ID获取字典数据")
    @GetMapping("/data/type/{dictTypeId}")
    public Result<List<DictData>> getDictDataByTypeId(@PathVariable Long dictTypeId) {
        return Result.success(dictService.getDictDataByTypeId(dictTypeId));
    }

    @Operation(summary = "获取所有字典数据（按类型分组）")
    @GetMapping("/data/all")
    public Result<Map<String, List<DictData>>> getAllDictData() {
        return Result.success(dictService.getAllDictDataGrouped());
    }
}
