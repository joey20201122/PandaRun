package com.joey.yx.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joey.yx.mapeer.AddressMapper;
import com.joey.yx.pojo.Address;
import com.joey.yx.service.AddressService;
import org.springframework.stereotype.Service;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/4/5 10:49
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {
}
