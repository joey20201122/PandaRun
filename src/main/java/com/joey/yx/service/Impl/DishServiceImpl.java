package com.joey.yx.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joey.yx.common.BaseContext;
import com.joey.yx.dto.DishDto;
import com.joey.yx.mapeer.DishMapper;
import com.joey.yx.pojo.Dish;
import com.joey.yx.pojo.DishFlavor;
import com.joey.yx.pojo.DishMonthsales;
import com.joey.yx.service.BusinessService;
import com.joey.yx.service.DishFlavorService;
import com.joey.yx.service.DishMonthsalesService;
import com.joey.yx.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/4/3 22:51
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private BusinessService businessService;
    @Autowired
    private DishMonthsalesService dishMonthsalesService;


    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {


        save(dishDto);
        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor dishFlavor : flavors) {
            dishFlavor.setDishId(dishId);
            dishFlavorService.save(dishFlavor);
        }

        DishMonthsales dishMonthsales = new DishMonthsales();
        dishMonthsales.setBusinessId(dishDto.getBusinessId());
        dishMonthsales.setDishId(dishId);
        dishMonthsales.setDishName(dishDto.getName());
        dishMonthsales.setSale(0);
        dishMonthsales.setPrice(dishDto.getPrice());
        dishMonthsalesService.save(dishMonthsales);


    }

    //分页获取
    @Override
    public Page pageDish(int page, int pageSize, String name ,Long businessId) {
        //声明dish分页器
        Page dishPage = new Page(page, pageSize);
        //设置查询条件并查询
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper();
        lqw.eq(Dish::getIsDeleted,0);
        lqw.eq(Dish::getBusinessId,businessId);
        lqw.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        lqw.orderByDesc(Dish::getSales);
        page(dishPage,lqw);

        return dishPage;
    }

    @Override
    public DishDto getDtoById(Long id) {
        //获取dish，声明dishDto，并赋值
        Dish dish = getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        //获取口味信息并赋值
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<DishFlavor>();
        lqw.eq(DishFlavor::getDishId,dishDto.getId());
        List<DishFlavor> flavors = dishFlavorService.list(lqw);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish，获取dish的id
        updateById(dishDto);
        Long dishId = dishDto.getId();
        //删除原本的flavors
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<DishFlavor>();
        lqw.eq(DishFlavor::getDishId,dishId);
        dishFlavorService.remove(lqw);
        //新增修改后的flavors
        List<DishFlavor> flavors = dishDto.getFlavors().stream().map((dishFlavor -> {
            dishFlavor.setDishId(dishId);
            return dishFlavor;
        })).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void deleteWithFlavor(Long[] ids) {
        for (Long id : ids) {
            //删除flavor表
            LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<DishFlavor>();
            lqw.eq(DishFlavor::getDishId,id);
            dishFlavorService.remove(lqw);
            //逻辑删除dish表
            Dish dish = getById(id);
            dish.setIsDeleted(1);
            updateById(dish);
        }

    }

    @Override
    public List listWithFlavor(Dish dish) {
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(dish.getBusinessId() != null,Dish::getBusinessId,dish.getBusinessId());
        lqw.like(dish.getName()!=null,Dish::getName,dish.getName());
        lqw.eq(Dish::getIsDeleted,0);
        lqw.eq(Dish::getStatus,1);
        lqw.orderByDesc(Dish::getSales);
        lqw.orderByAsc(Dish::getCreateTime);
        List<Dish> list = list(lqw);

        List<DishDto> dishDtoList = list.stream().map((Dish d)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(d, dishDto);
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId,d.getId());
            List<DishFlavor> dishFlavors = dishFlavorService.list(wrapper);
            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).collect(Collectors.toList());

        return dishDtoList;
    }

    @Override
    public List getCollectionDishDto() {
        Long userId = BaseContext.getCurrentId();

        return null;
    }


}
