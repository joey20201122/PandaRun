package com.joey.yx.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 购物车
 */
@Data
public class ShoppingCart implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //用户id
    private Long userId;

    //菜品id
    private Long dishId;

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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
