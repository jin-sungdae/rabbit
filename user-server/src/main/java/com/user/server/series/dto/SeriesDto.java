package com.user.server.series.dto;

import com.user.server.series.entity.ProductSeries;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeriesDto {
    private Long id;
    private String name;

    public static SeriesDto from(ProductSeries series) {
        return SeriesDto.builder()
                .id(series.getId())
                .name(series.getSeriesName())
                .build();
    }
}
