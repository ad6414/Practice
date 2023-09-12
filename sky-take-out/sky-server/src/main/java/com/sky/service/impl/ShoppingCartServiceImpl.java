package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.ShoppingCart;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired private DishMapper dishMapper;
    @Autowired private SetMealMapper setMealMapper;
    /**
     * 查看购物车
     * @return
     */
    public List<ShoppingCart> list(ShoppingCart shoppingCart) {
        return shoppingCartMapper.list(shoppingCart);
    }

    /**
     * 删除一个商品
     * @return
     */
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        //查询用户购物车
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(BaseContext.getCurrentId()).build();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        //判断是否有菜品
        if (list == null || list.isEmpty()){
            throw new ShoppingCartBusinessException(MessageConstant.UNKNOWN_ERROR);
        }
        //修改数量
        shoppingCart = list.get(0);
        int i = shoppingCart.getNumber() - 1;
        //判断修改后数量是否为0
        if ((i == 0)) {
            shoppingCartMapper.del(shoppingCart);
        } else {
            shoppingCart.setNumber(i);
            shoppingCartMapper.update(shoppingCart);
        }

    }

    /**
     * 添加购物车
     * @param shoppingCartDTO
     * @return
     */
    public void addGoods(ShoppingCartDTO shoppingCartDTO) {
        //查询用户购物车
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(BaseContext.getCurrentId()).build();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        //判断是否有重复菜品
        if(list != null && !list.isEmpty()){
            // 更新数量
            shoppingCart = list.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber()+1);
            shoppingCartMapper.update(shoppingCart);
        }else {
            // 新建数据项
            shoppingCart.setCreateTime(LocalDateTime.now());
            if (shoppingCart.getDishId() == null) {
                SetmealVO setmealVO = setMealMapper.getById(shoppingCart.getSetmealId());
                shoppingCart.setName(setmealVO.getName());
                shoppingCart.setAmount(setmealVO.getPrice());
                shoppingCart.setImage(setmealVO.getImage());
            }else {
                Dish dish = dishMapper.selectById(shoppingCart.getDishId());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
            }
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 清空购物车
     * @param userId
     */
    public void clean(Long userId) {
        shoppingCartMapper.clean(userId);
    }
}
