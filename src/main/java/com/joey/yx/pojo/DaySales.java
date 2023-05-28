package com.joey.yx.pojo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/4/19 7:35
 */
@Data
public class DaySales implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long dishId;

    private Long businessId;

    private Float price;

    private Integer sale;

    private LocalDate date;
}
