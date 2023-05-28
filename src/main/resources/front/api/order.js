//提交订单
function  addOrderApi(data){
    return $axios({
        'url': '/order/submit',
        'method': 'post',
        data
      })
}

//查询所有订单
function orderListApi() {
  return $axios({
    'url': '/order/list',
    'method': 'get',
  })
}

//分页查询订单
function orderPagingApi(data) {
  return $axios({
      'url': '/order/userPage',
      'method': 'get',
      params:{...data}
  })
}

//再来一单
function orderAgainApi(id) {
  return $axios({
      url: `/order/again/${id}`,
      method: 'post'
  })
}

//取消订单
function cancelOrderApi(id) {
    return $axios({
        url: `/order/cancel/${id}`,
        method: 'delete'
    })
}

//付款
function payOrderApi( id ) {
    return $axios({
        url: `/order/pay/${id}`,
        method: 'put'
    })
}

//查询等待时间
function getWaitTimeApi( id ) {
    return $axios({
        url: `/order/getWaitTime/${id}`,
        method: 'get'
    })
}

//付款
function finishOrderApi( id ) {
    return $axios({
        url: `/order/finish/${id}`,
        method: 'put'
    })
}

//改名字
function  changeUserNameApi(params){
    return $axios({
        url: '/user/changeName',
        method: 'put',
        params
    })
}


