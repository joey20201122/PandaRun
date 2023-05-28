package com.joey.yx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.joey.yx.pojo.User;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/4/4 14:29
 */
public interface UserService extends IService<User> {
    Integer getNeedTime();

    User changeName(String name);

    Integer getMakeTime();
}
