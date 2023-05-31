package com.joey.yx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.joey.yx.common.R;
import com.joey.yx.dto.OrdersDto;
import com.joey.yx.pojo.Orders;
import com.joey.yx.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/4/10 11:14
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService orderService;

    //生成订单
    @PostMapping("/submit")
    public R<Long> submit(@RequestBody Orders orders){

        return R.success(orderService.submit(orders));
    }

    //付款
    @PutMapping("/pay/{orderId}")
    public R<String> pay(@PathVariable Long orderId){
        if (orderId == null) {
            return R.error("此订单不存在!");
        }
        orderService.pay(orderId);


        return R.success("支付成功!");
    }

    //取消订单
    @DeleteMapping("/cancel/{orderId}")
    public R<String> cancel(@PathVariable Long orderId){
        if (orderId == null) {
            return R.error("此订单不存在!");
        }
        orderService.cancel(orderId);


        return R.success("取消订单成功!");
    }

    //一笔订单制作完成
    @PutMapping("/makeDown/{orderId}")
    public R<String> makeDown(@PathVariable Long orderId){
        orderService.makeDown(orderId);

        return R.success("制作完成!");
    }

    //配送订单
    @PutMapping("/delivery/{orderId}")
    public R<String> delivery(@PathVariable Long orderId){
        orderService.delivery(orderId);

        return R.success("开始配送!");
    }

    //配送订单
    @GetMapping("/delivery")
    public R<Page> delivery(int page , int pageSize){


        return R.success(orderService.getDeliveryPage(page,pageSize));
    }

    //订单完成
    @PutMapping("/finish/{orderId}")
    public R<String> finish(@PathVariable Long orderId){
        orderService.finish(orderId);

        return R.success("订单完成!");
    }

    //用户获取orderPage
    @GetMapping("/userPage")
    public R<Page> userPage(int page , int pageSize ,Long userId){


        return R.success(orderService.userPageWithDto(page,pageSize,userId));
    }

    //运营端获取所有商户Order
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, Long number, String beginTime, String endTime){



        return R.success(orderService.getDtoPaegAll(page ,pageSize ,number ,beginTime ,endTime));

    }

    //根据UserId获取一个用户的Order
    @GetMapping("/queryByUserId")
    public R<List> page(Long userId, Integer size){

        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(userId!=null,Orders::getUserId,userId);
        ordersLambdaQueryWrapper.orderByDesc(Orders::getCreateTime);
        if (size!=null&&size > 0){
            ordersLambdaQueryWrapper.last("limit 0,5");
        }
        return R.success(orderService.list(ordersLambdaQueryWrapper));

    }

    //商户获取orderPage
    @GetMapping("/pageType")
    public R<Page> page(int page, int pageSize, Long number, String beginTime, String endTime,Long businessId){

        return R.success(orderService.getDtoPageByBusinessId(page,pageSize,number,beginTime,endTime,businessId));

    }

    //商户获取OrderDtoPage(处理订单)
    @GetMapping("/handle")
    public R<Page> handlePage(int page, int pageSize,Long businessId){


        return R.success(orderService.pageWithDto(page,pageSize,businessId));

    }

    //获取一个订单的等待时间
    @GetMapping("/getWaitTime/{orderId}")
    public R<String> getWaitTime(@PathVariable Long orderId){
        if (orderId == null){
            return R.error("订单号不能为空");
        }
        Orders byId = orderService.getById(orderId);
        if (byId == null) {
            return R.error("此订单不存在");
        }
        LocalDateTime expectedTime = byId.getExpectedTime();
        return R.success(expectedTime.getHour()+","+expectedTime.getMinute());
    }

    @PostMapping("/again/{orderId}")
    public R<String> again(@PathVariable Long orderId){
        if (orderId == null){
            return R.error("订单号不能为空");
        }

        orderService.againOrder(orderId);
        return R.success("成功!");
    }

    @GetMapping("/getAwaitTime/{orderId}")
    public R<Integer> getAwaitTime(@PathVariable Long orderId){
        if (orderId == null){
            return R.error("订单号不能为空");
        }


        return R.success(orderService.getAwaitTime(orderId));
    }

    @GetMapping("/detail/{orderId}")
    public R<OrdersDto> getDetail(@PathVariable Long orderId){
        if (orderId == null){
            return R.error("订单号不能为空");
        }

        return R.success(orderService.getDtoById(orderId));
    }
}
