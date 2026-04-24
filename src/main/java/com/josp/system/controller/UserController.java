package com.josp.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.josp.system.common.api.PageResult;
import com.josp.system.common.api.Result;
import com.josp.system.dto.UserDTO;
import com.josp.system.entity.LoginUser;
import com.josp.system.entity.Role;
import com.josp.system.service.LoginUserService;
import com.josp.system.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户管理接口
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final LoginUserService loginUserService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "获取用户分页列表")
    @GetMapping
    public Result<PageResult<Map<String, Object>>> getUserPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @Parameter(description = "搜索关键词（用户名/姓名/手机号）") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status
    ) {
        Page<LoginUser> pageParam = new Page<>(page, limit);

        LambdaQueryWrapper<LoginUser> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(LoginUser::getUsername, keyword)
                    .or().like(LoginUser::getName, keyword)
                    .or().like(LoginUser::getPhone, keyword));
        }
        if (status != null) {
            wrapper.eq(LoginUser::getStatus, status);
        }
        wrapper.orderByDesc(LoginUser::getCreateTime);

        IPage<LoginUser> result = loginUserService.getPage(pageParam, wrapper);

        // 填充角色信息
        List<Map<String, Object>> records = result.getRecords().stream()
                .map(user -> {
                    Map<String, Object> map = toMap(user);
                    List<Long> roleIds = loginUserService.getRoleIdsByUserId(user.getId());
                    List<String> roleNames = new ArrayList<>();
                    if (roleIds != null && !roleIds.isEmpty()) {
                        roleNames = roleService.listByIds(roleIds).stream()
                                .map(Role::getName)
                                .collect(Collectors.toList());
                    }
                    map.put("roleIds", roleIds);
                    map.put("roleNames", roleNames);
                    return map;
                })
                .collect(Collectors.toList());

        PageResult<Map<String, Object>> pageResult = new PageResult<>(records, result.getTotal());
        return Result.success(pageResult);
    }

    @Operation(summary = "获取用户详情（含角色ID列表）")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getUserById(@PathVariable Long id) {
        LoginUser user = loginUserService.getById(id);
        if (user == null) {
            return Result.failed("用户不存在");
        }
        Map<String, Object> map = toMap(user);
        // 填充角色信息
        List<Long> roleIds = loginUserService.getRoleIdsByUserId(id);
        List<Role> roles = roleIds.isEmpty() ? Collections.emptyList() : roleService.listByIds(roleIds);
        map.put("roleIds", roleIds);
        map.put("roles", roles);
        return Result.success(map);
    }

    @Operation(summary = "创建用户")
    @PostMapping
    public Result<Map<String, Object>> createUser(@Valid @RequestBody UserDTO dto) {
        // 检查用户名唯一性
        LoginUser existingUser = loginUserService.getByUsername(dto.getUsername());
        if (existingUser != null) {
            return Result.failed("用户名已存在");
        }

        // 检查手机号唯一性
        if (dto.getPhone() != null && !dto.getPhone().isEmpty()) {
            LoginUser phoneUser = loginUserService.getOne(
                    new LambdaQueryWrapper<LoginUser>().eq(LoginUser::getPhone, dto.getPhone()));
            if (phoneUser != null) {
                return Result.failed("手机号已被使用");
            }
        }

        // 构建用户实体
        LoginUser user = LoginUser.builder()
                .name(dto.getName())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword() == null || dto.getPassword().isEmpty() ? "123456" : dto.getPassword()))
                .phone(dto.getPhone())
                .sex(dto.getSex())
                .idNumber(dto.getIdNumber())
                .status(dto.getStatus() != null ? dto.getStatus() : 1)
                .build();

        // 保存用户
        loginUserService.save(user);

        // 绑定角色
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            loginUserService.bindRoles(user.getId(), dto.getRoleIds());
        }

        return Result.success(toMap(user), "创建成功");
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public Result<Map<String, Object>> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        LoginUser existingUser = loginUserService.getById(id);
        if (existingUser == null) {
            return Result.failed("用户不存在");
        }

        // 检查用户名唯一性（排除自己）
        LoginUser usernameUser = loginUserService.getOne(
                new LambdaQueryWrapper<LoginUser>()
                        .eq(LoginUser::getUsername, dto.getUsername())
                        .ne(LoginUser::getId, id));
        if (usernameUser != null) {
            return Result.failed("用户名已存在");
        }

        // 检查手机号唯一性（排除自己）
        if (dto.getPhone() != null && !dto.getPhone().isEmpty()) {
            LoginUser phoneUser = loginUserService.getOne(
                    new LambdaQueryWrapper<LoginUser>()
                            .eq(LoginUser::getPhone, dto.getPhone())
                            .ne(LoginUser::getId, id));
            if (phoneUser != null) {
                return Result.failed("手机号已被使用");
            }
        }

        // 更新用户基本信息（不更新密码）
        existingUser.setName(dto.getName());
        existingUser.setUsername(dto.getUsername());
        existingUser.setPhone(dto.getPhone());
        existingUser.setSex(dto.getSex());
        existingUser.setIdNumber(dto.getIdNumber());
        existingUser.setStatus(dto.getStatus());
        loginUserService.updateById(existingUser);

        // 更新角色绑定
        loginUserService.bindRoles(id, dto.getRoleIds());

        return Result.success(toMap(existingUser), "更新成功");
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        LoginUser user = loginUserService.getById(id);
        if (user == null) {
            return Result.failed("用户不存在");
        }
        // 先清理角色关联，再删除用户
        loginUserService.unbindRoles(id);
        loginUserService.removeById(id);
        return Result.success(null, "删除成功");
    }

    @Operation(summary = "批量删除用户")
    @DeleteMapping("/batch")
    public Result<Void> deleteUsers(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.failed("请选择要删除的用户");
        }
        // 先清理角色关联
        loginUserService.unbindRoles(ids);
        loginUserService.removeByIds(ids);
        return Result.success(null, "批量删除成功");
    }

    @Operation(summary = "重置用户密码")
    @PutMapping("/{id}/password/reset")
    public Result<Void> resetPassword(@PathVariable Long id,
                                       @RequestParam(defaultValue = "123456") String newPassword) {
        LoginUser user = loginUserService.getById(id);
        if (user == null) {
            return Result.failed("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        loginUserService.updateById(user);
        return Result.success(null, "密码重置成功，新密码为：" + newPassword);
    }

    @Operation(summary = "修改用户状态")
    @PutMapping("/{id}/status")
    public Result<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        LoginUser user = loginUserService.getById(id);
        if (user == null) {
            return Result.failed("用户不存在");
        }
        if (status != 0 && status != 1) {
            return Result.failed("状态值无效，0-禁用，1-正常");
        }
        user.setStatus(status);
        loginUserService.updateById(user);
        return Result.success(null, "状态修改成功");
    }

    @Operation(summary = "修改当前用户密码")
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestParam String oldPassword,
                                         @RequestParam String newPassword) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        LoginUser user = loginUserService.getByUsername(username);
        if (user == null) {
            return Result.failed("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.failed("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        loginUserService.updateById(user);
        return Result.success(null, "密码修改成功");
    }

    @Operation(summary = "获取当前用户信息（含角色）")
    @GetMapping("/me")
    public Result<Map<String, Object>> getCurrentUser() {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        LoginUser user = loginUserService.getByUsername(username);
        if (user == null) {
            return Result.failed("用户不存在");
        }
        Map<String, Object> map = toMap(user);
        List<Long> roleIds = loginUserService.getRoleIdsByUserId(user.getId());
        map.put("roleIds", roleIds);
        return Result.success(map);
    }

    @Operation(summary = "导出用户列表")
    @GetMapping("/export")
    public void exportUsers(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            HttpServletResponse response
    ) throws IOException {
        LambdaQueryWrapper<LoginUser> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(LoginUser::getUsername, keyword)
                    .or().like(LoginUser::getName, keyword)
                    .or().like(LoginUser::getPhone, keyword));
        }
        if (status != null) {
            wrapper.eq(LoginUser::getStatus, status);
        }
        wrapper.orderByDesc(LoginUser::getCreateTime);

        List<LoginUser> users = loginUserService.list(wrapper);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("用户列表");

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "用户名", "姓名", "手机号", "性别", "身份证号", "状态", "创建时间"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int rowNum = 1;
        for (LoginUser user : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(String.valueOf(user.getId()));
            row.createCell(1).setCellValue(user.getUsername() != null ? user.getUsername() : "");
            row.createCell(2).setCellValue(user.getName() != null ? user.getName() : "");
            row.createCell(3).setCellValue(user.getPhone() != null ? user.getPhone() : "");
            row.createCell(4).setCellValue(user.getSex() != null ? user.getSex() : "");
            row.createCell(5).setCellValue(user.getIdNumber() != null ? user.getIdNumber() : "");
            row.createCell(6).setCellValue(user.getStatus() != null && user.getStatus() == 1 ? "正常" : "禁用");
            row.createCell(7).setCellValue(user.getCreateTime() != null ? sdf.format(user.getCreateTime()) : "");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        String fileName = URLEncoder.encode("用户列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();
    }

    @Operation(summary = "导入用户列表")
    @PostMapping("/import")
    public Result<Map<String, Object>> importUsers(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.failed("请选择要导入的文件");
        }
        String filename = file.getOriginalFilename();
        if (filename != null && !filename.matches(".*\\.(xlsx|xls)$")) {
            return Result.failed("仅支持 .xlsx, .xls 格式的文件");
        }

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() < 2) {
                return Result.failed("导入数据为空");
            }

            int successCount = 0;
            int failCount = 0;
            StringBuilder failMsg = new StringBuilder();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String username = getCellValueAsString(row.getCell(1));
                    if (username == null || username.isEmpty()) {
                        failCount++;
                        failMsg.append(String.format("第%d行：用户名不能为空\n", i + 1));
                        continue;
                    }
                    LoginUser existing = loginUserService.getByUsername(username);
                    if (existing != null) {
                        failCount++;
                        failMsg.append(String.format("第%d行：用户名%s已存在\n", i + 1, username));
                        continue;
                    }

                    LoginUser user = LoginUser.builder()
                            .username(username)
                            .name(getCellValueAsString(row.getCell(2)))
                            .phone(getCellValueAsString(row.getCell(3)))
                            .sex(getCellValueAsString(row.getCell(4)))
                            .idNumber(getCellValueAsString(row.getCell(5)))
                            .password(passwordEncoder.encode("123456"))
                            .status(1)
                            .build();
                    loginUserService.save(user);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failMsg.append(String.format("第%d行：导入失败\n", i + 1));
                    log.error("导入第{}行数据失败", i + 1, e);
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("failMsg", failMsg.toString());

            if (failCount > 0 && successCount > 0) {
                return Result.success(result, String.format("导入完成，成功%d条，失败%d条", successCount, failCount));
            } else if (failCount > 0) {
                return Result.success(result, "导入失败");
            } else {
                return Result.success(result, String.format("导入成功，共%d条", successCount));
            }
        } catch (Exception e) {
            log.error("导入文件解析失败", e);
            return Result.failed("文件解析失败，请检查文件格式");
        }
    }

    @Operation(summary = "获取用户选项列表（下拉框用）")
    @GetMapping("/options")
    public Result<List<Map<String, Object>>> getUserOptions() {
        LambdaQueryWrapper<LoginUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoginUser::getStatus, 1);
        wrapper.select(LoginUser::getId, LoginUser::getName, LoginUser::getUsername);
        wrapper.orderByAsc(LoginUser::getName);

        List<LoginUser> users = loginUserService.list(wrapper);
        List<Map<String, Object>> options = users.stream()
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", u.getId());
                    map.put("label", (u.getName() != null ? u.getName() : "") + "(" + u.getUsername() + ")");
                    return map;
                })
                .collect(Collectors.toList());
        return Result.success(options);
    }

    @Operation(summary = "下载用户导入模板")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("用户导入模板");

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        Row headerRow = sheet.createRow(0);
        String[] headers = {"用户名", "姓名", "手机号", "性别", "身份证号"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 20 * 256);
        }

        String fileName = URLEncoder.encode("用户导入模板", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();
    }

    /**
     * 实体转 Map
     */
    private Map<String, Object> toMap(LoginUser user) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", user.getId());
        map.put("name", user.getName());
        map.put("username", user.getUsername());
        map.put("phone", user.getPhone());
        map.put("sex", user.getSex());
        map.put("idNumber", user.getIdNumber());
        map.put("status", user.getStatus());
        map.put("createTime", user.getCreateTime());
        map.put("updateTime", user.getUpdateTime());
        map.put("createUser", user.getCreateUser());
        map.put("updateUser", user.getUpdateUser());
        return map;
    }

    /**
     * Excel 单元格值转字符串
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }
}
