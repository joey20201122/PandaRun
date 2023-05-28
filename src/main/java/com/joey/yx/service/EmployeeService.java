package com.joey.yx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.joey.yx.common.R;
import com.joey.yx.pojo.Employee;

import java.util.List;

/**
 * @author joey
 * @version 1.0
 * @description TODO
 * @date 2023/3/31 22:12
 */
public interface EmployeeService extends IService<Employee> {


    R<String> saveEmployee(Employee employee);

    List<Employee> getEmployeesByBusinessId(Long businessId);

    R<Page> getEmployeesByType(int page , int pageSize,Long businessId, String employeeName);





}
