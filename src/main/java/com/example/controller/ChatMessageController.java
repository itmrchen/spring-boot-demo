package com.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.exception.LogicException;
import com.example.group.Insert;
import com.example.kafka.DefaultProducer;
import com.example.model.po.ChatMessage;
import com.example.model.po.User;
import com.example.model.vo.ChatMessageVO;
import com.example.model.vo.ResultVO;
import com.example.model.vo.UserVO;
import com.example.params.Params;
import com.example.service.IChatMessageService;
import com.example.util.ModelUtil;
import io.jsonwebtoken.lang.Collections;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.example.config.KafkaConfig.CHAT_TOPIC;
import static com.example.model.vo.ResultVO.SUCCESS;
import static com.example.service.IChatMessageService.READ;
import static com.example.service.IChatMessageService.UNREAD;

@RestController
@RequestMapping(value = "/chat", produces = "application/json; charset=UTF-8")
@Api(tags = "点对点消息")
@Validated
public class ChatMessageController {

    @Autowired
    private IChatMessageService chatMessageService;
    @Autowired
    private DefaultProducer defaultProducer;

    @GetMapping("/{current}/{size}")
    @ApiOperation(value = "查询消息列表")
    public ResultVO findPage(@PathVariable @NotNull(message = "当前页不能为空") @ApiParam(value = "当前页", defaultValue = "1", required = true) long current,
                         @PathVariable @NotNull(message = "每页显示条数不能为空") @ApiParam(value = "每页显示条数", defaultValue = "10", required = true) long size,
                         ChatMessageVO chatMessageVO) {
        Page<ChatMessage> page = new Page<>(current, size);
        Params<ChatMessage> params = new Params<>(chatMessageVO);
        IPage<ChatMessage> iPage = chatMessageService.customPage(page, params);
        IPage messages = (IPage) ModelUtil.copy(iPage,
                new ModelUtil.Mapping(ChatMessage.class, ChatMessageVO.class),
                new ModelUtil.Mapping(User.class, UserVO.class, "password"));
        return new ResultVO<>(SUCCESS, "", messages);
    }

    @GetMapping("/self/{current}/{size}")
    @ApiOperation(value = "查询当前用户收到的消息列表")
    public ResultVO findSelfPage(@PathVariable @NotNull(message = "当前页不能为空") @ApiParam(value = "当前页", defaultValue = "1", required = true) long current,
                             @PathVariable @NotNull(message = "每页显示条数不能为空") @ApiParam(value = "每页显示条数", defaultValue = "10", required = true) long size,
                             ChatMessageVO chatMessageVO) {
        User currentUser = chatMessageService.currentUser();
        if (currentUser==null) {
            throw new LogicException("获取当前用户失败");
        }
        chatMessageVO.setToUserId(currentUser.getId());
        Page<ChatMessage> page = new Page<>(current, size);
        Params<ChatMessage> params = new Params<>(chatMessageVO);
        IPage<ChatMessage> iPage = chatMessageService.customPage(page, params);
        IPage messages = (IPage) ModelUtil.copy(iPage,
                new ModelUtil.Mapping(ChatMessage.class, ChatMessageVO.class),
                new ModelUtil.Mapping(User.class, UserVO.class, "password"));
        return new ResultVO<>(SUCCESS, "", messages);
    }

    @GetMapping("/self/unread")
    @ApiOperation(value = "查询当前用户的未读消息")
    public ResultVO findSelfUnread() {
        User currentUser = chatMessageService.currentUser();
        if (currentUser==null) {
            throw new LogicException("获取当前用户失败");
        }
        Params<ChatMessage> params = new Params<>(new ChatMessage().setToUserId(currentUser.getId()).setReadStatus(UNREAD));
        List<ChatMessage> list = chatMessageService.customList(params);
        List messages = (List) ModelUtil.copy(list,
                new ModelUtil.Mapping(ChatMessage.class, ChatMessageVO.class),
                new ModelUtil.Mapping(User.class, UserVO.class, "password"));
        return new ResultVO<>(SUCCESS, "", messages);
    }

    @GetMapping("/{userId}/{startId}/{current}/{size}")
    @ApiOperation(value = "查询指定消息id之前的往来消息")
    public ResultVO contacts(@PathVariable @NotNull(message = "用户id不能为空") @ApiParam(value = "用户id", required = true) Integer userId,
                             @PathVariable @NotNull(message = "起始消息id不能为空") @ApiParam(value = "起始消息id", required = true) Integer startId,
                             @PathVariable @NotNull(message = "当前页不能为空") @ApiParam(value = "当前页", defaultValue = "1", required = true) long current,
                             @PathVariable @NotNull(message = "每页显示条数不能为空") @ApiParam(value = "每页显示条数", defaultValue = "10", required = true) long size,
                             ChatMessageVO chatMessageVO) {
        User currentUser = chatMessageService.currentUser();
        if (currentUser==null) {
            throw new LogicException("获取当前用户失败");
        }
        Page<ChatMessage> page = new Page<>(current, size);
        Params<ChatMessage> params = new Params<>(chatMessageVO);
        if (startId!=null) {
            params.put("startId", startId);
        }
        params.put("currentUserId", currentUser.getId());
        params.put("userId", userId);
        IPage<ChatMessage> iPage = chatMessageService.customPage(page, params);
        IPage messages = (IPage) ModelUtil.copy(iPage,
                new ModelUtil.Mapping(ChatMessage.class, ChatMessageVO.class),
                new ModelUtil.Mapping(User.class, UserVO.class, "password"));
        return new ResultVO<>(SUCCESS, "", messages);
    }


