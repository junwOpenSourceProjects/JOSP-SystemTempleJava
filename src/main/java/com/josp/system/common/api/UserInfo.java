package com.josp.system.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {

    private Long userId;

    private String username;

    private String nickname;

    private String avatar;

    private List<String> roles;

    private List<String> perms;
}
