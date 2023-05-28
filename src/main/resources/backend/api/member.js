function getMemberList (params) {
  return $axios({
    url: '/employee/page',
    method: 'get',
    params
  })
}

function getMemberListPage (params) {
  return $axios({
    url: '/employee/pageType',
    method: 'get',
    params
  })
}

// 修改---启用禁用接口
function enableOrDisableEmployee (params) {
  return $axios({
    url: '/employee',
    method: 'put',
    data: { ...params }
  })
}

// 新增---添加员工
function addEmployee (params) {
  return $axios({
    url: '/employee',
    method: 'post',
    data: { ...params }
  })
}

// 修改---添加员工
function editEmployee (params) {
  return $axios({
    url: '/employee',
    method: 'put',
    data: { ...params }
  })
}

// 修改页面反查详情接口
function queryEmployeeById (id) {
  return $axios({
    url: `/employee/${id}`,
    method: 'get'
  })
}

// 删除员工
function deleteEmployeeById (id) {
  return $axios({
    url: `/employee/${id}`,
    method: 'delete'
  })
}

// 获取商户名列表
function getBusinessNameList () {
  return $axios({
    url: `/business/list`,
    method: 'get'
  })
}

