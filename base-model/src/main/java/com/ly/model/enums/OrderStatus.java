package com.ly.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum OrderStatus {
    UNPAID(7, "待付款"),
    PAID(8, "已付款"),
    FINISH(9, "完成"),
    CANCEL_ORDER(-1, "未接单取消订单"),
    NULL_ORDER(-100, "不存在"),
    ;

    @EnumValue
    private Integer status;
    private String comment;

    OrderStatus(Integer status, String comment) {
        this.status = status;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
