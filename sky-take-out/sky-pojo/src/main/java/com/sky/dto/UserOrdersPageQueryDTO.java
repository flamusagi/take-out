package com.sky.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrdersPageQueryDTO implements Serializable {
    private int pageNum;
    private int pageSize;
    private Integer status;
    private Long userId;
}
