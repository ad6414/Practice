package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 插入菜品口味数据
     * @param flavors
     */
    void insertFlavor(List<DishFlavor> flavors);

    /**
     * 根据菜品id删除口味数据
     * @param DishId
     */
    @Delete("delete from dish_flavor where dish_id = #{DishId}")
    void deleteByDishId(Long DishId);

    /**
     * 根据菜品Id查询口味数据
     * @param DishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{DishId}")
    List<DishFlavor> getByDishId(Long DishId);
}
