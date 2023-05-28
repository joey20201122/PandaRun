package com.joey.yx.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/4/19 7:47
 */
@Data
public class DishMonthsales implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long businessId;

    private Long dishId;

    private String dishName;

    private Integer sale;

    private Float price;

}
