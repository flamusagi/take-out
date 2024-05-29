package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "员工登录时传递的数据模型")
public class EmployeeLoginDTO implements Serializable {

    /**
     * 实际上在employee数据库username为账号必须唯一
     * name为用户的昵称
     */
    @ApiModelProperty("账号")
    private String username;


    @ApiModelProperty("密码")
    private String password;

}
