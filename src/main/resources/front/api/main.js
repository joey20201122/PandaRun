//获取所有的商户分类
function categoryListApi() {
    return $axios({
      'url': '/business/list',
      'method': 'get',
    })
}

//获取商户下的菜品列表
function dishListApi(data) {
    return $axios({
        'url': '/dish/list',
        'method': 'get',
        params:{...data}
    })
}

//获取购物车内商品的集合
function cartListApi(data) {
    return $axios({
        'url': '/shoppingCart/list',
        'method': 'get',
        params:{...data}
    })
}

//购物车中添加商品
function  addCartApi(data){
    return $axios({
        'url': '/shoppingCart/add',
        'method': 'post',
        data
      })
}

//购物车中减少商品
function  updateCartApi(data){
    return $axios({
        'url': '/shoppingCart/sub',
        'method': 'post',
        data
      })
}

//删除购物车的商品
function clearCartApi() {
    return $axios({
        'url': '/shoppingCart/clean',
        'method': 'delete',
    })
}

//获取做菜时间
function getMakeTimeApi() {
    return $axios({
        'url': '/user/makeTime',
        'method': 'get',
    })
}

//获取收藏列表
function collectionListApi() {
    return $axios({
        'url': '/collection/list',
        'method': 'get',
    })
}

//收藏夹中添加商品
function  addCollectionApi(data){
    return $axios({
        'url': '/collection',
        'method': 'post',
        data
    })
}

//收藏夹中删除商品
function  deleteCollectionApi(id){
    return $axios({
        url: `/collection/${id}`,
        method: 'delete',
    })
}









