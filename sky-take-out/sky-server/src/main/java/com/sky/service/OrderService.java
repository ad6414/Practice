package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.*;

public interface OrderService {

    /**
     * 用户提交订单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 历史订单查询
     * @param
     */
    PageResult historyOrders(int page, int pageSize, Integer status);

    /**
     * 订单详情查询
     * @param id
     * @return
     */
    OrderVO QueryOrderDetail(Long id);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 根据条件分页查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult PageQueryByInfo(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id拒单
     * @param id
     * @param rejectionReason
     */
    void rejection(Long id, String rejectionReason);

    /**
     * 根据id接收订单
     * @param id
     */
    void confirm(Long id);

    /**
     * 根据id标记完成订单
     * @param id
     */
    void complete(Long id);

    /**
     * 查看订单详情
     * @param id
     * @return
     */
    OrderVO details(Long id);

    /**
     * 标记订单派送
     * @param id
     */
    void delivery(Long id);

    /**
     * 统计各状态订单数
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 根据id取消订单
     * @param id
     * @param cancelReason
     */
    void cancel(Long id, String cancelReason);

    /**
     * 用户催单
     * @param id
     */
    void reminder(Long id);
}
