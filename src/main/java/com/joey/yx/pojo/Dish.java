package com.joey.yx.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 菜品
 */
@Data
public class Dish implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //商户id
    private Long businessId;

    //菜品名称
    private String name;

    //菜品名称
    private String businessName;

    //图片
    private String image;

    //描述
    private String description;

    //荣誉
    private String title;

    //菜品价格
    private Float price;

    //制作用时 单位：秒
    private Integer taketime;

    //月销量
    private Integer sales;

    //0 停售 1 起售
    private Integer status;

    //是否删除
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;



}
