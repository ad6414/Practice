package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result insert(@RequestBody DishDTO dishDTO){
        log.info("新增菜品{}",dishDTO);
        dishService.insertWithFlavor(dishDTO);

        redisTemplate.delete("dishlist_"+dishDTO.getCategoryId());

        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询：{}",dishPageQueryDTO);
        PageResult pageQuery = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageQuery);
    }

    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品批量删除{}",ids);
        dishService.deleteByIds(ids);
        removeAllRedis();
        return Result.success();
    }

    /**
     * 根据菜品id查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据菜品id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据菜品id:{} 查询菜品",id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    /**
     * 菜品修改
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("菜品修改")
    public Result modifyDish(@RequestBody DishDTO dishDTO){
        log.info("修改菜品:{} ",dishDTO);
        dishService.modifyDish(dishDTO);
        removeAllRedis();
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getByCategoryId(Long categoryId){
        log.info("根据分类id查询菜品{}",categoryId);
        List<Dish> dishList = dishService.getByCategoryId(categoryId);
        return Result.success(dishList);
    }
    @PostMapping("/status/{status}")
    @ApiOperation("菜品状态修改")
    public Result status(@PathVariable Integer status,Long id){
        log.info("菜品状态修改{},{}",status,id);
        dishService.status(id,status);
        removeAllRedis();
        return Result.success();
    }

    private void removeAllRedis(){
       redisTemplate.delete(redisTemplate.keys("dishlist_*"));
    }
}
