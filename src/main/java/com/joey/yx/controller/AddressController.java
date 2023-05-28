package com.joey.yx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.joey.yx.common.BaseContext;
import com.joey.yx.common.R;
import com.joey.yx.pojo.Address;
import com.joey.yx.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 86180
 * @version 1.0
 * @description 收货地址操作
 * @date 2023/4/5 10:50
 */
@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public R<List<Address>> list(Address address) {
        address.setUserId(BaseContext.getCurrentId());
        //条件构造器
        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != address.getUserId(), Address::getUserId, address.getUserId());
        queryWrapper.orderByDesc(Address::getCreateTime);
        //SQL:select * from address_book where user_id = ? order by create_time desc

        return R.success(addressService.list(queryWrapper));
    }

    /**
     * 新增
     */
    @PostMapping
    public R<Address> save(@RequestBody Address address) {
        address.setUserId(BaseContext.getCurrentId());
        //初始化数据 TODO 优化阶段删掉
        address.setBuilding("七号楼");
        address.setDormitory("335");
        address.setDetail(address.getBuilding()+":"+address.getDormitory()+"号");

        addressService.save(address);
        return R.success(address);
    }

    /**
     * 修改
     */
    @PutMapping
    public R<Address> update(@RequestBody Address address) {
        address.setUserId(BaseContext.getCurrentId());
        address.setDetail(address.getBuilding()+":"+address.getDormitory()+"号");
        addressService.updateById(address);
        return R.success(address);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("default")
    public R<Address> setDefault(@RequestBody Address address) {
        LambdaUpdateWrapper<Address> wrapper = new LambdaUpdateWrapper<>();
        //将user下所有地址设为0
        wrapper.eq(Address::getUserId, BaseContext.getCurrentId());
        wrapper.set(Address::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressService.update(wrapper);

        //将传进来的地址设为1
        address.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressService.updateById(address);
        return R.success(address);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        Address address = addressService.getById(id);
        if (address != null) {
            return R.success(address);
        } else {
            return R.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public R<Address> getDefault() {
        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Address::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(Address::getIsDefault, 1);
        //SQL:select * from address_book where user_id = ? and is_default = 1

        Address address = addressService.getOne(queryWrapper);

        if (null == address) {
            return R.error("暂无默认地址");
        } else {
            return R.success(address);
        }
    }

}
