package com.joey.yx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.joey.yx.pojo.Business;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/4/5 21:13
 */
public interface BusinessService extends IService<Business> {

    public int getNeedTime (Long businessId);

    public String getBusinessName (Long businessId);

    void saveBusiness(Business business);

    void deleteBusiness(Long ids);

    int getNumber(Long businessId);

    String getName(Long businessId);
}
