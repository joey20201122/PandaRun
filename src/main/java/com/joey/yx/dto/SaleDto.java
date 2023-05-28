package com.joey.yx.dto;

import com.joey.yx.pojo.BusinessMonthsales;
import com.joey.yx.pojo.DishMonthsales;
import lombok.Data;

import java.util.List;

/**
 * @author 86180
 * @version 1.0
 * @description TODO
 * @date 2023/5/19 11:29
 */
@Data
public class SaleDto extends BusinessMonthsales {

    private List<DishMonthsales> dishMonthsalesList;

}
