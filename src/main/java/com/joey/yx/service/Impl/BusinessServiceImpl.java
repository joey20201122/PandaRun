package com.joey.yx.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joey.yx.common.CustomException;
import com.joey.yx.mapeer.BusinessMapper;
import com.joey.yx.pojo.*;
import com.joey.yx.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/4/5 21:14
 */
@Service
public class BusinessServiceImpl extends ServiceImpl<BusinessMapper, Business> implements BusinessService {

    @Autowired
    private OrdersService ordersService;
    @Autowired
    private BusinessMonthsalesService businessMonthsalesService;
    @Autowired
    private DishService dishService;
    @Autowired
    private EmployeeService employeeService;

    /***
     * @description 返回所需等待时间
     * @param businessId
     * @return int 单位：秒
     * @author 86180
     * @date  17:01
    */
    @Override
    public int getNeedTime(Long businessId) {
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getBusinessId,businessId);
        ordersLambdaQueryWrapper.eq(Orders::getStatus,1);
        List<Orders> ordersList = ordersService.list(ordersLambdaQueryWrapper);

        int awaitTime = 0;
        for (Orders orders : ordersList) {
            awaitTime = awaitTime + orders.getMakeTime();
        }
        return awaitTime;
    }

    @Override
    public String getBusinessName(Long businessId) {
        LambdaQueryWrapper<Business> businessLambdaQueryWrapper = new LambdaQueryWrapper<>();
        businessLambdaQueryWrapper.eq(Business::getId,businessId);

        return getOne(businessLambdaQueryWrapper).getName();
    }

    @Override
    @Transactional
    public void saveBusiness(Business business) {

        save(business);

        BusinessMonthsales businessMonthsales = new BusinessMonthsales();
        businessMonthsales.setBusinessId(business.getId());
        businessMonthsales.setBusinessName(business.getName());
        businessMonthsales.setSale(0);
        businessMonthsales.setTurnover(0F);
        businessMonthsalesService.save(businessMonthsales);
    }

    @Override
    public void deleteBusiness(Long ids) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(Dish::getBusinessId,ids);
        List<Dish> dishList = dishService.list(dishLambdaQueryWrapper);
        if (dishList.size() > 0){
            throw new CustomException("删除失败!关联菜品尚未删除");
        }

        LambdaQueryWrapper<Employee> employeeQueryWrapper = new LambdaQueryWrapper<>();
        employeeQueryWrapper.in(Employee::getBusinessId,ids);
        List<Employee> employeeList = employeeService.list(employeeQueryWrapper);
        if (employeeList.size() > 0) {
            throw new CustomException("删除失败!尚有关联员工");
        }

        LambdaQueryWrapper<Business> businessLambdaQueryWrapper = new LambdaQueryWrapper<>();
        businessLambdaQueryWrapper.in(Business::getId,ids);
        List<Business> businessList = list(businessLambdaQueryWrapper);
        businessList.forEach((Business business)->{
            business.setIsDeleted(1);
            updateById(business);
        });

    }

    @Override
    public int getNumber(Long businessId) {
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getBusinessId,businessId);
        ordersLambdaQueryWrapper.between(Orders::getCreateTime,
                LocalDateTime.now().withHour(0).withMinute(0).withSecond(0),
                LocalDateTime.now().withHour(23).withMinute(59).withSecond(59));
        int count = ordersService.count(ordersLambdaQueryWrapper);
        return count+1;
    }

    @Override
    public String getName(Long businessId) {
        Business business = getById(businessId);
        return business.getName();
    }
}
