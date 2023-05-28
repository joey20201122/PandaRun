// 获取月销量
function getSalePage (params) {
    return $axios({
        url: '/sale/page',
        method: 'get',
        params
    })
}

// 限定时间段获取商户销量
function getSalePageByDate (params) {
    return $axios({
        url: '/sale/pageByDate',
        method: 'get',
        params
    })
}

// 获取月销量
function getDishSalePageApi (params) {
    return $axios({
        url: '/sale/dishPage',
        method: 'get',
        params
    })
}

// 限定时间段获取商户销量
function getDishSalePageByDateApi (params) {
    return $axios({
        url: '/sale/dishPageByDate',
        method: 'get',
        params
    })
}

// 刷新所有商户营收
function refreshAllApi () {
    return $axios({
        url: '/sale/refresh',
        method: 'put'
    })
}

// 刷新指定商户营收
function refreshByBusinessIdApi (id) {
    return $axios({
        url: `/sale/refresh/${id}`,
        method: 'put'
    })
}

// 刷新用户所见月销量
function refreshViewApi () {
    return $axios({
        url: '/sale/refreshView',
        method: 'put'
    })
}

// 获取商户名称
function getBusinessNameApi (params) {
    return $axios({
        url: '/business/getName',
        method: 'get',
        params
    })
}