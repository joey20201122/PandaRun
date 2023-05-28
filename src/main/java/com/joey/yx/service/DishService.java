package com.joey.yx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.joey.yx.dto.DishDto;
import com.joey.yx.pojo.Dish;

import java.util.List;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/4/3 22:50
 */
public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);

    public Page pageDish(int page , int pageSize , String name ,Long businessId);

    public DishDto getDtoById(Long id);

    public void updateWithFlavor(DishDto dishDto);

    public void deleteWithFlavor(Long[] ids);

    public List listWithFlavor(Dish dish);
}
