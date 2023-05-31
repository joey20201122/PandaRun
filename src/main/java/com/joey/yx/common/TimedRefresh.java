package com.joey.yx.common;

import com.joey.yx.service.DaySalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/5/31 18:09
 */

@Component
public class TimedRefresh {

    @Autowired
    private DaySalesService salesService;

    //测试 每五秒输出一次
//    @Scheduled(fixedDelay = 5000)
//    public void test (){
//        System.out.println("refresh123"+new Date());
//    }

    //表示每天6，10，14，20点30分执行一次所有销量刷新和更新销量
    @Scheduled(cron = "0 30 6,10,14,20 * * ?")
    public void timedRefreshSale() {
        System.out.println("定时刷新销量"+new Date());
        salesService.refreshAll();
        System.out.println("定时更新用户视图"+new Date());
        salesService.refreshView();
        System.out.println("定时任务完成"+new Date());
    }



}
