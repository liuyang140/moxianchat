package com.ly.chat.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.model.entity.chat.ChatMessage;
import com.ly.model.vo.base.PageVo;
import com.ly.model.vo.chat.ChatMessageVo;


public interface ChatMessageService extends IService<ChatMessage> {

    void saveAndForward(JSONObject json);

    void recallMessage(JSONObject json);

    PageVo<ChatMessageVo> getHistoryMessage(Long roomId, Integer page, Integer size);
}
