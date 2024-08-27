package com.elastic.cspm.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GraphScanDto {
    private String category;
    private Integer count;
    private String countColor;
}
