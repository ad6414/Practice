package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "管理端订单接口")
public class OrderController {
    @Autowired private OrderService orderService;

    /**
     * 分页查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("分页查询订单")
    public Result<PageResult> PageQueryByInfo(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("条件查询订单信息：{}",ordersPageQueryDTO);
        PageResult pageResult = orderService.PageQueryByInfo(ordersPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 根据id拒绝订单
     * @param ordersRejectionDTO
     */
    @PutMapping("/rejection")
    @ApiOperation("拒绝订单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        log.info("拒绝订单：{}，拒单原因：{}",ordersRejectionDTO.getId(),ordersRejectionDTO.getRejectionReason());
        orderService.rejection(ordersRejectionDTO.getId(),ordersRejectionDTO.getRejectionReason());
        return Result.success();
    }

    /**
     * 根据id拒绝订单
     * @param ordersCancelDTO
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("取消订单：{}，取消原因：{}",ordersCancelDTO.getId(),ordersCancelDTO.getCancelReason());
        orderService.cancel(ordersCancelDTO.getId(),ordersCancelDTO.getCancelReason());
        return Result.success();
    }

    /**
     * 根据订单id接收订单
     * @param ordersConfirmDTO
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        Long id = ordersConfirmDTO.getId();
        log.info("根据id接受订单：{}",id);
        orderService.confirm(id);
        return Result.success();
    }

    /**
     * 根据id标记完成订单
     * @param id
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable Long id){
       log.info("完成订单：{}",id);
       orderService.complete(id);
       return Result.success();
    }

    /**
     * 查看订单详情
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查看订单详情")
    public Result<OrderVO> details(@PathVariable Long id){
        log.info("查看订单详情:{}",id);
        OrderVO vo = orderService.details(id);
        return Result.success(vo);
    }

    /**
     * 标记订单派送
     * @param id
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("标记订单派送")
    public Result delivery(@PathVariable Long id){
        log.info("标记订单{}派送",id);
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 统计各状态订单数
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("各状态订单数总览")
    public Result<OrderStatisticsVO> statistics(){
        return Result.success(orderService.statistics());
    }
}
