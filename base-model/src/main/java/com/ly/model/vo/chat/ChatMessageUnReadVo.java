package com.ly.model.vo.chat;

import com.ly.model.dto.chat.ChatMessageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Schema(description = "未读消息vo")
@Accessors(chain = true)
public class ChatMessageUnReadVo {
    private Long roomId;

    private Long unreadCount;

    private ChatMessageDTO chatMessageDTO;
}
