package com.josp.system.controller;

import com.josp.system.common.api.PageResult;
import com.josp.system.common.api.Result;
import com.josp.system.dto.DictDataDTO;
import com.josp.system.dto.DictTypeDTO;
import com.josp.system.entity.DictData;
import com.josp.system.entity.DictType;
import com.josp.system.service.DictManageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理控制器
 */
@Tag(name = "字典管理接口")
@RestController
@RequestMapping("/api/v1/dictManage")
@RequiredArgsConstructor
public class DictManageController {

    private final DictManageService dictManageService;

    // ==================== 字典类型接口 ====================

    @Operation(summary = "分页查询字典类型")
    @GetMapping("/type/page")
    public Result<PageResult<DictType>> pageDictTypes(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code) {
        return Result.success(dictManageService.pageDictTypes(pageNum, pageSize, name, code));
    }

    @Operation(summary = "获取所有字典类型")
    @GetMapping("/type/list")
    public Result<List<DictType>> listAllDictTypes() {
        return Result.success(dictManageService.listAllDictTypes());
    }

    @Operation(summary = "根据ID获取字典类型详情")
    @GetMapping("/type/{id}")
    public Result<DictType> getDictTypeById(@PathVariable Long id) {
        return Result.success(dictManageService.getDictTypeById(id));
    }

    @Operation(summary = "创建字典类型")
    @PostMapping("/type")
    public Result<Boolean> createDictType(@RequestBody DictTypeDTO dto) {
        return Result.success(dictManageService.createDictType(dto));
    }

    @Operation(summary = "更新字典类型")
    @PutMapping("/type")
    public Result<Boolean> updateDictType(@RequestBody DictTypeDTO dto) {
        return Result.success(dictManageService.updateDictType(dto));
    }

    @Operation(summary = "删除字典类型")
    @DeleteMapping("/type/{id}")
    public Result<Boolean> deleteDictType(@PathVariable Long id) {
        return Result.success(dictManageService.deleteDictType(id));
    }

    // ==================== 字典数据接口 ====================

    @Operation(summary = "分页查询字典数据")
    @GetMapping("/data/page")
    public Result<PageResult<DictData>> pageDictData(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long dictTypeId,
            @RequestParam(required = false) String label) {
        return Result.success(dictManageService.pageDictData(pageNum, pageSize, dictTypeId, label));
    }

    @Operation(summary = "根据字典类型ID获取字典数据")
    @GetMapping("/data/type/{dictTypeId}")
    public Result<List<DictData>> listDictDataByTypeId(@PathVariable Long dictTypeId) {
        return Result.success(dictManageService.listDictDataByTypeId(dictTypeId));
    }

    @Operation(summary = "根据字典编码获取字典数据")
    @GetMapping("/data/code/{dictCode}")
    public Result<List<DictData>> listDictDataByCode(@PathVariable String dictCode) {
        return Result.success(dictManageService.listDictDataByCode(dictCode));
    }

    @Operation(summary = "根据ID获取字典数据详情")
    @GetMapping("/data/{id}")
    public Result<DictData> getDictDataById(@PathVariable Long id) {
        return Result.success(dictManageService.getDictDataById(id));
    }

    @Operation(summary = "创建字典数据")
    @PostMapping("/data")
    public Result<Boolean> createDictData(@RequestBody DictDataDTO dto) {
        return Result.success(dictManageService.createDictData(dto));
    }

    @Operation(summary = "更新字典数据")
    @PutMapping("/data")
    public Result<Boolean> updateDictData(@RequestBody DictDataDTO dto) {
        return Result.success(dictManageService.updateDictData(dto));
    }

    @Operation(summary = "删除字典数据")
    @DeleteMapping("/data/{id}")
    public Result<Boolean> deleteDictData(@PathVariable Long id) {
        return Result.success(dictManageService.deleteDictData(id));
    }
}
