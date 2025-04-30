package com.ly.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.chat.mapper.ChatRoomMapper;
import com.ly.chat.mapper.ChatRoomUserMapper;
import com.ly.chat.service.ChatRoomService;
import com.ly.chat.service.ChatRoomUserService;
import com.ly.model.entity.chat.ChatRoom;
import com.ly.model.entity.chat.ChatRoomUser;
import com.ly.model.enums.ChatTypeEnum;
import com.ly.model.vo.customer.MatchUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ChatRoomServiceImpl extends ServiceImpl<ChatRoomMapper, ChatRoom> implements ChatRoomService {


    @Autowired
    private ChatRoomUserService chatRoomUserService;

    @Autowired
    private ChatRoomUserMapper chatRoomUserMapper;

    /**
     * 创建（或复用）一个私聊房间，返回包含房间ID的匹配用户信息
     * @param userId 当前用户ID
     * @param target 目标用户VO（至少含 customerId 和距离）
     * @return 带房间ID的 MatchUserVo
     */
    public MatchUserVo createRoom(Long userId, MatchUserVo target) {
        Long targetId = target.getCustomerId();

        // 1. 查找是否已经存在一个 "private" 聊天室，并且这两个用户都在
        List<Long> commonRoomIds = chatRoomUserMapper.selectRoomsByUsersAndType(Arrays.asList(userId, targetId),  ChatTypeEnum.PRIVATE.getValue());

        ChatRoom room = null;
        if (!commonRoomIds.isEmpty()) {
            // 2. 查询这些房间中有没有 type=private 的
            room = this.lambdaQuery()
                    .in(ChatRoom::getId, commonRoomIds)
                    .eq(ChatRoom::getType, ChatTypeEnum.PRIVATE.getValue())
                    .last("LIMIT 1")
                    .one();
        }

        if (room == null) {
            // 3. 没有，创建新的聊天室
            room = new ChatRoom();
            room.setType(ChatTypeEnum.PRIVATE.getValue());
            room.setName(null);
            this.save(room);

            // 4. 插入两条聊天室成员记录
            ChatRoomUser user1 = new ChatRoomUser();
            user1.setRoomId(room.getId());
            user1.setUserId(userId);
            user1.setIsOwner(true);

            ChatRoomUser user2 = new ChatRoomUser();
            user2.setRoomId(room.getId());
            user2.setUserId(targetId);
            user2.setIsOwner(false);

            chatRoomUserService.saveBatch(Arrays.asList(user1, user2));
        }

        // 5. 返回
        target.setRoomId(room.getId());
        return target;
    }

}