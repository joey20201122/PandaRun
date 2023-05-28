package com.joey.yx.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 86180
 * @version 1.0
 * @description 商户结构表
 * @date 2023/4/5 21:03
 */

@Data
public class Business implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //菜名
    private String name;

    //图片
    private String image;

    //简介
    private String briefly;

    //荣誉
    private String title;

    //销量
    private Integer sales;

    //状态
    private Integer status;

    //是否删除
    private Integer isDeleted;

    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


}
