package com.joey.yx.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joey.yx.mapeer.OrderDetailMapper;
import com.joey.yx.pojo.OrderDetail;
import com.joey.yx.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/4/10 11:13
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
