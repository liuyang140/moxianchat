package com.ly.chatws.util;

import cn.hutool.core.collection.CollUtil;
import com.ly.chat.client.WebChatFeignClient;
import com.ly.common.result.Result;
import com.ly.model.dto.chat.ChatMessageDTO;
import com.ly.model.dto.chat.UpdateMessageDTO;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

@Component
@Slf4j
public class ClientUtils {

    @Autowired
    private WebChatFeignClient chatFeign;

    /*
     * 批量保存消息
     * */
    public Boolean saveMessages(UpdateMessageDTO uDto) {
        Result<List<ChatMessageDTO>> result = executeWithException(
                () -> chatFeign.saveMessages(uDto),
                "保存消息"
        );
        if (result != null && result.isOk()) {
            return true;
        }
        log.error("保存消息失败: {}", result != null ? result.getMessage() : "未知错误");
        return false;
    }

    /*
     * 批量撤回消息
     * */
    public List<ChatMessageDTO> reCallMessages(UpdateMessageDTO uDto) {
        Result<List<ChatMessageDTO>> result = executeWithException(
                () -> chatFeign.recallMessages(uDto),
                "撤回消息"
        );
        if (result != null && result.isOk()) {
            return result.getData();
        }
        log.error("撤回消息失败: {}", result != null ? result.getMessage() : "未知错误");
        return CollUtil.newArrayList();
    }

    /*
     * 根据房间id获取房间内所有用户id
     * */
    public List<Long> getRoomUserIds(Long roomId){
        Result<List<Long>> result = executeWithException(
                () -> chatFeign.getRoomUserIds(roomId),
                "获取房间用户"
        );
        if (result != null && result.isOk()) {
            return result.getData();
        }
        log.error("获取房间用户失败: {}", result != null ? result.getMessage() : "未知错误");
        return CollUtil.newArrayList();
    }

    /*
    * 获取房间内某用户未读消息数量
    * */
    public Long unreadCountOne(Long roomId,Long userId){
        Result<Long> result = executeWithException(
                () -> chatFeign.unreadCountOne(roomId,userId),
                "获取房间用户未读消息数量"
        );
        if (result != null && result.isOk()) {
            return result.getData();
        }
        log.error("获取房间用户未读消息数量失败: {}", result != null ? result.getMessage() : "未知错误");
        return 0L;
    }

    /*
    * 统计用户所有聊天室未读消息总数
    * */
    public Long getTotalUnreadCount(Long userId) {
        Result<Long> result = executeWithException(
                () -> chatFeign.getTotalUnreadCount(userId),
                "获取用户所有聊天室未读消息总数"
        );
        if (result != null && result.isOk()) {
            return result.getData();
        }
        log.error("获取用户所有聊天室未读消息总数失败: {}", result != null ? result.getMessage() : "未知错误");
        return 0L;
    }

    public Boolean updateLastReadTime(Long roomId, Long userId) {
        Result<Boolean> result = executeWithException(() -> chatFeign.updateReadStatus(roomId, userId, LocalDateTime.now()),
                "更新最后已读状态成功"
        );
        if (result != null && result.isOk()) {
            return result.getData();
        } else {
            log.error("更新最后已读状态失败: {}", result != null ? result.getMessage() : "未知错误");
        }
        return Boolean.FALSE;
    }

    /*
    * 模版方法
    * */
    public <T> T executeWithException(Supplier<T> action, String errorMessage) {
        try {
            return action.get();
        } catch (Exception e) {
            log.error("{}异常: {}", errorMessage, e.getMessage(), e);
            return null;
        }
    }


}
