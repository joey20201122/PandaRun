package com.joey.yx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.joey.yx.common.R;
import com.joey.yx.service.DaySalesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/4/19 8:02
 */
@RestController
@Slf4j
@RequestMapping("/sale")
public class SaleController {

    @Autowired
    private DaySalesService daySalesService;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/page")
    public R<Page> page (int page, int pageSize, String businessName){

        return R.success(daySalesService.getPage(page,pageSize,businessName));
    }

    @GetMapping("/pageByDate")
    public R<Page> pageByDate (int page, int pageSize, String businessName, int dateZone){

        return R.success(daySalesService.getPageByDate(page,pageSize,businessName,dateZone));
    }

    @GetMapping("/dishPage")
    public R<Page> dishPage (int page, int pageSize, Long businessId){

        return R.success(daySalesService.getDishSalePageByBusinessId(page,pageSize,businessId));
    }

    @GetMapping("/dishPageByDate")
    public R<Page> dishPageByDate (int page, int pageSize, Long businessId, int dateZone){

        return R.success(daySalesService.getDishSalePageByBusinessIdAndDate(page,pageSize,businessId,dateZone));
    }

    @PutMapping("/refresh")
    public R<String> refresh (){
        daySalesService.refreshAll();
        return R.success("刷新成功");
    }

    @PutMapping("/refresh/{businessId}")
    public R<String> refreshByBusinessId (@PathVariable Long businessId){
        daySalesService.refreshByBusinessId(businessId);
        return R.success("刷新此商户成功");
    }

    @PutMapping("/refreshView")
    public R<String> refreshView (){
        daySalesService.refreshView();
        //
        //清理商户缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        //
        String key = "business_list";
        redisTemplate.delete(key);


        return R.success("更新成功");
    }










}
