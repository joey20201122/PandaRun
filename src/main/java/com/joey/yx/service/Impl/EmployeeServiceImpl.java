package com.joey.yx.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joey.yx.common.CustomException;
import com.joey.yx.common.R;
import com.joey.yx.mapeer.EmployeeMapper;
import com.joey.yx.pojo.Employee;
import com.joey.yx.service.BusinessService;
import com.joey.yx.service.EmployeeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

/**
 * @author joey
 * @version 1.0
 * @description TODO
 * @date 2023/3/31 22:13
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{

    @Autowired
    private BusinessService businessService;

    @Override
    public R<String> saveEmployee(Employee employee) {

        //设置初始密码123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //设置商户名称
        employee.setBusinessName(businessService.getBusinessName(employee.getBusinessId()));
        //调用service保存
        boolean save = save(employee);
        if (save) {
            return R.success("新增员工成功!");
        } else {
            return R.error("添加失败!");
        }
    }

    @Override
    public List<Employee> getEmployeesByBusinessId(Long businessId) {
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        employeeLambdaQueryWrapper.eq(Employee::getBusinessId,businessId);
        employeeLambdaQueryWrapper.orderByDesc(Employee::getType);
        employeeLambdaQueryWrapper.orderByAsc(Employee::getCreateTime);
        return list(employeeLambdaQueryWrapper);
    }

    /***
     * @description 获取一个商户所有员工和经营方所有员工
     * @param page
     * @param pageSize
     * @param businessId
     * @param employeeName
     * @return com.joey.yx.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     * @author 86180
     * @date  16:50
    */
    @Override
    public R<Page> getEmployeesByType(int page , int pageSize,Long businessId, String employeeName) {
        //健壮性
        if (businessId == null){
            throw new CustomException("什么玩意儿");
        }
        //声明分页器对象
        Page<Employee> employeePage = new Page<>(page,pageSize);
        //声明查询条件，排序规则
        //查询businessID为传入参数或为1的员工
        LambdaQueryWrapper<Employee> employeeQueryWrapper = new LambdaQueryWrapper<>();
        employeeQueryWrapper.like(StringUtils.isNotEmpty(employeeName),Employee::getName,employeeName);
        employeeQueryWrapper.eq(Employee::getIsDeleted,0);
        employeeQueryWrapper.eq(Employee::getBusinessId,1L)
                //或者businessId为本商户id
                .or()
                .like(StringUtils.isNotEmpty(employeeName),Employee::getName,employeeName)
                .eq(Employee::getIsDeleted,0)
                .eq(Employee::getBusinessId,businessId);
//        employeeQueryWrapper.groupBy(Employee::getBusinessId);
        //排序规则
        employeeQueryWrapper.orderByDesc(Employee::getBusinessId);
        employeeQueryWrapper.orderByDesc(Employee::getType);
        employeeQueryWrapper.orderByAsc(Employee::getCreateTime);
        //分页查询
        page(employeePage,employeeQueryWrapper);

        return R.success(employeePage);
    }




}
