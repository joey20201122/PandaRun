package com.joey.yx.dto;

import com.joey.yx.pojo.OrderDetail;
import com.joey.yx.pojo.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    //与之对应的订单明细对象列表
    private List<OrderDetail> orderDetails;
	
}
