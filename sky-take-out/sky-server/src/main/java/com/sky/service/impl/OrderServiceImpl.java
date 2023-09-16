package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.*;
import com.sky.websocket.WebSocketServer;
import io.swagger.annotations.ApiOperation;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.Buffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired private OrderMapper orderMapper;
    @Autowired private OrderDetailMapper orderDetailMapper;
    @Autowired private AddressBookMapper addressBookMapper;
    @Autowired private ShoppingCartMapper shoppingCartMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private WebSocketServer webSocketServer;
    /**
     * 用户提交订单
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);

        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(new StringBuffer()
                .append(addressBook.getProvinceName())
                .append(addressBook.getCityName())
                .append(addressBook.getDistrictName())
                .append(addressBook.getDetail()).toString());
        orderMapper.insert(orders);

        //插入订单明细表
        ShoppingCart cart = ShoppingCart.builder().userId(userId).build();
        List<ShoppingCart> list = shoppingCartMapper.list(cart);
        List<OrderDetail> details = new ArrayList<>();
        for (ShoppingCart shoppingCart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            details.add(orderDetail);
        }
        orderDetailMapper.insertList(details);
        //生成返回数据
        OrderSubmitVO submitVO = OrderSubmitVO.builder()
                .orderTime(orders.getOrderTime())
                .orderAmount(ordersSubmitDTO.getAmount())
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .build();
        //清空购物车
        shoppingCartMapper.clean(userId);
        return submitVO;
    }

    /**
     * 历史订单查询
     * @param
     */
    public PageResult historyOrders(int pageNum, int pageSize, Integer status) {
        PageHelper.startPage(pageNum,pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        Page<Orders> page = orderMapper.QueryOrders(ordersPageQueryDTO);
        List<OrderVO> list = new ArrayList<>();
        if (page !=null && page.getTotal() > 0) {
            for (Orders order : page) {
                List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(order.getId());
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(order,orderVO);
                orderVO.setOrderDetailList(orderDetailList);
                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(),list);
    }

    /**
     * 订单详情查询
     * @param id
     * @return
     */
    public OrderVO QueryOrderDetail(Long id) {
        Orders order = orderMapper.getById(id);
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderDetailList(orderDetailList);
        BeanUtils.copyProperties(order,orderVO);
        return orderVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
        JSONObject jsonObject = new JSONObject();
        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));
        vo.setNonceStr(ordersPaymentDTO.getOrderNumber());
        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
        //推送提醒
        Map<Object, Object> map = new HashMap<>();
        map.put("type",1);
        map.put("orderId",ordersDB.getId());
        map.put("content","订单号："+outTradeNo);
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }


    /**
     * 根据条件分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    public PageResult PageQueryByInfo(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Page<OrderVO> page = orderMapper.PageQueryByInfo(ordersPageQueryDTO);
        List<OrderVO> ordersList = page.getResult();
        for (OrderVO orderVO : ordersList) {
            List<String> strings = getStrings(orderVO);
            orderVO.setOrderDishes(String.join(",",strings));
        }
        return new PageResult(page.getTotal(),ordersList);
    }

    /**
     * 获取菜品数据为字符串
     * @param orderVO
     * @return
     */
    private List<String> getStrings(OrderVO orderVO) {
        StringBuffer stringBuffer = new StringBuffer();
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orderVO.getId());
        List<String> strings = new ArrayList<>();
        for (OrderDetail detail : orderDetailList) {
            strings.add(detail.getName());
        }
        return strings;
    }

    /**
     * 根据订单id拒单
     * @param id
     * @param rejectionReason
     */
    public void rejection(Long id, String rejectionReason) {
        Orders order = orderMapper.getById(id);
        order.setStatus(Orders.CANCELLED);
        order.setRejectionReason(rejectionReason);
        orderMapper.update(order);
    }
    /**
     * 根据订单id取消订单
     * @param id
     * @param cancelReason
     */
    public void cancel(Long id, String cancelReason) {
        Orders order = orderMapper.getById(id);
        order.setStatus(Orders.CANCELLED);
        order.setCancelReason(cancelReason);
        orderMapper.update(order);
    }

    /**
     * 根据id接收订单
     * @param id
     */
    public void confirm(Long id) {
        Orders orders = orderMapper.getById(id);
        orders.setStatus(Orders.CONFIRMED);
        orderMapper.update(orders);
    }

    /**
     * 根据id标记完成订单
     * @param id
     */
    public void complete(Long id) {
        Orders orders = orderMapper.getById(id);
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 查看订单详情
     * @param id
     * @return
     */
    public OrderVO details(Long id) {
        Orders orders = orderMapper.getById(id);
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(orders,vo);
        vo.setOrderDetailList(orderDetailList);
        return vo;
    }

    /**
     * 标记订单派送
     * @param id
     */
    public void delivery(Long id) {
        Orders orders = orderMapper.getById(id);
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }

    /**
     * 统计各状态订单数
     * @return
     */
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO viewVO = new OrderStatisticsVO();
        OrdersPageQueryDTO queryDTO = new OrdersPageQueryDTO();

        //待接单数量
        queryDTO.setStatus(Orders.TO_BE_CONFIRMED);
        viewVO.setToBeConfirmed(orderMapper.PageQueryByInfo(queryDTO).size());
        //待派送数量
        queryDTO.setStatus(Orders.CONFIRMED);
        viewVO.setConfirmed(orderMapper.PageQueryByInfo(queryDTO).size());
        //派送中数量
        queryDTO.setStatus(Orders.DELIVERY_IN_PROGRESS);
        viewVO.setDeliveryInProgress(orderMapper.PageQueryByInfo(queryDTO).size());

        return viewVO;
    }

    /**
     * 用户催单
     * @param id
     */
    public void reminder(Long id) {
        Orders ordersDB = orderMapper.getById(id);
        //推送提醒
        Map<Object, Object> map = new HashMap<>();
        map.put("type",2);
        map.put("orderId",ordersDB.getId());
        map.put("content","订单号："+ordersDB.getNumber());
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }
}
