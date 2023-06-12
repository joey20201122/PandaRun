package com.joey.yx.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joey.yx.common.CustomException;
import com.joey.yx.mapeer.DaySalesMapper;
import com.joey.yx.pojo.*;
import com.joey.yx.service.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/4/19 7:43
 */
@Service
public class DaySalesServiceImpl extends ServiceImpl<DaySalesMapper, DaySales> implements DaySalesService {

    @Autowired
    private DishMonthsalesService dishMonthsalesService;
    @Autowired
    private BusinessMonthsalesService businessMonthsalesService;
    @Autowired
    private DishService dishService;
    @Autowired
    private BusinessService businessService;


    /***
     * @description 刷新所有商户的销售额信息
     *
     * @return void
     * @author 86180
     * @date  11:32
    */
    //TODO优化语句，少次大数量强于多次小数量
    @Override
    @Transactional
    public void refreshAll() {
        //将所有菜品月销量清零
        List<DishMonthsales> dishMonthsalesList = dishMonthsalesService.list();
        dishMonthsalesList.forEach((DishMonthsales dishMonthsale) -> {
            dishMonthsale.setSale(0);
        });
        dishMonthsalesService.updateBatchById(dishMonthsalesList);
        //找到一个月内的daysales条目
        LambdaQueryWrapper<DaySales> daySalesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        daySalesLambdaQueryWrapper.between(DaySales::getDate, LocalDate.now().minusMonths(1L),LocalDate.now());
        List<DaySales> daySalesList = list(daySalesLambdaQueryWrapper);
        //每条daysales对应与菜品月销加上daysales的销售量
        daySalesList.forEach((DaySales daySales)->{
            LambdaQueryWrapper<DishMonthsales> dishMonthsalesLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishMonthsalesLambdaQueryWrapper.eq(DishMonthsales::getDishId,daySales.getDishId());
            DishMonthsales dishMonthsales = dishMonthsalesService.getOne(dishMonthsalesLambdaQueryWrapper);
            dishMonthsales.setSale(dishMonthsales.getSale()+daySales.getSale());
            dishMonthsalesService.updateById(dishMonthsales);
        });
        //将所有商户月销量清零
        List<BusinessMonthsales> businessMonthsalesList = businessMonthsalesService.list();
        businessMonthsalesList.forEach((BusinessMonthsales businessMonthsales) -> {
            businessMonthsales.setSale(0);
            businessMonthsales.setTurnover(0F);
        });
        businessMonthsalesService.updateBatchById(businessMonthsalesList);
        //每条dishMonthsales与对应与商户月销量加上dishMonthsales的销售量
        List<DishMonthsales> dishMonthsalesList2 = dishMonthsalesService.list();
        dishMonthsalesList2.forEach((DishMonthsales dishMonthsales)->{
            LambdaQueryWrapper<BusinessMonthsales> businessMonthsalesLambdaQueryWrapper = new LambdaQueryWrapper<>();
            businessMonthsalesLambdaQueryWrapper.eq(BusinessMonthsales::getBusinessId,dishMonthsales.getBusinessId());
            BusinessMonthsales businessMonthsales = businessMonthsalesService.getOne(businessMonthsalesLambdaQueryWrapper);
            businessMonthsales.setSale(businessMonthsales.getSale()+dishMonthsales.getSale());
            businessMonthsales.setTurnover(businessMonthsales.getTurnover()+dishMonthsales.getSale()*dishMonthsales.getPrice());
            businessMonthsalesService.updateById(businessMonthsales);
        });
    }

