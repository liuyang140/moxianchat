package com.ly.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.chat.mapper.ChatMessageMapper;
import com.ly.chat.mapper.ChatMessageReadMapper;
import com.ly.chat.mapper.ChatRoomMapper;
import com.ly.chat.mapper.ChatRoomUserMapper;
import com.ly.chat.service.ChatRoomService;
import com.ly.chat.service.ChatRoomUserService;
import com.ly.chat.utils.ClientUtils;
import com.ly.common.util.AuthContextHolder;
import com.ly.model.dto.chat.ChatMessageDTO;
import com.ly.model.dto.chat.UnreadCountDTO;
import com.ly.model.entity.chat.ChatMessage;
import com.ly.model.entity.chat.ChatRoom;
import com.ly.model.entity.chat.ChatRoomUser;
import com.ly.model.enums.ChatEventTypeEnum;
import com.ly.model.enums.ChatTypeEnum;
import com.ly.model.vo.chat.RoomVO;
import com.ly.model.vo.chat.UserRoomUnreadMessagesVO;
import com.ly.model.vo.customer.CustomerUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ChatRoomServiceImpl extends ServiceImpl<ChatRoomMapper, ChatRoom> implements ChatRoomService {


    @Autowired
    private ChatRoomUserService chatRoomUserService;

    @Autowired
    private ChatRoomUserMapper chatRoomUserMapper;

    @Autowired
    private ChatMessageReadMapper chatMessageReadMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ClientUtils clientUtils;

    /**
     * 创建（或复用）一个私聊房间，返回包含房间ID的匹配用户信息
     * @param userId 当前用户ID
     * @param target 目标用户VO（至少含 customerId 和距离）
     * @return 带房间ID的 MatchUserVo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CustomerUserVo createRoom(Long userId, CustomerUserVo target) {
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

    @Override
    public List<Long> getRoomUserIds(Long roomId) {
        return baseMapper.getRoomMemberIds(roomId);
    }

    @Override
    public UserRoomUnreadMessagesVO getUnreadMessagesByUser() {
        Long userId = AuthContextHolder.getUserId();
        ChatRoomMapper chatRoomMapper = this.getBaseMapper();
        // 获取用户加入的房间ID
        List<Long> roomIds = chatRoomMapper.getRoomIdsByUser(userId);

        // 获取每个房间的未读消息数
        List<UnreadCountDTO> unreadList = chatMessageReadMapper.countUnreadMessagesBatch(roomIds, userId);

        // 获取每个房间的最新消息
        List<ChatMessage> latestMessageList = chatMessageMapper.getLatestMessagesByRoomIds(roomIds);

        List<Long> userIds = chatRoomMapper.getRoomsMemberIdsByType(roomIds, ChatTypeEnum.PRIVATE.getValue());
        //用户信息
        Map<Long, CustomerUserVo> userVoMap = clientUtils.getBatchUserVos(userIds).stream().collect(Collectors.toMap(CustomerUserVo::getCustomerId, Function.identity()));
        Map<Long, CustomerUserVo> roomId2UserVoMap = chatRoomUserService.lambdaQuery().in(ChatRoomUser::getRoomId, roomIds)
                .list().stream()
                .filter(roomUser -> userVoMap.containsKey(roomUser.getUserId()))
                .collect(Collectors.toMap(ChatRoomUser::getRoomId, roomUser -> userVoMap.get(roomUser.getUserId()), (v1, v2) -> v1));

        // 获取每个房间的基础信息
        List<ChatRoom> roomList = chatRoomMapper.selectBatchIds(roomIds);

        // 整合数据
        Map<Long, Integer> unreadMap = unreadList.stream()
                .collect(Collectors.toMap(UnreadCountDTO::getRoomId, UnreadCountDTO::getUnreadCount));

        Map<Long, ChatMessage> messageMap = latestMessageList.stream()
                .collect(Collectors.toMap(ChatMessage::getRoomId, Function.identity()));

        Map<Long, ChatRoom> roomMap = roomList.stream()
                .collect(Collectors.toMap(ChatRoom::getId, Function.identity()));

        List<RoomVO> roomVOList = roomIds.stream().map(roomId -> {
            RoomVO vo = new RoomVO();
            vo.setRoomId(roomId);
            ChatRoom room = roomMap.get(roomId);
            if (room != null) {
                if (ChatTypeEnum.GROUP.getValue().intValue() == room.getType().intValue()) {
                    vo.setRoomName(room.getName());
                }else{
                    CustomerUserVo customerUserVo = roomId2UserVoMap.getOrDefault(roomId, new CustomerUserVo().setNickname("郭亚东"));
                    vo.setRoomName(customerUserVo.getNickname());
                }
                vo.setType(room.getType());
            }
            vo.setUnreadCount(unreadMap.getOrDefault(roomId, 0));
            ChatMessage msg = messageMap.get(roomId);
            if (msg != null) {
                vo.setLatestMessage(ChatMessageDTO.buildDTO(msg, ChatEventTypeEnum.UNREAD_PUSH.getValue()));
            }
            return vo;
        }).collect(Collectors.toList());

        UserRoomUnreadMessagesVO result = new UserRoomUnreadMessagesVO();
        result.setUserId(userId);
        result.setRooms(roomVOList);
        return result;
    }

}