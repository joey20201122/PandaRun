package com.joey.yx.dto;

import com.joey.yx.pojo.Dish;
import com.joey.yx.pojo.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();


    private Integer copies;
}
