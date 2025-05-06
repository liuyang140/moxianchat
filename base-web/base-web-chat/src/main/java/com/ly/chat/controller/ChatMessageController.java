package com.ly.chat.controller;

import com.ly.chat.service.ChatMessageService;
import com.ly.model.vo.base.PageVo;
import com.ly.model.vo.chat.ChatMessageVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "消息API接口管理")
@RestController
@RequestMapping("/chatMessage")
public class ChatMessageController {
    @Autowired
    private ChatMessageService chatMessageService;

    @Operation(summary = "获取历史消息")
    @GetMapping("/history")
    public ResponseEntity<PageVo<ChatMessageVo>> getHistoryMessages(
            @Schema(description = "房间id", name = "roomId") @RequestParam(value = "roomId", required = true) Long roomId,
            @Schema(description = "页码", name = "page") @RequestParam(value = "page", defaultValue = "1",required = false) Integer page,
            @Schema(description = "条数", name = "size") @RequestParam(value = "size", defaultValue = "20",required = false) Integer size
    ) {
        PageVo<ChatMessageVo> pageVo =  chatMessageService.getHistoryMessage(roomId, page, size);
        return ResponseEntity.ok(pageVo);
    }
}
