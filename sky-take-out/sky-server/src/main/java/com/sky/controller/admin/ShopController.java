package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop/")
@Slf4j
@Api(tags = "店铺操作接口")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;
    private static final String Key = "SHOP_STATUS";
    /**
     * 设置营业状态
     * @return
     */
    @ApiOperation("设置营业状态")
    @PutMapping("{status}")
    public Result shopStatus(@PathVariable Integer status){
        log.info("设置营业状态为：{}",status == 1 ?"营业中":"打烊中");
        redisTemplate.opsForValue().set(Key,status);
        return Result.success();
    }

    /**
     * 获取营业状态
     * @return
     */
    @ApiOperation("获取营业状态")
    @GetMapping("/status")
    public Result<Integer> getShopStatus(){
        Integer ShopStatus = (Integer) redisTemplate.opsForValue().get(Key);
        log.info("获取营业状态为:{}",ShopStatus == 1 ?"营业中":"打烊中");
        return Result.success(ShopStatus);
    }
}
