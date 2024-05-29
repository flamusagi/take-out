package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService extends IService<Employee> {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 更改员工启禁用状态
     * @param status
     * @param id
     * @return
     */
    Void startOrStop(Integer status,Long id);

    /**
     * 跟新用户信息
     * @param employeeDTO
     * @return
     */
    Integer updateEmployee(EmployeeDTO employeeDTO);


    void addNewEmployee(EmployeeDTO employeeDTO);

    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    Employee getById(long id);

    boolean updatePassword(PasswordEditDTO passwordEditDTO);

    Integer deleteById(Long id);
}
