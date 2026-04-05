package com.josp.system.common.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "验证码响应对象")
public class CaptchaResult implements Serializable {

    @Schema(description = "验证码唯一标识")
    private String captchaKey;

    @Schema(description = "验证码Base64图片内容")
    private String captchaBase64;
}
