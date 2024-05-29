package com.sky.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.mapper.EmployeeMapper;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@Slf4j
@RestController
@RequestMapping("/admin/employee")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;
    @Resource
    private EmployeeMapper employeeMapper;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }




    /**
     * 员工分页查询
     *
     * @param employeePageQueryDTO
     *
     * @return Result<PageResult<Employee>>
     */
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("分页查询员工：{}", employeePageQueryDTO);
        PageResult pageQueryData = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageQueryData);
    }






    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("查询员工信息")
    public Result<Employee>getById(@PathVariable Long id){

        Employee employee= employeeService.getById(id);
        employee.setPassword("****");

        return Result.success(employee);
    }

    /**
     * 根据id删除员工信息
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除员工信息")
    public Result deleteById(@RequestParam("id") Long id){
        log.info("删除员工：{}", id);
        employeeService.deleteById(id);
        return Result.success(MessageConstant.DISH_DEL_SUC);
    }

    /**
     * 启禁用员工账号
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启禁用员工账号")
    public Result startOrStop(@PathVariable Integer status,long id){

         employeeService.startOrStop(status,id);

        return Result.success();
    }


    /**
     * 用户修改密码
     * @param passwordEditDTO
     * @return
     */
    @PutMapping("/editPassword")
    @ApiOperation("修改密码")
    public Result updatePassword(@RequestBody PasswordEditDTO passwordEditDTO){
        log.info("根据修改员工密码：{}", passwordEditDTO.getEmpId());
        boolean isSuccess =employeeService.updatePassword(passwordEditDTO);
        if(isSuccess){
        return Result.success(MessageConstant.PASSWORD_EDIT_SUCCESS);
        }else{
            return Result.error(MessageConstant.PASSWORD_ERROR);
        }
    }

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增员工")
    public Result addNewEmployee(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工：{}", employeeDTO);

        employeeService.addNewEmployee(employeeDTO);
        return Result.success();
    }


    /**
     * 编辑员工信息
     * @param employeeDTO
     * @return
     */
    @PutMapping
    @ApiOperation("编辑员工信息")
    public Result updateEmployee(@RequestBody EmployeeDTO employeeDTO){

        /**
         * 在handler包下统一处理插入数据的键值重复问题
         */
        /*Employee emp = employeeService.getById(employeeDTO.getId());
        if(!emp.getUsername().equals(employeeDTO.getUsername())){
            Employee employee = employeeMapper.getByUsername(employeeDTO.getUsername());
            if (employee != null){
                Result.error("此用户名" + MessageConstant.ALREADY_EXISTS);
            }
        }*/
        employeeService.updateEmployee(employeeDTO);

        return Result.success();
    }



}
