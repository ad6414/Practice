package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

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
}
