// 查询列表页接口
const getOrderDetailPage = (params) => {
  return $axios({
    url: '/order/page',
    method: 'get',
    params
  })
}

// 查询列表页接口
const getOrderDetailPageType = (params) => {
  return $axios({
    url: '/order/pageType',
    method: 'get',
    params
  })
}

// getDtoPage处理订单 返回Dto
const getDtoPage = (params) => {
  return $axios({
    url: '/order/handle',
    method: 'get',
    params
  })
}

// 查看接口
const queryOrderDetailById = (id) => {
  return $axios({
    url: `/orderDetail/${id}`,
    method: 'get'
  })
}

// 取消，派送，完成接口
const editOrderDetail = (params) => {
  return $axios({
    url: '/order',
    method: 'put',
    data: { ...params }
  })
}

// 查看接口
const makeDownOrderApi = (id) => {
  return $axios({
    url: `/order/makeDown/${id}`,
    method: 'put'
  })
}

// 查看接口
const getDeliveryPageApi = (params) => {
  return $axios({
    url: '/order/delivery',
    method: 'get',
    params
  })
}

// 查看接口
const deliveryApi = (id) => {
  return $axios({
    url: `/order/delivery/${id}`,
    method: 'put'
  })
}

//根据orderId获取订单dto
const getDtoApi = (id) => {
  return $axios({
    url: `/order/detail/${id}`,
    method: 'get'
  })
}