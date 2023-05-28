package com.joey.yx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.joey.yx.common.BaseContext;
import com.joey.yx.common.R;
import com.joey.yx.pojo.User;
import com.joey.yx.service.UserService;
import com.joey.yx.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @
 *
 *
 * date 2023/4/4 14:30
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMessage (@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            //生成验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            //调用阿里云api完成短信发送
            log.info("验证码为-----"+code);
            //将生成的验证码保存起来，存到session中
            session.setAttribute(phone,code);

            return R.success(code);
        }
        return R.error("验证码发送失败");
    }

    /**
     * 用户登录
     * @param map
     * @return
     */
    @PostMapping("/login")
    public R<User> login (@RequestBody Map map, HttpSession session){
        //获取手机号,验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //从session获取保存的验证码
        Object codeInSession = session.getAttribute(phone);
        //校验验证码是否正确
        if (codeInSession != null && codeInSession.equals(code)) {
            //若校验成功，则登录成功
            //判断若为新用户，为他注册账户
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
            lqw.eq(User::getPhone,phone);
            User user = userService.getOne(lqw);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setName("一只小胖达");
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            BaseContext.setCurrentId(user.getId());
            return R.success(user);
        }
        //校验失败，返回错误
        return R.error("登录失败");
    }

    /**
     * 用户退出
     * @return
     */
    @PostMapping("/loginout")
    public R<String> loginout (){
        return R.success("退出成功");
    }


    @GetMapping("/page")
    public R<Page> page (int page , int pageSize , String name , String phone){
        //新建分页对象
        Page<User> userPage = new Page<>(page,pageSize);
        //构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name),User::getName,name);
        queryWrapper.eq(StringUtils.isNotBlank(phone),User::getPhone,phone);
        //按注册时间降序
        queryWrapper.orderByDesc(User::getCreateTime);
        //分页查询
        userService.page(userPage, queryWrapper);

        return R.success(userPage);
    }

    @GetMapping("/getNeedTime")
    public R<Integer> getNeedTime (){


        return R.success(userService.getNeedTime());
    }

    @PutMapping("/changeName")
    public R<User> changeName(String name){

        return R.success(userService.changeName(name));
    }

    @GetMapping("/makeTime")
    public R<Integer> makeTime(){

        return R.success(userService.getMakeTime());
    }
}
