package com.huanghe.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
@AllArgsConstructor
public class Car {

    @NotNull
    private String manufacture;

    @NotNull
    @Size(min = 2,max = 14)
    private String licensePlate;

    @Min(2)
    private int seatCount;
}
