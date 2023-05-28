package com.joey.yx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.joey.yx.common.R;
import com.joey.yx.pojo.Business;
import com.joey.yx.service.BusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 86180
 * @version 1.0
 * @description 商户管理 TODO 修改访问路径
 * @date 2023/4/5 21:15
 */
@RestController
@Slf4j
@RequestMapping("/business")
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    /**
     * 添加商户,添加商户月销表
     * @param business
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Business business){
        businessService.saveBusiness(business);
        return R.success("添加成功!");
    }

    /**
     * 分页获取
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        Page<Business> businessPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Business> lqm = new LambdaQueryWrapper<>();
//        lqm.eq(Business::getStatus,1);
        lqm.eq(Business::getIsDeleted,0);
        lqm.orderByDesc(Business::getSales);
        lqm.orderByAsc(Business::getCreateTime);
        businessService.page(businessPage,lqm);
        return R.success(businessPage);

    }

    /**
     * 删除商户，若商户已经关联了菜品则删除失败
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteById(Long ids){
        businessService.deleteBusiness(ids);
        return R.success("商户删除成功");
    }

    /**
     * 删除商户，若商户已经关联了菜品或套餐则删除失败
     * @param
     * @return
     */
    @GetMapping
    public R<Page> getBusinessById(Long businessId){
        LambdaQueryWrapper<Business> businessLambdaQueryWrapper = new LambdaQueryWrapper<>();
        businessLambdaQueryWrapper.eq(Business::getId,businessId);
        Page<Business> page = new Page<>();
        businessService.page(page,businessLambdaQueryWrapper);
        return R.success(page);
    }

    /**
     * 修改商户
     */
    @PutMapping
    public R<String> update(@RequestBody Business business){
        if (businessService.updateById(business)) {
            return R.success("");
        }
        return R.error("修改失败!");
    }

    /**
     * 获取商户的列表数据
     * @param business
     * @return
     */
    @GetMapping("/list")
    public R<List<Business>> list(Business business) {
        LambdaQueryWrapper<Business> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Business::getId,1L);
        queryWrapper.eq(Business::getIsDeleted,0);
        queryWrapper.eq(Business::getStatus,1);
        queryWrapper.orderByDesc(Business::getSales);
        queryWrapper.orderByAsc(Business::getCreateTime);
        return R.success(businessService.list(queryWrapper));
    }


    @GetMapping("/getNeedTime")
    public R<Integer> list(Long businessId) {

        return R.success(businessService.getNeedTime(businessId));
    }

    @GetMapping("/getName")
    public R<String> getName(Long businessId) {

        return R.success(businessService.getName(businessId));
    }
}
