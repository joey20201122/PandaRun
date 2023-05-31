package com.joey.yx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.joey.yx.dto.OrdersDto;
import com.joey.yx.pojo.Orders;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/4/10 11:10
 */
public interface OrdersService extends IService<Orders> {

    //提交订单
    public Long submit(Orders orders);

    //user返回dto
    Page userPageWithDto(int page, int pageSize ,Long userID);

    //返回dto
    Page pageWithDto(int page, int pageSize,Long businessId);

    void pay(Long orderId);

    //获取一个订单的等待时间
    Integer getWaitTime(Long businessId);

    //制作完成
    void makeDown(Long orderId);

    //开始配送
    void delivery(Long orderId);

    void finish(Long orderId);

    void cancel(Long orderId);

    Page getDeliveryPage(int page, int pageSize);

    void againOrder(Long orderId);

    Integer getAwaitTime(Long orderId);

    OrdersDto getDtoById(Long orderId);

    Page getDtoPaegAll(int page, int pageSize, Long number, String beginTime, String endTime);

    Page getDtoPageByBusinessId(int page, int pageSize, Long number, String beginTime, String endTime, Long businessId);
}
