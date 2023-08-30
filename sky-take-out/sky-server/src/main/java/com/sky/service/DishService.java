package com.sky.service;

import com.sky.dto.DishDTO;

public interface DishService {
    /**
     * 新增菜品以及绑定口味
     * @param dishDTO
     */
    void insertWithFlavor(DishDTO dishDTO);


}
