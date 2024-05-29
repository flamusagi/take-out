package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController("userShopController")
@RequestMapping("/user/shop")
public class ShopController {

    public static final String SHOP_STATUS = "SHOP_STATUS";

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 获取店铺状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺状态")
    public Result<Integer> getStatus(){


        //TODO 不高兴开redis了就 全弄1
        Integer status = (Integer) redisTemplate.opsForValue().get(SHOP_STATUS);
        log.info("获取到店铺的营业状态为：{}",status == 1 ? "营业中" : "打烊中");

        return Result.success(1);
    }

    
}
