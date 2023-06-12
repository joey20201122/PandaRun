package com.joey.yx.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 收藏
 */
@Data
public class Collection implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //用户Id
    private Long userId;

    //商户id
    private Long businessId;

    //菜品名称
    private String businessName;

    //菜品id
    private Long dishId;

    //菜品名称
    private String dishName;

    //菜品名称
//    private String dishFlavor;

    //图片
    private String image;

    //菜品价格
    private Float price;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;



}
