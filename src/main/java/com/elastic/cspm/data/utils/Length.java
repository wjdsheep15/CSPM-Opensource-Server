package com.elastic.cspm.data.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum Length {
    SMALL, // SMALL의 길이를 50으로 설정
    MEDIUM,
    LARGE;

    public static class Lengths{
        public static final int SMALL = 50;
        public static final int MEDIUM = 100;
        public static final int LARGE = 150;
    }
}