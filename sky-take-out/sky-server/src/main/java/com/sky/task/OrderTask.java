package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class OrderTask {

    @Resource
    private OrderMapper orderMapper;
    /**
     * 如果过15分钟还未支付，则取消订单
     */
    @Scheduled(cron = "0 * * * * ? ") //每分钟触发一次
    public void processTimeoutOrder(){
        /**
         *
         * 解决的逻辑是，先得到离当前时间前15分钟的orderTime
         * sql查询对应状态和时间的订单是否存在
         * 存在进行下一步
         */
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> orderList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);

        if (orderList != null && orderList.size()>0){
            for (Orders orders : orderList){
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("15分钟未支付,自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
                log.info("成功处理支付超时订单：{}", orders.getNumber());

            }
        }

    }

    /**
     * 处理一直处于派送中状态的订单
     */
    @Scheduled(cron = "0 0 1 * * ?") //每天凌晨1点触发一次
    public void processDeliveryOrder(){
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> orderList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        if (orderList != null && orderList.size()>0){
            for (Orders orders : orderList){
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
                orders.setDeliveryTime(LocalDateTime.now());

                log.info("成功处理派送中订单：{}",orders.getNumber());

            }
        }

    }

}
