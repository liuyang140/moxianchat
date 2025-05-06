package com.ly.model.vo.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "消息vo")
public class ChatMessageVo {
    @Schema(description = "消息id")
    private Long id;
    @Schema(description = "发送人id")
    private Long senderId;
    @Schema(description = "接收人id")
    private Long receiverId;
    @Schema(description = "消息内容")
    private String content;
    @Schema(description = "消息类型")
    private Integer messageType;
    @Schema(description = "聊天类型")
    private Integer chatType;
    @Schema(description = "时间戳")
    private Long timestamp;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}