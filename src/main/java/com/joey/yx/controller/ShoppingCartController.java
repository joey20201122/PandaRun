package com.joey.yx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.joey.yx.common.BaseContext;
import com.joey.yx.common.R;
import com.joey.yx.pojo.ShoppingCart;
import com.joey.yx.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/4/5 11:53
 */
@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 获取购物车列表
     * @return
     */
    @GetMapping("/list")
    public R<List> list(){
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(ShoppingCart::getUserId,currentId);
        wrapper.orderByDesc(ShoppingCart::getAmount);

        List<ShoppingCart> shoppingCartList = shoppingCartService.list(wrapper);
        return R.success(shoppingCartList);
    }

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        //设置用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        //若打包则设置服务费
        //
        //查询当前菜品或套餐是否在购物车中
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,currentId);
        wrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        ShoppingCart one = shoppingCartService.getOne(wrapper);
        if(one != null){
            //若已经有了，数量加一即可
            one.setNumber((one.getNumber()+1));
            one.setAmount(one.getAmount()+one.getPrice());
            shoppingCartService.updateById(one);
        } else {
            //若不存在则添加到购物车，数量为一
            shoppingCart.setNumber(1);
            shoppingCart.setAmount(shoppingCart.getPrice());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }

        return R.success(one);
    }

    /**
     * 减少数量
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        //设置用户id
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,currentId);
        wrapper.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId());
        ShoppingCart one = shoppingCartService.getOne(wrapper);
        if (one == null) {
            return R.error("无法再减少了");
        }
        //数量减少，总额减少
        int number = one.getNumber() - 1;
        one.setNumber(number);
        one.setAmount(one.getAmount()-one.getPrice());
        if (number == 0) {
            shoppingCartService.removeById(one.getId());
        }else {
            shoppingCartService.updateById(one);
        }

        return R.success(one);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean (){
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,currentId);
        shoppingCartService.remove(wrapper);
        return R.success("");
    }
}
