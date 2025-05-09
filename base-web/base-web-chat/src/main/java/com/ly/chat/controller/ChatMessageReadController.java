package com.ly.chat.controller;

import com.ly.chat.service.ChatMessageReadService;
import com.ly.common.result.Result;
import com.ly.model.vo.chat.UserUnreadMessagesVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户已读消息状态
 */
@RestController
@RequestMapping("/chatMessageRead")
@Tag(name = "用户已读消息状态API")
public class ChatMessageReadController {

    @Autowired
    private ChatMessageReadService chatMessageReadService;

    /**
     * 获取某个聊天室中所有用户的未读消息数
     *
     * @param roomId 聊天室ID
     * @return List<Long> 未读消息数
     */
    @Operation(summary = "获取某个聊天室中所有用户的未读消息数")
    @GetMapping("/unreadCountAll")
    public Result<Map<Long, Integer>> unreadCountAll(@Schema(description = "房间id") @RequestParam(value = "roomId") Long roomId) {
        return Result.ok(chatMessageReadService.getUnreadMessageCountByRoomId(roomId));
    }

    /**
     * 获取某个聊天室中某个用户的未读消息数
     *
     * @param roomId 聊天室ID
     * @param userId 用户
     * @return List<Long> 未读消息数
     */
    @Operation(summary = "获取某个聊天室中某个用户的未读消息数")
    @GetMapping("/unreadCountOne")
    public Result<Long> unreadCountOne(@Schema(description = "房间id") @RequestParam(value = "roomId") Long roomId,
                                                     @Schema(description = "用户id") @RequestParam(value = "userId") Long userId
    ) {
        return Result.ok(null);
    }

    /**
     * 获取用户在某个聊天室的最新未读消息ID
     *
     * @param roomId 聊天室ID
     * @param userId 用户ID
     * @return Long 最新未读消息ID
     */
    @Operation(summary = "获取用户在某个聊天室的最新未读消息ID")
    @GetMapping("/latestUnread")
    public Long getLatestUnreadMessageId(@Schema(description = "房间id") @RequestParam(value = "roomId") Long roomId,
                                         @Schema(description = "用户id") @RequestParam(value = "userId") Long userId) {
        return chatMessageReadService.getLatestUnreadMessageId(roomId, userId);
    }

    /**
     * 更新用户在聊天室的已读状态
     *
     * @param roomId       聊天室ID
     * @param userId       用户ID
     * @param lastReadTime 用户的最后读取时间
     * @return boolean 操作结果
     */
    @Operation(summary = "更新用户在聊天室的已读状态")
    @PostMapping("/updateReadStatus")
    public boolean updateReadStatus(@Schema(description = "房间id") @RequestParam(value = "roomId") Long roomId,
                                    @Schema(description = "用户id") @RequestParam(value = "userId") Long userId,
                                    @Schema(description = "最后阅读时间")  @RequestParam(value = "lastReadTime") LocalDateTime lastReadTime) {
        return chatMessageReadService.updateReadStatus(roomId, userId, lastReadTime);
    }


    @Operation(summary = "获取用户所有房间的未读消息数和最新消息")
    @GetMapping("/unreadByUserId")
    public Result<UserUnreadMessagesVO> getUnreadMessagesByUserId(@Parameter(description = "用户ID", required = true) @RequestParam(value = "userId") Long userId) {
        return Result.ok(chatMessageReadService.getUnreadMessagesByUser(userId));
    }

}