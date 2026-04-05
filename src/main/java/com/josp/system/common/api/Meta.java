package com.josp.system.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Meta implements Serializable {

    private String title;
    private String icon;
    private Boolean hidden;
    private Boolean keepAlive;
    private Boolean alwaysShow;
    private List<String> roles;
}
