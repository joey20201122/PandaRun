package com.joey.yx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.joey.yx.common.R;
import com.joey.yx.dto.DishDto;
import com.joey.yx.pojo.Dish;
import com.joey.yx.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 86180
 * @version 1.0
 * @description 菜品管理
 * @date 2023/4/3 23:11
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品和菜品口味
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page , int pageSize , String name ,Long businessId){
        return R.success(dishService.pageDish(page, pageSize, name ,businessId));
    }


    /**
     * 根据菜品id获取菜品和口味信息,回显数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable("id") Long id) {
        return R.success(dishService.getDtoById(id));

    }

    /**
     * 修改更新
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("");
    }

    /**
     * 菜品数据删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] ids){
        dishService.deleteWithFlavor(ids);
        return R.success("");
    }

    /**
     * 售卖状态修改
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable int status, Long[] ids){
        List<Dish> list = dishService.listByIds(Arrays.asList(ids));
        list = list.stream().map((Dish dish)->{
            dish.setStatus(status);
            return dish;
        }).collect(Collectors.toList());
        dishService.updateBatchById(list);
        return R.success("");
    }

    @GetMapping("/list")
    public R<List> list (Dish dish){
        List list = dishService.listWithFlavor(dish);

        return R.success(list);
    }
}
