package com.josp.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户数据传输对象
 * 用于创建和更新用户时接收请求参数
 */
@Schema(description = "用户DTO")
@Data
public class UserDTO implements Serializable {

    @Schema(description = "用户ID（更新时必传）")
    private Long id;

    @Schema(description = "姓名")
    @Size(max = 100, message = "姓名长度不能超过100")
    private String name;

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度在3-50之间")
    private String username;

    @Schema(description = "密码（创建时有效，更新时留空则不修改）")
    @Size(min = 6, max = 100, message = "密码长度至少6位")
    private String password;

    @Schema(description = "手机号")
    @Size(max = 20, message = "手机号长度不能超过20")
    private String phone;

    @Schema(description = "性别")
    private String sex;

    @Schema(description = "身份证号")
    @Size(max = 30, message = "身份证号长度不能超过30")
    private String idNumber;

    @Schema(description = "状态：0-禁用，1-正常")
    private Integer status;

    @Schema(description = "角色ID列表")
    private List<Long> roleIds;

    private static final long serialVersionUID = 1L;
}
