package com.ly.chat.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.model.dto.chat.ChatMessageDTO;
import com.ly.model.dto.chat.UpdateMessageDTO;
import com.ly.model.entity.chat.ChatMessage;
import com.ly.model.vo.base.PageVo;
import com.ly.model.vo.chat.ChatMessageVo;

import java.util.List;


public interface ChatMessageService extends IService<ChatMessage> {

    void saveMessages(UpdateMessageDTO uDto);

    List<ChatMessageDTO> recallMessages(UpdateMessageDTO uDto);

    PageVo<ChatMessageVo> getHistoryMessage(Long roomId, Integer page, Integer size);

    List<Long> getRoomUserIds(Long roomId);
}
