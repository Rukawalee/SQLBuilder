package com.rukawa.sql.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageParam {

    // 当前页
    private Integer current;

    // 每页大小
    private Integer pageSize;
}