    @GetMapping("/{userId}/{current}/{size}")
    @ApiOperation(value = "查询往来消息")
    public ResultVO contacts(@PathVariable @NotNull(message = "用户id不能为空") @ApiParam(value = "用户id", required = true) Integer userId,
                             @PathVariable @NotNull(message = "当前页不能为空") @ApiParam(value = "当前页", defaultValue = "1", required = true) long current,
                             @PathVariable @NotNull(message = "每页显示条数不能为空") @ApiParam(value = "每页显示条数", defaultValue = "10", required = true) long size,
                             ChatMessageVO chatMessageVO) {
        User currentUser = chatMessageService.currentUser();
        if (currentUser==null) {
            throw new LogicException("获取当前用户失败");
        }
        Page<ChatMessage> page = new Page<>(current, size);
        Params<ChatMessage> params = new Params<>(chatMessageVO);
        params.put("currentUserId", currentUser.getId());
        params.put("userId", userId);
        IPage<ChatMessage> iPage = chatMessageService.customPage(page, params);
        IPage messages = (IPage) ModelUtil.copy(iPage,
                new ModelUtil.Mapping(ChatMessage.class, ChatMessageVO.class),
                new ModelUtil.Mapping(User.class, UserVO.class, "password"));
        return new ResultVO<>(SUCCESS, "", messages);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询消息")
    public ResultVO<ChatMessageVO> findById(@PathVariable @NotNull(message = "消息id不能为空") @ApiParam(value = "消息id", required = true) Integer id) {
        ChatMessage chatMessage = chatMessageService.customGetById(id);
        ChatMessageVO chatMessageVO = (ChatMessageVO) ModelUtil.copy(chatMessage,
                new ModelUtil.Mapping(ChatMessage.class, ChatMessageVO.class),
                new ModelUtil.Mapping(User.class, UserVO.class, "password"));
        return new ResultVO<>(SUCCESS, "", chatMessageVO);
    }

    @GetMapping("/self/{id}")
    @ApiOperation(value = "根据id查询当前用户收到的消息")
    public ResultVO<ChatMessageVO> findSelfById(@PathVariable @NotNull(message = "消息id不能为空") @ApiParam(value = "消息id", required = true) Integer id) {
        User currentUser = chatMessageService.currentUser();
        if (currentUser==null) {
            throw new LogicException("获取当前用户失败");
        }
        Params<ChatMessage> params = new Params<>(new ChatMessage().setToUserId(currentUser.getId()));
        ChatMessage chatMessage = chatMessageService.getSelfById(id, params);
        ChatMessageVO chatMessageVO = (ChatMessageVO) ModelUtil.copy(chatMessage,
                new ModelUtil.Mapping(ChatMessage.class, ChatMessageVO.class),
                new ModelUtil.Mapping(User.class, UserVO.class, "password"));
        return new ResultVO<>(SUCCESS, "", chatMessageVO);
    }

    @PostMapping
    @ApiOperation(value = "发送消息")
    public ResultVO save(@Validated({Insert.class}) ChatMessageVO chatMessageVO) {
        User currentUser = chatMessageService.currentUser();
        if (currentUser==null) {
            throw new LogicException("获取当前用户失败");
        }
        ChatMessage chatMessage = (ChatMessage) ModelUtil.copy(chatMessageVO, new ModelUtil.Mapping(ChatMessageVO.class, ChatMessage.class));
        long now = System.currentTimeMillis();
        chatMessage.setSendTime(now);
        chatMessage.setSendUser(currentUser);
        chatMessage.setSendUserId(currentUser.getId());
        chatMessage.setReadStatus(UNREAD);
        chatMessageService.save(chatMessage);
        defaultProducer.send(CHAT_TOPIC, chatMessage);
        return new ResultVO<>(SUCCESS, "发送消息成功！", null);
    }

    @PutMapping("/{ids}")
    @ApiOperation(value = "更新消息状态为已读")
    public ResultVO update(
            @PathVariable
            @NotNull(message = "消息id不能为空")
            @NotEmpty(message = "消息id不能为空")
            @ApiParam(value = "消息id，多个用逗号分隔", required = true) List<Integer> ids) {
        User currentUser = chatMessageService.currentUser();
        if (currentUser==null) {
            throw new LogicException("获取当前用户失败");
        }
        Params<ChatMessage> params = new Params<>(new ChatMessage().setToUserId(currentUser.getId()))
                .put("ids", ids);
        List<ChatMessage> chatMessages = chatMessageService.customList(params);
        if (!Collections.isEmpty(chatMessages)) {
            chatMessages.forEach(chatMessage -> chatMessage.setReadStatus(READ));
            chatMessageService.updateBatchById(chatMessages);
        }
        return new ResultVO<>(SUCCESS, "更新消息状态成功！", null);
    }

}
