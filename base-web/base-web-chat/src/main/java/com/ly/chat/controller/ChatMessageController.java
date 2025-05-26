package com.ly.chat.controller;

import com.ly.chat.service.ChatMessageService;
import com.ly.common.result.Result;
import com.ly.model.dto.chat.ChatMessageDTO;
import com.ly.model.dto.chat.UpdateMessageDTO;
import com.ly.model.vo.base.PageVo;
import com.ly.model.vo.chat.ChatMessageVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "消息API接口管理（chat-api）")
@RestController
@RequestMapping("/chatMessage")
public class ChatMessageController {
    @Autowired
    private ChatMessageService chatMessageService;

    @Operation(summary = "获取历史消息")
    @GetMapping("/history")
    public Result<PageVo<ChatMessageVo>> getHistoryMessages(
            @Schema(description = "房间id", name = "roomId") @RequestParam(value = "roomId", required = true) Long roomId,
            @Schema(description = "页码", name = "page") @RequestParam(value = "page", defaultValue = "1",required = false) Integer page,
            @Schema(description = "条数", name = "size") @RequestParam(value = "size", defaultValue = "20",required = false) Integer size
    ) {
        PageVo<ChatMessageVo> pageVo =  chatMessageService.getHistoryMessage(roomId, page, size);
        return Result.ok(pageVo);
    }

    @Operation(summary = "批量持久化消息，后台使用")
    @PostMapping("/saveMessages")
    public Result saveMessages(@RequestBody UpdateMessageDTO dto) {
        chatMessageService.saveMessages(dto);
        return Result.ok();
    }

    @Operation(summary = "批量撤回消息，后台使用")
    @PostMapping("/recallMessages")
    public Result<List<ChatMessageDTO>> recallMessages(@RequestBody UpdateMessageDTO dto) {
        return Result.ok(chatMessageService.recallMessages(dto));
    }





}
