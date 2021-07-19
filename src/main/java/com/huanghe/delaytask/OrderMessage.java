package com.huanghe.delaytask;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderMessage {

    private String orderId;
    private BigDecimal amount;
    private Long userId;
    private String timestamp;
}
