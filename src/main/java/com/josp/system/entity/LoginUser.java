package com.josp.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录用户实体。
 *
 * <p>实现 Spring Security 的 UserDetails 接口，用于认证和授权。
 * 用户与角色通过 {@code account_role} 关联表实现多对多关系。
 * 用户名（username）在系统中全局唯一。
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Schema(description = "登录用户表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "login_user")
public class LoginUser implements UserDetails {

    /**
     * 主键，雪花算法生成
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private Long id;

    /**
     * 姓名（真实姓名或昵称）
     */
    @TableField(value = "name")
    @Schema(description = "姓名")
    private String name;

    /**
     * 用户名（登录账号，全局唯一）
     */
    @TableField(value = "username")
    @Schema(description = "用户名")
    private String username;

    /**
     * 密码（加密存储）
     */
    @TableField(value = "password")
    @Schema(description = "密码")
    private String password;

    /**
     * 手机号（可用于登录或接收短信）
     */
    @TableField(value = "phone")
    @Schema(description = "手机号")
    private String phone;

    /**
     * 性别（M-男，F-女）
     */
    @TableField(value = "sex")
    @Schema(description = "性别")
    private String sex;

    /**
     * 身份证号
     */
    @TableField(value = "id_number")
    @Schema(description = "身份证号")
    private String idNumber;

    /**
     * 状态：0-禁用，1-正常
     */
    @TableField(value = "status")
    @Schema(description = "状态 0:禁用，1:正常")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    @TableField(value = "create_user", fill = FieldFill.INSERT)
    @Schema(description = "创建人")
    private Long createUser;

    /**
     * 修改人ID
     */
    @TableField(value = "update_user", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "修改人")
    private Long updateUser;

    /**
     * 验证码缓存key（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "验证码缓存key")
    private String captchaKey;

    /**
     * 验证码（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "验证码")
    private String captchaCode;

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色编码列表（非数据库字段）。
     * 用于 Spring Security 权限校验。
     */
    @TableField(exist = false)
    private List<String> roles;

    /**
     * 获取用户权限列表。
     * 如果未分配角色，默认返回 ROLE_USER。
     *
     * @return 权限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null || roles.isEmpty()) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status != null && status == 1;
    }

    /**
     * 设置用户角色列表
     *
     * @param roles 角色编码列表
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
