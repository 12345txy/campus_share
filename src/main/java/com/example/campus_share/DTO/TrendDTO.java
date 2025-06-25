package com.example.campus_share.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrendDTO {
    private List<String> dates;
    private List<Integer> values;
}

