function getUserList (params) {
    return $axios({
        url: '/user/page',
        method: 'get',
        params
    })
}

function getOrderByUserId (params) {
    return $axios({
        url: '/order/page',
        method: 'get',
        params
    })
}

function getOrderAllByUserId (params) {
    return $axios({
        url: '/order/queryByUserId',
        method: 'get',
        params
    })
}