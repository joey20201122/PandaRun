package com.joey.yx.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joey.yx.common.BaseContext;
import com.joey.yx.common.CustomException;
import com.joey.yx.dto.OrdersDto;
import com.joey.yx.mapeer.OrdersMapper;
import com.joey.yx.pojo.*;
import com.joey.yx.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/4/10 11:12
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    ShoppingCartService shoppingCartService;
    @Autowired
    OrderDetailService orderDetailService;
    @Autowired
    AddressService addressService;
    @Autowired
    UserService userService;
    @Autowired
    BusinessService businessService;
    @Autowired
    DishService dishService;
    @Autowired
    DaySalesService daySalesService;

    /***
     * @description 订单提交
     * @param orders controller接口调用此方法传入参数封装为orders类
     * @return Long 订单号
     * @author 86180
     * @date  2023-04-10
    */
    @Override
    @Transactional()
    public Long submit(Orders orders) {
        //获取存储在线程中的当前用户ID
        Long currentId = BaseContext.getCurrentId();
        //构建购物车对象查询器，按照用户Id获取该用户的购物车列表
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        if (shoppingCartList == null || shoppingCartList.size() == 0){
            //购物车为空，抛出异常
            throw new CustomException("下单失败!购物车为空");
        }

        //生成订单id并填入
        long orderId = IdWorker.getId();
        orders.setId(orderId);
        //获取购物车中第一个菜品的商户ID属性
        Long businessId = dishService.getById(shoppingCartList.get(0).getDishId()).getBusinessId();
        //向orders封装商户信息 设置商户ID、商户名、订单号
        orders.setBusinessId(businessId);
        orders.setBusinessName(businessService.getName(businessId));
        orders.setNumber(businessService.getNumber(businessId));

        //设orders的制作时长和总金额为0 以便遍历时累加
        orders.setMakeTime(0);
        orders.setAmount(0F);
        //遍历shoppingCartList 对每一个购物车对象：创建订单明细对象，向订单明细对象封装orderId、菜品数量等属性
        //遍历结束后将 新建的所有订单明细对象 收集到列表 orderDetailList
        List<OrderDetail> orderDetailList= shoppingCartList.stream()
                .map((ShoppingCart shoppingCart)->{
            //对每一个 购物车对象 新建订单明细对象 封装属性
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            orderDetail.setDishId(shoppingCart.getDishId());
            orderDetail.setName(shoppingCart.getName());
            orderDetail.setImage(shoppingCart.getImage());
            orderDetail.setPrice(shoppingCart.getPrice());
            orderDetail.setAmount(shoppingCart.getAmount());
            orderDetail.setBusinessId(businessId);
            //制作时长++
            orders.setMakeTime(orders.getMakeTime()+(dishService.
                    getById(shoppingCart.getDishId())
                    .getTaketime()*shoppingCart.getNumber()));
            //总金额++
            orders.setAmount(orders.getAmount()+shoppingCart.getAmount());
            return orderDetail;
            //收集到列表orderDetailList
        }).collect(Collectors.toList());
        //向订单明细表插入多条数据orderDetailList
        orderDetailService.saveBatch(orderDetailList);

        //向order封装必要基本数据
        //设置订单状态 待付款
        orders.setStatus(0);
        //设置预期完成时间=预定起做时间+制作所需时间
        orders.setExpectedTime(orders.getBookTime().plusSeconds(orders.getMakeTime()));
        //设置预期等待时间=预期完成时间-现在时间
        Duration duration =  Duration.between(LocalDateTime.now(),orders.getExpectedTime());
        orders.setAwaitTime((int) duration.getSeconds());

        //添加用户信息:用户ID、用户手机号、用户名
        orders.setUserId(currentId);
        User user = userService.getById(currentId);
        orders.setPhone(user.getPhone());
        orders.setUserName(user.getName());

        //若为外卖 则添加地址信息
        if (orders.getType()==1 && orders.getAddressId()!=null && orders.getAddressId()!=0L){
            //获取地址信息
            Address address = addressService.getById(orders.getAddressId());
            if (address == null){
                throw new CustomException("下单失败!用户地址信息有误");
            }
            //向orders封装地址信息、收货人、收货人手机号
            orders.setAddress(address.getDetail());
            orders.setConsignee(address.getConsignee());
            orders.setPhone(address.getPhone());
            //设置打包费0.5元
            orders.setServiceCharge(0.5F);
        }

        //向订单表插入数据
        save(orders);
        //清空购物车
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        //返回orders对象ID
        return orderId;
    }

    @Override
    public Page userPageWithDto(int page, int pageSize, Long userId) {
        Long currentId = BaseContext.getCurrentId();

        //声明orders分页器
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        //获取当前用户所有订单
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getUserId,currentId);
        ordersLambdaQueryWrapper.orderByDesc(Orders::getCreateTime);
        //分页查询orders
        page(ordersPage,ordersLambdaQueryWrapper);
        //声明dto分页器
        Page<OrdersDto> ordersDtoPage = new Page<>(page, pageSize);
        //dto分页器拷贝order分页器
        BeanUtils.copyProperties(ordersPage, ordersDtoPage);
        //重新封装records，更换为dto
        List<OrdersDto> ordersDtos = ordersDtoPage.getRecords().stream().map((Orders order) -> {
            //将details封装如dto
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(order, ordersDto);
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId, order.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(orderDetailLambdaQueryWrapper);
            ordersDto.setOrderDetails(orderDetails);
            return ordersDto;
        }).collect(Collectors.toList());
        //设置details
        ordersDtoPage.setRecords(ordersDtos);

        return ordersDtoPage;
    }

    @Override
    public Page pageWithDto(int page, int pageSize, Long businessId) {
        //初始化OrderPage
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //条件
        ordersLambdaQueryWrapper.eq(Orders::getBusinessId,businessId);
        ordersLambdaQueryWrapper.eq(Orders::getStatus,1);
        ordersLambdaQueryWrapper.orderByAsc(Orders::getExpectedTime);
        //分页
        page(ordersPage,ordersLambdaQueryWrapper);

        return packageDtoPage(ordersPage);

    }

    @Override
    @Transactional
    public void pay(Long orderId) {
        Orders orders = getById(orderId);
        //修改信息
        orders.setPayTime(LocalDateTime.now());


        orders.setStatus(1);
        updateById(orders);
        //保存daysales信息
        LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId,orders.getId());
        List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailLambdaQueryWrapper);
        orderDetailList.stream().forEach((OrderDetail orderDetail)->{
            LambdaQueryWrapper<DaySales> daySalesLambdaQueryWrapper = new LambdaQueryWrapper<>();
            daySalesLambdaQueryWrapper.eq(DaySales::getDishId,orderDetail.getDishId());
            daySalesLambdaQueryWrapper.eq(DaySales::getDate, LocalDate.now());
            DaySales daySales = daySalesService.getOne(daySalesLambdaQueryWrapper);
            if (daySales == null){
                DaySales sales = new DaySales();
                sales.setDishId(orderDetail.getDishId());
                sales.setBusinessId(orderDetail.getBusinessId());
                sales.setPrice(orderDetail.getPrice());
                sales.setSale(orderDetail.getNumber());
                sales.setDate(LocalDate.now());
                daySalesService.save(sales);
            } else {
                daySales.setSale(daySales.getSale()+orderDetail.getNumber());
                daySalesService.updateById(daySales);
            }

        });
    }

    @Override
    public Integer getWaitTime(Long businessId) {
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getBusinessId,businessId);
        ordersLambdaQueryWrapper.eq(Orders::getStatus,1);
        List<Orders> ordersList = list(ordersLambdaQueryWrapper);
        Integer waitTime = 0;
        for (Orders orders : ordersList) {
            if (orders.getAwaitTime()>waitTime){
                waitTime = orders.getAwaitTime();
            }
        }
        return waitTime;
    }

    @Override
    public void makeDown(Long orderId) {
        Orders orders = getById(orderId);

        if (orders.getType() == 0){
            orders.setStatus(2);
            updateById(orders);
        } else {
            orders.setStatus(3);
            updateById(orders);
        }
    }

    @Override
    @Transactional
    public void delivery(Long orderId) {
        Orders orders = getById(orderId);
        if (orders.getType() != 1){
            throw new CustomException("未知错误!此单并非外卖!");
        }
        orders.setStatus(4);
        updateById(orders);
    }

    @Override
    public void finish(Long orderId) {
        Orders orders = getById(orderId);
        orders.setStatus(5);
        orders.setIndeedTime(LocalDateTime.now());
        updateById(orders);
    }

    @Override
    public void cancel(Long orderId) {
        //删除detail
        LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId,orderId);
        orderDetailService.remove(orderDetailLambdaQueryWrapper);
        //删除order
        removeById(orderId);
    }

    @Override
    public Page getDeliveryPage(int page, int pageSize) {
        Page orderPage = new Page(page, pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getStatus,3);
        ordersLambdaQueryWrapper.orderByAsc(Orders::getExpectedTime);

        return page(orderPage,ordersLambdaQueryWrapper);
    }

    @Override
    public void againOrder(Long orderId) {
        LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId,orderId);
        List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailLambdaQueryWrapper);

        Orders orders = getById(orderId);

        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map((OrderDetail orderDetail) -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(orders.getUserId());
            shoppingCart.setDishId(orderDetail.getDishId());
            shoppingCart.setName(orderDetail.getName());
            shoppingCart.setImage(orderDetail.getImage());
            shoppingCart.setDishFlavor(orderDetail.getDishFlavor());
            shoppingCart.setNumber(orderDetail.getNumber());
            shoppingCart.setPrice(orderDetail.getPrice());
            shoppingCart.setAmount(orderDetail.getAmount());
            shoppingCart.setServiceCharge(orderDetail.getServiceCharge());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());

        shoppingCartService.saveBatch(shoppingCartList);


    }

    @Override
    public Integer getAwaitTime(Long orderId) {

        return getById(orderId).getAwaitTime();
    }

    @Override
    public OrdersDto getDtoById(Long orderId) {
        Orders orders = getById(orderId);
        if (orders == null) {
            throw new CustomException("请求错误!无对应订单");
        }
        OrdersDto dto = new OrdersDto();

        BeanUtils.copyProperties(orders, dto);

        LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId,orderId);
        List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailLambdaQueryWrapper);

        dto.setOrderDetails(orderDetailList);

        return dto;
    }

    @Override
    public Page getDtoPaegAll(int page, int pageSize, Long number, String beginTime, String endTime) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(number!=null,Orders::getId,number);
        ordersLambdaQueryWrapper.gt(beginTime!=null,Orders::getCreateTime,beginTime);
        ordersLambdaQueryWrapper.lt(endTime!=null,Orders::getCreateTime,endTime);
        ordersLambdaQueryWrapper.orderByDesc(Orders::getCreateTime);
        page(ordersPage,ordersLambdaQueryWrapper);

        return packageDtoPage(ordersPage);
    }

    @Override
    public Page getDtoPageByBusinessId(int page, int pageSize, Long number, String beginTime, String endTime, Long businessId) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();

        ordersLambdaQueryWrapper.eq(Orders::getBusinessId,businessId);
        ordersLambdaQueryWrapper.eq(number!=null,Orders::getId,number);
        ordersLambdaQueryWrapper.gt(beginTime!=null,Orders::getCreateTime,beginTime);
        ordersLambdaQueryWrapper.lt(endTime!=null,Orders::getCreateTime,endTime);
        ordersLambdaQueryWrapper.orderByDesc(Orders::getCreateTime);

        page(ordersPage,ordersLambdaQueryWrapper);

        return packageDtoPage(ordersPage);
    }

    private Page packageDtoPage(Page<Orders> ordersPage){
        //声明dto分页器
        Page<OrdersDto> ordersDtoPage = new Page<OrdersDto>(ordersPage.getCurrent(), ordersPage.getSize());
        //dto分页器拷贝order分页器
        BeanUtils.copyProperties(ordersPage, ordersDtoPage);
        //重新封装records，更换为dto
        List<OrdersDto> ordersDtos = ordersDtoPage.getRecords().stream().map((Orders order) -> {
            //将details封装如dto
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(order, ordersDto);
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId, order.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(orderDetailLambdaQueryWrapper);
            ordersDto.setOrderDetails(orderDetails);
            return ordersDto;
        }).collect(Collectors.toList());
        //设置details
        ordersDtoPage.setRecords(ordersDtos);

        return ordersDtoPage;
    }
}
