package com.ly.model.dto.chat;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class UpdateMessageDTO {

    private List<ChatMessageDTO> chatMessageDTOList;
}
