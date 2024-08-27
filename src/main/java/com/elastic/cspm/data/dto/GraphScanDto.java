package com.elastic.cspm.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GraphScanDto {
    private String category;
    private Integer count;
    private String countColor;
}
