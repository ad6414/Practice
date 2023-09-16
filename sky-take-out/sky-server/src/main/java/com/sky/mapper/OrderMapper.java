package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 条件查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> QueryOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 根据订单号查询订单
     * @param outTradeNo
     * @return
     */
    @Select("select * from orders where number = #{outTradeNo}")
    Orders getByNumber(String outTradeNo);

    /**
     * 修改订单状态
     * @param orders
     */
    void update(Orders orders);

    /**
     * 查询指定状态指定时间订单
     * @param status
     * @param checkTime
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{checkTime} ")
    List<Orders> getByRangeTime(Integer status, LocalDateTime checkTime);

    /**
     * 条件查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    Page<OrderVO> PageQueryByInfo(OrdersPageQueryDTO ordersPageQueryDTO);
}
