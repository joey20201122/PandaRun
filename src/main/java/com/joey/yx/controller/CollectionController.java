package com.joey.yx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.joey.yx.common.BaseContext;
import com.joey.yx.common.CustomException;
import com.joey.yx.common.R;
import com.joey.yx.pojo.Collection;
import com.joey.yx.pojo.Dish;
import com.joey.yx.service.CollectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/6/10 16:42
 */
@RestController
@Slf4j
@RequestMapping("/collection")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @PostMapping
    public R<String> save (@RequestBody Dish dish){
        //
        LambdaQueryWrapper<Collection> collectionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectionLambdaQueryWrapper.eq(Collection::getUserId,BaseContext.getCurrentId());
        collectionLambdaQueryWrapper.eq(Collection::getDishId,dish.getId());
        if (collectionService.getOne(collectionLambdaQueryWrapper) != null){
            throw new CustomException("收藏失败!不可重复收藏");
        }
        //
        Collection collection = new Collection();
        collection.setBusinessId(dish.getBusinessId());
        collection.setBusinessName(dish.getBusinessName());
        collection.setDishId(dish.getId());
        collection.setDishName(dish.getName());
        collection.setImage(dish.getImage());
        collection.setPrice(dish.getPrice());
        collection.setUserId(BaseContext.getCurrentId());
        if (collectionService.save(collection)) {
            return R.success("收藏成功！");

        }
        return R.error("收藏失败");
    }

    @DeleteMapping("/{dishId}")
    public R<String> delete (@PathVariable Long dishId){
        //
        LambdaQueryWrapper<Collection> collectionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectionLambdaQueryWrapper.eq(Collection::getUserId,BaseContext.getCurrentId());
        collectionLambdaQueryWrapper.eq(Collection::getDishId,dishId);
        if (collectionService.getOne(collectionLambdaQueryWrapper) == null){
            throw new CustomException("取消收藏失败!李在赣神魔");
        }
        //
        if (collectionService.remove(collectionLambdaQueryWrapper)) {
            return R.success("取消收藏成功！");

        }
        return R.error("取消收藏失败");
    }

    @GetMapping("/list")
    public R<List> list(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Collection> collectionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectionLambdaQueryWrapper.eq(Collection::getUserId,userId);
        collectionLambdaQueryWrapper.orderByDesc(Collection::getCreateTime);
        return R.success(collectionService.list(collectionLambdaQueryWrapper));
    }

}
