package com.sky.controller.user;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.service.ShoppingCartService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@Api(tags = "用户订单接口")
@RequestMapping("/user/order")
@Slf4j
public class OrderController {
    @Autowired private OrderService orderService;
    @Autowired private ShoppingCartService shoppingCartService;

    /**
     * 用户提交订单
     * @param ordersSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    @ApiOperation("用户提交订单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户提交订单{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }
    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        orderService.paySuccess(orderPaymentVO.getNonceStr());
        return Result.success(orderPaymentVO);
    }

    /**
     * 历史订单查询
     * @param
     * @return
     */
    @GetMapping("/historyOrders")
    @ApiOperation("历史订单查询")
    public Result<PageResult> historyOrders(int page ,int pageSize ,Integer status){
        log.info("历史订单分页查询：第{}页，状态为：{}",page,pageSize,status);
        PageResult pageResult = orderService.historyOrders(page,pageSize,status);
        return Result.success(pageResult);
    }

    /**
     * 订单详情查询
     * @param id
     * @return
     */
    @GetMapping("orderDetail/{id}")
    @ApiOperation("订单详情查询")
    public Result<OrderVO> QueryOrderDetail(@PathVariable Long id){
        log.info("订单详情查询:{}",id);
        OrderVO orderVO = orderService.QueryOrderDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 用户催单
     * @param id
     * @return
     */
    @GetMapping("/reminder/{id}")
    @ApiOperation("用户催单")
    public Result reminder(@PathVariable Long id){
        log.info("用户催单：{}",id);
        orderService.reminder(id);
        return Result.success();
    }

    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id){
        shoppingCartService.repetiton(id);
        return Result.success();
    }
}