    @Override
    @Transactional
    public void refreshView() {
        //1.获取BusinessMonthsales
        List<BusinessMonthsales> businessMonthsalesList = businessMonthsalesService.list();
        //2.获取Business
        LambdaQueryWrapper<Business> businessLambdaQueryWrapper = new LambdaQueryWrapper<>();
        businessLambdaQueryWrapper.eq(Business::getIsDeleted,0);
        businessLambdaQueryWrapper.eq(Business::getStatus,1);
        businessLambdaQueryWrapper.ne(Business::getId,1);
        List<Business> businessList = businessService.list(businessLambdaQueryWrapper);
        //3.新建map,存入需要 查询的Business
        HashMap<Long, Business> map = new HashMap<>();
        for (Business business : businessList) {
            map.put(business.getId(),business);
        }
        //4.对获取BusinessMonthsales遍历，若符合则更新销量
        for (BusinessMonthsales businessMonthsales : businessMonthsalesList) {
            if (map.containsKey(businessMonthsales.getBusinessId())){
                Business business = map.get(businessMonthsales.getBusinessId());
                business.setSales(businessMonthsales.getSale());
                map.put(businessMonthsales.getBusinessId(),business);
            }
        }
        //更新business表
        businessList.clear();
        for (Map.Entry<Long, Business> entry : map.entrySet()) {
            businessList.add(entry.getValue());
        }
        businessService.updateBatchById(businessList);
        //5.获取dishMonthsales
        List<DishMonthsales> dishMonthsalesList = dishMonthsalesService.list();
        //6.获取dish
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getIsDeleted,0);
        dishLambdaQueryWrapper.eq(Dish::getStatus,1);
        List<Dish> dishList = dishService.list(dishLambdaQueryWrapper);
        //7.新建map存入需要的Dish
        HashMap<Long,Dish> dishMap = new HashMap<>();
        for (Dish dish : dishList) {
            dishMap.put(dish.getId(),dish);
        }
        //对dishMonthsalesList遍历，若符合则更新销量
        for (DishMonthsales dishMonthsales : dishMonthsalesList) {
            if (dishMap.containsKey(dishMonthsales.getDishId())){
                Dish dish = dishMap.get(dishMonthsales.getDishId());
                dish.setSales(dishMonthsales.getSale());
                dishMap.put(dishMonthsales.getDishId(),dish);
            }
        }
        //更新dish表
        dishList.clear();
        for (Map.Entry<Long, Dish> entry : dishMap.entrySet()) {
            dishList.add(entry.getValue());
        }
        dishService.updateBatchById(dishList);

    }



    //更新一个商户的营业额信息
    @Override
    @Transactional
    public void refreshByBusinessId(Long businessId) {
        //将此店菜品月销量清零
        LambdaQueryWrapper<DishMonthsales> dishMonthsalesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishMonthsalesLambdaQueryWrapper.eq(DishMonthsales::getBusinessId,businessId);
        List<DishMonthsales> dishMonthsalesList = dishMonthsalesService.list(dishMonthsalesLambdaQueryWrapper);
        dishMonthsalesList.forEach((DishMonthsales dishMonthsales)->{
            dishMonthsales.setSale(0);
        });
        dishMonthsalesService.updateBatchById(dishMonthsalesList);
        //找到本店一个月内的daysales
        LambdaQueryWrapper<DaySales> daySalesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        daySalesLambdaQueryWrapper.eq(DaySales::getBusinessId,businessId);
        daySalesLambdaQueryWrapper.between(DaySales::getDate, LocalDate.now().minusMonths(1L),LocalDate.now());
        List<DaySales> daySalesList = list(daySalesLambdaQueryWrapper);
        //将daysales与对应dishMonthsales数据相加
        daySalesList.forEach((DaySales daySales)->{
            LambdaQueryWrapper<DishMonthsales> dishMonthsalesLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            dishMonthsalesLambdaQueryWrapper1.eq(DishMonthsales::getDishId,daySales.getDishId());
            DishMonthsales dishMonthsales = dishMonthsalesService.getOne(dishMonthsalesLambdaQueryWrapper1);
            dishMonthsales.setSale(dishMonthsales.getSale()+daySales.getSale());
            dishMonthsalesService.updateById(dishMonthsales);
        });
        //将本店月销量清零
        LambdaQueryWrapper<BusinessMonthsales> businessMonthsalesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        businessMonthsalesLambdaQueryWrapper.eq(BusinessMonthsales::getBusinessId,businessId);
        BusinessMonthsales businessMonthsales = businessMonthsalesService.getOne(businessMonthsalesLambdaQueryWrapper);
        businessMonthsales.setSale(0);
        businessMonthsales.setTurnover(0F);
        //遍历对应的dishMonthsales条目以更新对应的BusinessMonthsales数据
        List<DishMonthsales> dishMonthsalesList1 = dishMonthsalesService.list(dishMonthsalesLambdaQueryWrapper);
        dishMonthsalesList1.forEach((DishMonthsales dishMonthsales)->{
            businessMonthsales.setSale(businessMonthsales.getSale()+dishMonthsales.getSale());
            businessMonthsales.setTurnover(businessMonthsales.getTurnover()+dishMonthsales.getSale()*dishMonthsales.getPrice());
        });
        //更新businessMonthsales表
        businessMonthsalesService.updateById(businessMonthsales);

    }

    @Override
    public Page<BusinessMonthsales> getPage(int page, int pageSize, String businessName) {
        Page<BusinessMonthsales> businessMonthsalesPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<BusinessMonthsales> businessMonthsalesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        businessMonthsalesLambdaQueryWrapper.like(StringUtils.isNotBlank(businessName), BusinessMonthsales::getBusinessName,businessName);
        businessMonthsalesLambdaQueryWrapper.orderByDesc(BusinessMonthsales::getSale);
        businessMonthsalesService.page(businessMonthsalesPage,businessMonthsalesLambdaQueryWrapper);

        return businessMonthsalesPage;
    }

    @Override
    public Page<BusinessMonthsales> getPageByDate(int page, int pageSize, String businessName, int dateZone) {
        //穿错参数则
        if (dateZone == 30){
            return getPage(page,pageSize,businessName);
        }
        //创建map，key：businessId；value：BusinessMonthsales
        HashMap<Long, BusinessMonthsales> map = new HashMap<>();
        //获取businessMonthsalesList，符合要求的条目
        LambdaQueryWrapper<BusinessMonthsales> businessMonthsalesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        businessMonthsalesLambdaQueryWrapper.like(StringUtils.isNotBlank(businessName),BusinessMonthsales::getBusinessName,businessName);
        List<BusinessMonthsales> businessMonthsalesList = businessMonthsalesService.list(businessMonthsalesLambdaQueryWrapper);
        //给每条数据销量清零，并添加入map
        businessMonthsalesList.forEach((BusinessMonthsales b)->{
            b.setSale(0);
            b.setTurnover(0F);
            map.put(b.getBusinessId(),b);
        });
        //获取规定时间段内的DaySales
        LambdaQueryWrapper<DaySales> daySalesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        daySalesLambdaQueryWrapper.between(DaySales::getDate,getDate(dateZone),LocalDate.now());
        List<DaySales> daySalesList = list(daySalesLambdaQueryWrapper);
        //对每个DaySales处理，若为所需商户的数据，则添加数据
        daySalesList.forEach((DaySales daySales)->{
            if (map.containsKey(daySales.getBusinessId())){
                BusinessMonthsales businessMonthsales = map.get(daySales.getBusinessId());
                businessMonthsales.setSale(businessMonthsales.getSale()+daySales.getSale());
                businessMonthsales.setTurnover(businessMonthsales.getTurnover()+daySales.getSale()*daySales.getPrice()*100);
            }
        });
        //businessMonthsalesList数据清零，将map中数据再放回list
        businessMonthsalesList.clear();
        for (Map.Entry<Long, BusinessMonthsales> entry : map.entrySet()) {
            businessMonthsalesList.add(entry.getValue());
        }
        //新建Page对象
        Page<BusinessMonthsales> businessMonthsalesPage = new Page<>(page, pageSize);
        //写入total
        businessMonthsalesPage.setTotal(businessMonthsalesList.size());
        //list排序，按照销量从高到低
        businessMonthsalesList = businessMonthsalesList.stream().sorted(Comparator.comparing(BusinessMonthsales::getSale).reversed())
                .collect(Collectors.toList());
        //截取下(page-1)*pageSize到page*pageSize-1的数据
        int toIndex = page*pageSize-1;
        if (toIndex > businessMonthsalesList.size()-1) {
            toIndex = businessMonthsalesList.size()-1;
            if (toIndex == 0){
                toIndex = 1;
            }
        }
        //Page设置截取后的list作为records
        businessMonthsalesPage.setRecords(businessMonthsalesList.subList((page-1)*pageSize,toIndex));
        //返回Page对象
        return businessMonthsalesPage;
    }

    @Override
    public Page<DishMonthsales> getDishSalePageByBusinessId(int page, int pageSize, Long businessId) {
        if (businessId == null){
            throw new CustomException("非法调用!");
        }
        //新建分页器
        Page<DishMonthsales> dishMonthsalesPage = new Page<>(page, pageSize);
        //获取此商户所有菜品售卖表
        LambdaQueryWrapper<DishMonthsales> dishMonthsalesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishMonthsalesLambdaQueryWrapper.eq(DishMonthsales::getBusinessId,businessId);
        dishMonthsalesLambdaQueryWrapper.orderByDesc(DishMonthsales::getSale);
        dishMonthsalesService.page(dishMonthsalesPage,dishMonthsalesLambdaQueryWrapper);


        return dishMonthsalesPage;
    }

    @Override
    public Page<DishMonthsales> getDishSalePageByBusinessIdAndDate(int page, int pageSize, Long businessId, int dateZone) {
        if (businessId == null){
            throw new CustomException("非法调用!");
        }
        //穿错参数则
        if (dateZone == 30){
            return getDishSalePageByBusinessId(page,pageSize,businessId);
        }
        //找到此商户指定时间段内daysale数据
        LambdaQueryWrapper<DaySales> daySalesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        daySalesLambdaQueryWrapper.eq(DaySales::getBusinessId,businessId);
        daySalesLambdaQueryWrapper.between(DaySales::getDate,getDate(dateZone),LocalDate.now());
        List<DaySales> daySalesList = list(daySalesLambdaQueryWrapper);
        //新建map K:dishId V:dishMonthsale
        HashMap<Long, DishMonthsales> map = new HashMap<>();
        //获取dishMonthsaleList
        LambdaQueryWrapper<DishMonthsales> dishMonthsalesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishMonthsalesLambdaQueryWrapper.eq(DishMonthsales::getBusinessId,businessId);
        List<DishMonthsales> dishMonthsalesList = dishMonthsalesService.list(dishMonthsalesLambdaQueryWrapper);
        //遍历dishMonthsalesList,向map填充初始数据
        for (DishMonthsales dishMonthsales : dishMonthsalesList) {
            dishMonthsales.setSale(0);
            map.put(dishMonthsales.getDishId(),dishMonthsales);
        }
        //遍历daySalesList向map中更新数据
        for (DaySales daySales : daySalesList) {
            if (map.containsKey(daySales.getDishId())){
                DishMonthsales dishMonthsales = map.get(daySales.getDishId());
                dishMonthsales.setSale(dishMonthsales.getSale()+daySales.getSale());
                map.put(daySales.getDishId(),dishMonthsales);
            }
        }
        //新建Page对象
        Page<DishMonthsales> dishMonthsalesPage = new Page<>();
        //dishMonthsalesList清零
        dishMonthsalesList.clear();
        //将map中DishMonthsale数据存入list
        for (Map.Entry<Long, DishMonthsales> entry : map.entrySet()) {
            dishMonthsalesList.add(entry.getValue());
        }
        //设置total
        dishMonthsalesPage.setTotal(dishMonthsalesList.size());
        //list排序，销量从高到低
        dishMonthsalesList = dishMonthsalesList.stream().sorted(Comparator.comparing(DishMonthsales::getSale).reversed())
                .collect(Collectors.toList());
        //截取下(page-1)*pageSize到page*pageSize-1的数据
        int toIndex = page*pageSize-1;
        if (toIndex > dishMonthsalesList.size()-1) {
            toIndex = dishMonthsalesList.size()-1;
            if (toIndex == 0){
                toIndex = 1;
            }
        }
        //Page设置截取后的list作为records
        dishMonthsalesPage.setRecords(dishMonthsalesList.subList((page-1)*pageSize,toIndex));

        return dishMonthsalesPage;
    }

    LocalDate getDate(int dateZone){
        switch (dateZone) {
            case 31:
                return LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);
            case 7:
                return LocalDate.now().minusDays(7);
            case 8:
                return LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue());
            case 90:
                return LocalDate.now().minusMonths(3);
            case 91:
                int monthValue = LocalDate.now().getMonthValue();
                if (monthValue<=3){
                    return LocalDate.now().withMonth(1).withDayOfMonth(1);
                } else if (monthValue <= 6) {
                    return LocalDate.now().withMonth(4).withDayOfMonth(1);
                } else if (monthValue <= 9) {
                    return LocalDate.now().withMonth(7).withDayOfMonth(1);
                } else {
                    return LocalDate.now().withMonth(10).withDayOfMonth(1);
                }
            case 365:
                return LocalDate.now().minusYears(1);
            case 366:
                return LocalDate.now().minusDays(LocalDate.now().getDayOfYear()-1);
            default:
                throw new CustomException("输入错误!");
        }
    }
}
