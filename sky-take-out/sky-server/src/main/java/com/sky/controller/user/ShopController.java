package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop/")
@Slf4j
@Api(tags = "用户店铺操作接口")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;
    private static final String Key = "SHOP_STATUS";
    /**
     * 获取营业状态
     * @return
     */
    @ApiOperation("用户获取营业状态")
    @GetMapping("/status")
    public Result<Integer> getShopStatus(){
        Integer ShopStatus = (Integer) redisTemplate.opsForValue().get(Key);
        log.info("用户获取营业状态为:{}",ShopStatus == 1 ?"营业中":"打烊中");
        return Result.success(ShopStatus);
    }
}
