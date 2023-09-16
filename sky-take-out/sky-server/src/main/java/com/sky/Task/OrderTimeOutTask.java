package com.sky.Task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTimeOutTask {
    @Autowired
    private OrderMapper orderMapper;
    @Scheduled(cron = "0 * * * * ?")
    public void PayCheck(){
        log.info("处理超时未支付订单{}", LocalDateTime.now());
        List<Orders> ordersList = orderMapper.getByRangeTime(
                        Orders.PENDING_PAYMENT,
                        LocalDateTime.now().plusMinutes(-15));
        if (!ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("超时未支付");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }
    @Scheduled(cron = "0 0 1 * * ?")
    public void SentCheck(){
        log.info("处理超时未完成订单{}", LocalDateTime.now());
        List<Orders> ordersList = orderMapper.getByRangeTime(
                Orders.DELIVERY_IN_PROGRESS,
                LocalDateTime.now().plusHours(-1));
        if (!ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
