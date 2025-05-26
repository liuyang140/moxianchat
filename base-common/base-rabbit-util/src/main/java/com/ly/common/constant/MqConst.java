package com.ly.common.constant;

public class MqConst {


    public static final String EXCHANGE_ORDER = "chat.order";
    public static final String ROUTING_PAY_SUCCESS = "chat.pay.success";
    public static final String ROUTING_PROFITSHARING_SUCCESS = "chat.profitsharing.success";
    public static final String QUEUE_PAY_SUCCESS = "chat.pay.success";
    public static final String QUEUE_PROFITSHARING_SUCCESS = "chat.profitsharing.success";


    //取消订单延迟消息
    public static final String EXCHANGE_CANCEL_ORDER = "chat.cancel.order";
    public static final String ROUTING_CANCEL_ORDER = "chat.cancel.order";
    public static final String QUEUE_CANCEL_ORDER = "chat.cancel.order";

    //分账延迟消息
    public static final String EXCHANGE_PROFITSHARING = "chat.profitsharing";
    public static final String ROUTING_PROFITSHARING = "chat.profitsharing";
    public static final String QUEUE_PROFITSHARING  = "chat.profitsharing";

}
