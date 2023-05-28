package com.joey.yx.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
菜品口味
 */
@Data
public class DishFlavor implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //菜品id
    private Long dishId;


    //口味名称
    private String name;


    //口味数据list
    private String value;

    //是否删除
    private Integer isDeleted;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;







}
