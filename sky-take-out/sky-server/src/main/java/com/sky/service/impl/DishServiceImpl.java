package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishFlavorService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>  implements DishService {
    @Resource
    private DishMapper dishMapper;
    @Resource
    private SetmealDishMapper setmealDishMapper;

    @Resource
    private DishFlavorMapper dishFlavorMapper;
    @Resource
    private SetmealMapper setmealMapper;

    @Override
    @Transactional // 要么都成功，要么都失败
    public void saveWithFlavor(DishDTO dishDTO) {
        /**
         *insert dish
         */
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //dish.setStatus(StatusConstant.DISABLE);
        dishMapper.insert(dish);

        /**
         *insert flavor
         */
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if (flavors != null && flavors.size() > 0) {

            for(DishFlavor flavor : flavors){
                flavor.setDishId(dishId);
            }
            dishFlavorMapper.insertBatch(flavors); //foreach sql insert
        }




    }

    /**
     * 分页查询菜品
     *
     * @param dishPageQueryDTO
     */
    @Override
    public PageResult queryPage(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 删除菜品
     *
     * @param ids
     */
    @Override
    @Transactional // 一致性
    public void deleteBatch(List<Long> ids) {
        /**
         * 起售中的菜品不能删除
         * 被套餐关联的菜品不能删除
         * 删除菜品后，关联的口味数据也需要删除掉
         */
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus().equals(StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        List<Long> setmealIds = setmealDishMapper.getSetmealIdByDishIds(ids);
        if(setmealIds != null && setmealIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        for (Long id : ids){
            dishMapper.deleteById(id);
            dishFlavorMapper.deleteByDishId(id);
        }


    }

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);
        DishVO dishVO=new DishVO();
        /**
         * copy
         */
        BeanUtils.copyProperties(dish, dishVO);
        List<DishFlavor> dishFlavors= dishFlavorMapper.getByDishId(id);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }
    /**
     * 更新菜品以及保存口味
     *
     * @param dishDTO
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        /**
         *insert dish
         */
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setStatus(StatusConstant.DISABLE);
        dishMapper.update(dish);


        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();

        /**
         * 删除原来的flavor
         */
        dishFlavorMapper.deleteByDishId(dishId);

        /**
         * update新的flavor
         */
        if (flavors != null && flavors.size() > 0) {

            for(DishFlavor flavor : flavors){
                flavor.setDishId(dishId);
            }
            dishFlavorMapper.insertBatch(flavors); //foreach sql insert
        }




    }

    /**
     * 启用或禁用菜品
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id){
        Dish dish = dishMapper.getById(id);
        /*因为操作中的status就是我想要让它改变后的status的值所以可以这么写-_-
            dish.setStatus(status);*/
        if(dish.getStatus().equals(StatusConstant.ENABLE)){
            dish.setStatus(StatusConstant.DISABLE);
            List<Long> dishIds=new ArrayList<>();
            dishIds.add(id);
            /**
             * 如果将该菜品禁用，需要将套餐也禁用
             * 查询到有对应的套餐就禁用，没有就不禁用，所以有一步判断是否为空的操作
             */
            List<Long> setMealIds= setmealDishMapper.getSetmealIdByDishIds(dishIds);
            if (setMealIds != null && setMealIds.size() > 0){
                for(Long setMealId : setMealIds){
                    Setmeal setmeal=setmealMapper.getById(setMealId);
                    setmeal.setStatus(StatusConstant.DISABLE);
                    setmealMapper.update(setmeal);
                }
            }

        }else{
            dish.setStatus(StatusConstant.ENABLE);
        }
        /**
         * 当前对象是在内存里面，所以要进行数据库的操作
         * 数据库是持久化的
         */
        dishMapper.update(dish);
    }

    /**
     * 根据菜品分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> listByCategoryId(Long categoryId) {
        Integer status=StatusConstant.ENABLE;
        List<Dish> dish = dishMapper.getByCategoryId(categoryId,status);
        return dish;
    }

    @Override
    public List<Dish> listByName(String name) {
        Integer status=StatusConstant.ENABLE;
        List<Dish> dish = dishMapper.getByName(name,status);
        return dish;
    }

    /**
     * 条件查询菜品和口味
     *
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);

            // 根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
