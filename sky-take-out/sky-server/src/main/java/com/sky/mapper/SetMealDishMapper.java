package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface SetMealDishMapper {
    /**
     * 根据ids查询关系表
     * @param ids
     * @return
     */
    List<SetmealDish> selectByIds(List<Long> ids);

    /**
     * 根据菜品id查询套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsIds(List<Long> dishIds);

    /**
     * 添加套餐菜品关系
     * @param setmealDish
     */
    void create(SetmealDish setmealDish);

    /**
     * 批量删除
     * @param ids
     */
    void deleteList(List<Long> ids);

    /**
     * 根据套餐id查询菜品列表
     * @param SetMealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{SetMealId}")
    List<SetmealDish> selectBySetMealId(Long SetMealId);
}
