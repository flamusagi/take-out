package com.sky.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealDishService;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Resource
    private SetmealMapper setmealMapper;
    @Resource
    private SetmealDishMapper setmealDishMapper;

    @Resource
    private DishMapper dishMapper;

    /**
     * 新增套餐
     *
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insert(setmeal);

        List<SetmealDish> setmealDishes=setmealDTO.getSetmealDishes();
        Long setmealId=setmeal.getId();
        /**
         * 菜品为空前端会提示菜品为空不能添加
         */
        if (setmealDishes != null && setmealDishes.size() > 0) {

            for(SetmealDish setmealDish:setmealDishes){
                setmealDish.setSetmealId(setmealId);
            }
            setmealDishMapper.insertBatch(setmealDishes);
        }



        }

    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 删除套餐
     *
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids){
        //套餐起售的不能删除
        //删除套餐后，菜品和相关联的套餐也要删除
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);
            if (setmeal.getStatus().equals(StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        for (Long id : ids){
            setmealMapper.deleteById(id);
            setmealDishMapper.deleteBySetmealId(id);
        }

    }
    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id){
        Setmeal setmeal=setmealMapper.getById(id);
        SetmealVO setmealVO=new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        List<SetmealDish> setmealDishes=setmealDishMapper.getDishBySetmealId(id);
        if (setmealDishes != null && setmealDishes.size() > 0){
            setmealVO.setSetmealDishes(setmealDishes);
        }
        return setmealVO;
    }

    /**
     * 更新套餐
     *
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmeal.setStatus(StatusConstant.DISABLE);
        setmealMapper.update(setmeal);
        //删除原来的菜品
        List<SetmealDish>setmealDishes=setmealDTO.getSetmealDishes();
        Long setMealId=setmeal.getId();
        if (setmealDishes != null && setmealDishes.size() > 0){
            setmealDishMapper.deleteBySetmealId(setMealId);
        }
        //更新新添加的菜品
        if (setmealDishes != null && setmealDishes.size() > 0){
            for(SetmealDish setmealDish:setmealDishes){
                setmealDish.setSetmealId(setMealId);
            }
            setmealDishMapper.insertBatch(setmealDishes);
        }



        }
    /**
     * 上架或下架
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        /**
         * 如果菜品为停售，套餐要改为起售状态，则报错
         */
        Setmeal setmeal = setmealMapper.getById(id);

        /*因为操作中的status就是我想要让它改变后的status的值所以可以这么写-_-
            dish.setStatus(status);*/
        if(setmeal.getStatus().equals(StatusConstant.ENABLE)){
            setmeal.setStatus(StatusConstant.DISABLE);
        }else{
            List<SetmealDish> setmealDishes=setmealDishMapper.getDishBySetmealId(id);
            if (setmealDishes != null && setmealDishes.size() > 0) {

                for(SetmealDish setmealDish:setmealDishes){
                    Long dishId=setmealDish.getDishId();
                    Dish dish=dishMapper.getById(dishId);
                    if(dish.getStatus().equals(StatusConstant.DISABLE)){
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                }
            }
            setmeal.setStatus(StatusConstant.ENABLE);
            setmealMapper.update(setmeal);
        }

    }
    /**
     * 条件查询
     *
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     *
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
