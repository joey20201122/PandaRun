// 查询列表接口
const getBusinessPage = (params) => {
  return $axios({
    url: '/business/page',
    method: 'get',
    params
  })
}

// 查自己家的商户
const getBusinessById = (params) => {
  return $axios({
    url: '/business',
    method: 'get',
    params
  })
}

// 编辑页面反查详情接口
const queryBusinessById = (id) => {
  return $axios({
    url: `/business/${id}`,
    method: 'get'
  })
}

// 删除当前列的接口
const deleBusiness = (ids) => {
  return $axios({
    url: '/business',
    method: 'delete',
    params: { ids }
  })
}

// 修改接口
const editBusiness = (params) => {
  return $axios({
    url: '/business',
    method: 'put',
    data: { ...params }
  })
}

// 新增接口
const addBusiness = (params) => {
  return $axios({
    url: '/business',
    method: 'post',
    data: { ...params }
  })
}

