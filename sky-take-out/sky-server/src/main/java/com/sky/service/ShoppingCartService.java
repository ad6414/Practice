package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 添加购物车
     * @param shoppingCartDTO
     * @return
     */
    void addGoods(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 删除一个商品
     * @return
     */
    void sub(ShoppingCartDTO shoppingCartDTO);

    /**
     * 清空购物车
     * @param currentId
     */
    void clean(Long currentId);

    void repetiton(Long id);
}
