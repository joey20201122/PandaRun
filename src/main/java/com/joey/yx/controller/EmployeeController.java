package com.joey.yx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.joey.yx.common.R;
import com.joey.yx.pojo.Employee;
import com.joey.yx.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author joey
 * @version 1.0
 * @description 员工管理
 * @date 2023/3/31 22:14
 */
@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /***
     * @description 员工登录
     *
     * @return com.joey.yx.common.R 通用结果类 返回employee信息
     * @author joey
     * @date  16:21
    */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1.将页面提交的密码进行加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.根据用户名查询表
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername,employee.getUsername());
        Employee realEmployee = employeeService.getOne(lqw);
        //3.若没有此数据，返回错误
        if (realEmployee == null) {
            return R.error("登陆失败!用户名不存在");
        }
        //4.比较密码
        //5.若密码错误，返回错误
        if (!realEmployee.getPassword().equals(password)){
            return R.error("登陆失败!用户名或密码错误");
        }
        //6.查询此账户是否被删除，若删除，返回错误
        if (realEmployee.getIsDeleted()==1){
            return R.error("登陆失败!此账户已被删除");
        }
        //6.查询此账户是否被禁用，若禁用，返回错误
        if (realEmployee.getStatus()!=1){
            return R.error("登陆失败!此账户已被禁用");
        }
        //7.成功登录,并将用户信息存入session
        request.getSession().setAttribute("employee",realEmployee.getId());
        request.getSession().setAttribute("business",realEmployee.getBusinessId());
        request.getSession().setAttribute("type",realEmployee.getType());
        return R.success(realEmployee);
    }

    /***
     * @description 员工退出
     * @param request
     * @return com.joey.yx.common.R<java.lang.String>
     * @author joey
     * @date  16:43
    */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        request.getSession().removeAttribute("business");
        request.getSession().removeAttribute("type");
        return R.success("退出成功");
    }

    /***
     * @description 添加员工
     * @param employee 前端传的员工信息
     * @return com.joey.yx.common.R<java.lang.String>
     * @author joey
     * @date  17:41
    */
    @PostMapping
    public R<String> save(@RequestBody Employee employee){
        return employeeService.saveEmployee(employee);
    }

    /***
     * @description 所有员工分页查询
     * @param page
     * @param pageSize
     * @param name 员工姓名
     * @return com.joey.yx.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     * @author 86180
     * @date  10:37
    */
    @GetMapping("/page")
    public R<Page> page(HttpServletRequest request,int page , int pageSize , String name){
        //新建分页查询对象
        Page<Employee> employeePage = new Page<>(page, pageSize);
        //查询条件
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();

        //根据名字查询
        lqw.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //查询没有被逻辑删除的
        lqw.eq(Employee::getIsDeleted,0);
        //排序规则
        lqw.orderByDesc(Employee::getType);
        lqw.orderByAsc(Employee::getCreateTime);
        employeeService.page(employeePage,lqw);

        return R.success(employeePage);
    }

    @GetMapping("/pageType")
    public R<Page> page(int page , int pageSize , String name, Long businessId){

        return employeeService.getEmployeesByType(page , pageSize,businessId, name);
    }


    /***
     * @description 员工修改
     * @param employee
     * @return com.joey.yx.common.R<java.lang.String>
     * @author 86180
     * @date  11:06
    */
    @PutMapping
    public R<String> updateStatus(@RequestBody Employee employee){

        boolean b = employeeService.updateById(employee);
        if (b){
            return R.success("");
        }

        return R.error("修改失败!");
    }

    /***
     * @description 根据id获取员工信息/回显
     * @param id
     * @return com.joey.yx.common.R<com.joey.yx.pojo.Employee>
     * @author 86180
     * @date  20:03
    */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee == null) {
            return R.error("获取失败!");
        }
        return R.success(employee);
    }

    /***
     * @description 根据id获取员工信息/回显
     * @param id
     * @return com.joey.yx.common.R<com.joey.yx.pojo.Employee>
     * @author 86180
     * @date  20:03
     */
    @DeleteMapping("/{id}")
    public R<String> deleteById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        employee.setIsDeleted(1);
        boolean remove = employeeService.updateById(employee);
        if (remove) {
            return R.success("删除成功!");
        } else {
            return R.error("删除失败!");
        }


    }
}
