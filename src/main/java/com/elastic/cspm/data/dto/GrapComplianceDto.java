package com.elastic.cspm.data.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class GrapComplianceDto {
    private String id;
    private String color;
    private List<DataPoint> data;

    @Getter
    @Setter
    public static class DataPoint {
        private String x;
        private int y;
    }
}
