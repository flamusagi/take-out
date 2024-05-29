package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService extends IService<Category> {

    /**
     * 更改分类启禁用状态
     * @param status
     * @param id
     * @return
     */
    Void startOrStop(Integer status,Long id);

    /**
     * 删除分类
     * @param id
     */
     void remove(Long id);

    /**
     * 通过名字查
     * @param name
     * @return
     */
    String getByName(String name);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    List<Category> list(Integer type);

    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    void insert(CategoryDTO categoryDTO);

    Integer updateCategory(CategoryDTO categoryDTO);
}
