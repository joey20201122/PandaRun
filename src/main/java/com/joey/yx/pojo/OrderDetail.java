package com.joey.yx.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单明细
 */
@Data
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //订单id
    private Long orderId;

    //菜品id
    private Long dishId;

    //商户id
    private Long businessId;

    //名称
    private String name;

    //图片
    private String image;

    //口味
    private String dishFlavor;

    //数量
    private Integer number;

    //价格
    private Float price;

    //金额
    private Float amount;

    //服务费
    private Float serviceCharge;










}
