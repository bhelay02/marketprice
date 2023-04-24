package com.test.exercise.marketprice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor(staticName = "of")
public class Price {
    private int id;
    private String instrument;
    private double bid;
    private double ask;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss.SSS")
    private LocalDateTime instrumenttimestamp;
}
