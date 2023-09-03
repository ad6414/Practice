package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface SetmealService {
    /**
     * 新建套餐
     * @return
     */
    void create(SetmealDTO setmealDTO);

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 批量删除
     * @param ids
     * @return
     */
    void deleteList(List<Long> ids);


    /**
     * 根据id查询套餐信息
     */
    SetmealVO getById(Long id);

    /**
     * 修改套餐
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 套餐状态修改
     * @param status
     * @return
     */
    void status(Integer status, Long id);
}
