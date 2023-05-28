package com.joey.yx.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joey.yx.common.BaseContext;
import com.joey.yx.mapeer.UserMapper;
import com.joey.yx.pojo.ShoppingCart;
import com.joey.yx.pojo.User;
import com.joey.yx.service.BusinessService;
import com.joey.yx.service.DishService;
import com.joey.yx.service.ShoppingCartService;
import com.joey.yx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/4/4 14:29
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private BusinessService businessService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private DishService dishService;


    @Override
    public Integer getNeedTime() {
        Long currentId = BaseContext.getCurrentId();
        //找到user
        User user = userService.getById(currentId);
        //找到user的购物车
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,user.getId());
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        //需要等待时间
        Integer needTime = 0;
        for (ShoppingCart shoppingCart : shoppingCarts) {
            int i = businessService.getNeedTime(dishService.getById(shoppingCart.getDishId()).getBusinessId());
            if (i > needTime) {
                needTime = i;
            }
        }
        return needTime;
    }

    @Override
    public User changeName(String name) {
        Long currentId = BaseContext.getCurrentId();
        //找到user
        User user = userService.getById(currentId);

        user.setName(name);
        updateById(user);
        return user;
    }

    @Override
    public Integer getMakeTime() {
        Long currentId = BaseContext.getCurrentId();
        //找到user
        User user = userService.getById(currentId);
        //找到user的购物车
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,user.getId());
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        //
        int t = 0;
        for (ShoppingCart shoppingCart : shoppingCarts) {
            t = t +dishService.getById(shoppingCart.getDishId()).getTaketime()*shoppingCart.getNumber();
        }
        return t;
    }
}
