package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐管理接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    /**
     * 新建套餐
     * @return
     */
    @PostMapping
    @ApiOperation("新建套餐")
    @CacheEvict(cacheNames = "setmeal",key = "#setmealDTO.categoryId")
    public Result create(@RequestBody SetmealDTO setmealDTO){
        log.info("新建套餐{}",setmealDTO);
        setmealService.create(setmealDTO);
        return Result.success();
    }

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("分页查询{}",setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除")
    @CacheEvict(cacheNames = "setmeal",allEntries = true)
    public Result deleteList(@RequestParam List<Long> ids){
       log.info("批量删除:{}",ids);
       setmealService.deleteList(ids);
       return Result.success();
    }

    /**
     * 修改套餐
     */
    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setmeal",allEntries = true)
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐{}",setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }

    /**
     * 根据id查询套餐信息
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐信息")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("根据id查询套餐信息{}",id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    /**
     * 套餐状态修改
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐状态修改")
    @CacheEvict(cacheNames = "setmeal",allEntries = true)
    public Result status(@PathVariable Integer status,Long id){
        log.info("套餐状态修改 {},{}",status,id);
        setmealService.status(status,id);
        return Result.success();
    }
}
