package com.ly.model.entity.customer;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ly.model.entity.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("customer_location")
public class CustomerLocation extends BaseEntity {
    @TableField("customer_id")
    private Long customerId;

    @TableField("latitude")
    private Double latitude;

    @TableField("longitude")
    private Double longitude;

}