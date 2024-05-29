package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        /**
         * 先得到当前用户id，不然不知道是哪个用户的购物车
         */
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //判断当前商品是否在购物车中
        ShoppingCart shopCart =shoppingCartMapper.findByCart(shoppingCart);

        if(shopCart !=null){
            //如果已经存在了，只需要将数量加一
            shopCart.setNumber(shoppingCart.getNumber()+1);
            shoppingCartMapper.updateNumberById(shopCart.getId());

        }else{
            //如果不存在，需要插入一条购物车数据
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

            //判断本次添加到购物车的是菜品还是套餐
            /**
             * 购物车字段有菜品id和套餐id，很明显它们不可能同时存在，只能有一个不为空
             */
            if(shoppingCart.getDishId()!=null){
                Dish dish=dishMapper.getById(shoppingCart.getDishId());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice().abs());
            }else if(shoppingCart.getSetmealId()!=null){
                Setmeal setmeal = setmealMapper.getById(shoppingCart.getSetmealId());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice().abs());
            }
            shoppingCartMapper.insert(shoppingCart);
        }




    }
    /**
     * 查看购物车
     * @return
     */
    public List<ShoppingCart> showShoppingCart(){
        //获取到当前微信用户的id
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCarts=shoppingCartMapper.findByUserId(userId);
        return shoppingCarts;
    }

    /**
     * 清空购物车
     */
    public void cleanShoppingCart() {
        //获取到当前微信用户的id
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO){
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        /**
         * 先得到当前用户id，不然不知道是哪个用户的购物车
         */
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        ShoppingCart shopCart =shoppingCartMapper.findByCart(shoppingCart);
        if(shopCart!=null){
            /**
             * 我没搞懂这个函数要干嘛，とりあえず先考虑它减掉一个是否删完的情况
             */
            if(shopCart.getNumber()==1){
                shoppingCartMapper.deleteById(shopCart.getId());

            }else{
                shopCart.setNumber(shopCart.getNumber()-1);
                shoppingCartMapper.updateNumberById(shopCart.getId());
            }


        }
    }
}
