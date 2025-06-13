package com.example.lab8.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaClimaDTO {
    private String match;
    private String date;
    private String location;
    private Weather weather;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Weather {
        private String condition;
        private double max_temp_c;
        private double min_temp_c;
    }
}
