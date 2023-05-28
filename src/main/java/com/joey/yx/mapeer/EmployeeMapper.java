package com.joey.yx.mapeer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joey.yx.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author joey
 * @version 1.0
 * @description TODO
 * @date 2023/3/31 22:10
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
