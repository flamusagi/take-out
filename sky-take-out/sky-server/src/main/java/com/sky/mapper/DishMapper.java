package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    /**
     * 根据分类id查询菜品数量
     *
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);



    /**
     * 新增菜品
     *
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    int insert(Dish dish);

    /**
     * 分页查询菜品
     *
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);



    /**
     * 删除菜品
     *
     * @param
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 更新菜品
     *
     * @param dish
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据分类id查询菜品
     *
     * @param
     * @return
     */
    List<Dish> list(Dish dish);

    /**
     * 根据套餐id查询菜品
     *
     * @param setmealId
     * @return
     */
    @Select("select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long setmealId);

    /**
     * 根据条件统计菜品数量
     *
     * @param map
     * @return
     */
    Integer countByMap(Map<String, Integer> map);

    /**
     * 根据category查询菜品
     * @param categoryId
     * @return
     */
    @Select("select * from dish where category_id = #{categoryId} and status = #{status}")
    List<Dish> getByCategoryId(Long categoryId,Integer status);

    @Select("select * from dish where name like concat('%',#{name},'%') and status = #{status}")
    List<Dish> getByName(String name, Integer status);
}
