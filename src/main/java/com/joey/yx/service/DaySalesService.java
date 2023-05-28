package com.joey.yx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.joey.yx.pojo.BusinessMonthsales;
import com.joey.yx.pojo.DaySales;
import com.joey.yx.pojo.DishMonthsales;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/4/19 7:42
 */
public interface DaySalesService extends IService<DaySales> {

    void refreshAll();

    void refreshView();

    void refreshByBusinessId(Long businessId);

    Page<BusinessMonthsales> getPage(int page, int pageSize, String businessName);

    Page<BusinessMonthsales> getPageByDate(int page, int pageSize, String businessName, int dateZone);

    Page<DishMonthsales> getDishSalePageByBusinessId(int page, int pageSize, Long businessId);

    Page<DishMonthsales> getDishSalePageByBusinessIdAndDate(int page, int pageSize, Long businessId, int dateZone);
}
