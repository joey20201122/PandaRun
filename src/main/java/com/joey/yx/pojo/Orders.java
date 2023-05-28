package com.joey.yx.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单
 */
@Data
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //下单用户id
    private Long userId;

    //用户名称
    private String userName;

    //商户id
    private Long businessId;

    //商户名称
    private String businessName;

    //地址id
    private Long addressId;

    //类型 0订餐 1外卖
    private Integer type;

    //订单号
    private Integer number;

    //备注
    private String remark;

    //收货人
    private String consignee;

    //手机号
    private String phone;

    //地址
    private String address;

    @TableField(fill = FieldFill.INSERT)
    //订单生成时间
    private LocalDateTime createTime;

    //结账时间
    private LocalDateTime payTime;

    //预定时间 预定什么时候起做
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime bookTime;

    //制作时长 单位：秒
    private Integer makeTime;

    //预计等待时长 秒
    private Integer awaitTime;

    //服务费
    private Float serviceCharge;

    //预期完成时间 预期的做好餐的时间/外卖送到的时间
    private LocalDateTime expectedTime;

    //实际完成时间 实际上用户取餐时间/外卖送到时间
    private LocalDateTime indeedTime;

    //状态 0待付款 1制作中 2待取餐 3待派送 4派送中 5待评价 6已完成
    private Integer status;

    //实收金额
    private Float amount;

}
