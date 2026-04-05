package com.josp.system.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteVO implements Serializable {

    private String name;

    private String path;

    private String component;

    private String redirect;

    private Meta meta;

    private List<RouteVO> children = new ArrayList<>();
}
