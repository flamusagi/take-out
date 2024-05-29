package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.*;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @Update("update employee set status = #{status} where id = #{id}")
    Integer updateStatusById(Integer status,Long id);

    @Update("update employee set password = #{password} where id = #{id}")
    Integer updatePasswordById(String password,Long id);


    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    @Select("select * from employee where id = #{id}")
    Employee getById(long id);

    @Delete("delete from employee where id = #{id}")
    Integer deleteById(long id);

    @Insert("INSERT INTO employee (name, username, password, phone, sex, id_number, status, create_time, create_user, update_time, update_user)" +
            " VALUES" +
            " (#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{createUser}, #{updateTime}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    int insert(Employee employee);

    @AutoFill(value = OperationType.UPDATE)
    void update(Employee employee);
}
