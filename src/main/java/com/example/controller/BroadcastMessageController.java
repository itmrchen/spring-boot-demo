package com.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.exception.LogicException;
import com.example.group.BroadcastInsert;
import com.example.kafka.DefaultProducer;
import com.example.model.po.BroadcastMessage;
import com.example.model.po.BroadcastToUserLink;
import com.example.model.po.User;
import com.example.model.vo.BroadcastMessageVO;
import com.example.model.vo.ResultVO;
import com.example.model.vo.UserVO;
import com.example.params.Params;
import com.example.service.IBroadcastMessageService;
import com.example.service.IBroadcastToUserLinkService;
import com.example.util.ModelUtil;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.example.config.KafkaConfig.BROADCAST_TOPIC;
import static com.example.model.vo.ResultVO.SUCCESS;
import static com.example.service.IBroadcastMessageService.UNREAD;

@RestController
@RequestMapping(value = "/broadcast", produces = "application/json; charset=UTF-8")
@Api(tags = "广播")
@Validated
public class BroadcastMessageController {

    @Autowired
    private IBroadcastMessageService broadcastMessageService;
    @Autowired
    private IBroadcastToUserLinkService broadcastToUserLinkService;
    @Autowired
    private DefaultProducer defaultProducer;

    @GetMapping("/{current}/{size}")
    @ApiOperation(value = "查询广播列表")
    public ResultVO findPage(@PathVariable @NotNull(message = "当前页不能为空") @ApiParam(value = "当前页", defaultValue = "1", required = true) long current,
                         @PathVariable @NotNull(message = "每页显示条数不能为空") @ApiParam(value = "每页显示条数", defaultValue = "10", required = true) long size,
                         BroadcastMessageVO broadcastMessageVO) {
        Page<BroadcastMessage> page = new Page<>(current, size);
        Params<BroadcastMessage> params = new Params<>(broadcastMessageVO);
        IPage<BroadcastMessage> iPage = broadcastMessageService.customPage(page, params);
        IPage messages = (IPage) ModelUtil.copy(iPage,
                new ModelUtil.Mapping(BroadcastMessage.class, BroadcastMessageVO.class),
                new ModelUtil.Mapping(User.class, UserVO.class, "password"));
        return new ResultVO<>(SUCCESS, "", messages);
    }

    @GetMapping("/self/{current}/{size}")
    @ApiOperation(value = "查询当前用户收到的广播列表")
    @SuppressWarnings("unchecked")
    public ResultVO findSelfPage(@PathVariable @NotNull(message = "当前页不能为空") @ApiParam(value = "当前页", defaultValue = "1", required = true) long current,
                             @PathVariable @NotNull(message = "每页显示条数不能为空") @ApiParam(value = "每页显示条数", defaultValue = "10", required = true) long size,
                             BroadcastMessageVO broadcastMessageVO) {
        User currentUser = broadcastMessageService.currentUser();
        if (currentUser==null) {
            throw new LogicException("获取当前用户失败");
        }
        Page<BroadcastMessage> page = new Page<>(current, size);
        Params<BroadcastMessage> params = new Params<>(broadcastMessageVO);
        params.put("currentUserId", currentUser.getId());
        IPage<BroadcastMessage> iPage = broadcastMessageService.selfPage(page, params);
        IPage messages = (IPage) ModelUtil.copy(iPage,
                new ModelUtil.Mapping(BroadcastMessage.class, BroadcastMessageVO.class),
                new ModelUtil.Mapping(User.class, UserVO.class, "password"));
        if (messages!=null) {
            messages.getRecords().forEach(o -> {
                BroadcastMessageVO message = (BroadcastMessageVO) o;
                BroadcastToUserLink link = message.getLinks().get(0);
                message.setReadStatus(link.getReadStatus());
            });
        }
        return new ResultVO<>(SUCCESS, "", messages);
    }

    @GetMapping("/self/unread")
    @ApiOperation(value = "查询当前用户的未读广播")
    public ResultVO findSelfPage() {
        User currentUser = broadcastMessageService.currentUser();
        if (currentUser==null) {
            throw new LogicException("获取当前用户失败");
        }
        Params<BroadcastMessage> params = new Params<>(new BroadcastMessageVO().setReadStatus(UNREAD));
        params.put("currentUserId", currentUser.getId());
        List<BroadcastMessage> list = broadcastMessageService.selfList(params);
        List messages = (List) ModelUtil.copy(list,
                new ModelUtil.Mapping(BroadcastMessage.class, BroadcastMessageVO.class),
                new ModelUtil.Mapping(User.class, UserVO.class, "password"));
        return new ResultVO<>(SUCCESS, "", messages);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询广播")
    public ResultVO<BroadcastMessageVO> findById(@PathVariable @NotNull(message = "广播id不能为空") @ApiParam(value = "广播id", required = true) Integer id) {
        BroadcastMessage broadcastMessage = broadcastMessageService.customGetById(id);
        BroadcastMessageVO broadcastMessageVO = (BroadcastMessageVO) ModelUtil.copy(broadcastMessage,
                new ModelUtil.Mapping(BroadcastMessage.class, BroadcastMessageVO.class),
                new ModelUtil.Mapping(User.class, UserVO.class, "password"));
        return new ResultVO<>(SUCCESS, "", broadcastMessageVO);
    }

    @GetMapping("/self/{id}")
    @ApiOperation(value = "根据id查询当前用户收到的广播")
    public ResultVO<BroadcastMessageVO> findSelfById(@PathVariable @NotNull(message = "广播id不能为空") @ApiParam(value = "广播id", required = true) Integer id) {
        User currentUser = broadcastMessageService.currentUser();
        if (currentUser==null) {
            throw new LogicException("获取当前用户失败");
        }
        Params<BroadcastMessage> params = new Params<>(new BroadcastMessage());
        params.put("currentUserId", currentUser.getId());
        BroadcastMessage broadcastMessage = broadcastMessageService.getSelfById(id, params);
        BroadcastMessageVO broadcastMessageVO = (BroadcastMessageVO) ModelUtil.copy(broadcastMessage,
                new ModelUtil.Mapping(BroadcastMessage.class, BroadcastMessageVO.class),
                new ModelUtil.Mapping(User.class, UserVO.class, "password"));
        return new ResultVO<>(SUCCESS, "", broadcastMessageVO);
    }

    @DeleteMapping("/{ids}")
    @ApiOperation(value = "删除广播")
    public ResultVO delete(
            @PathVariable
            @NotNull(message = "广播id不能为空")
            @NotEmpty(message = "广播id不能为空")
            @ApiParam(value = "广播id，多个用逗号分隔", required = true) List<Integer> ids) {
        broadcastMessageService.removeByIds(ids);
        return new ResultVO<>(SUCCESS, "删除广播成功！", null);
    }

    @PostMapping
    @ApiOperation(value = "发送广播")
    public ResultVO save(@Validated({BroadcastInsert.class}) @RequestBody BroadcastMessageVO broadcastMessageVO) {
        BroadcastMessage broadcastMessage = (BroadcastMessage) ModelUtil.copy(broadcastMessageVO,
                new ModelUtil.Mapping(BroadcastMessageVO.class, BroadcastMessage.class));
        broadcastMessageService.customSave(broadcastMessage);
        defaultProducer.send(BROADCAST_TOPIC, broadcastMessage);
        return new ResultVO<>(SUCCESS, "广播成功！", null);
    }

    @PutMapping("/{ids}")
    @ApiOperation(value = "更新消息状态为已读")
    public ResultVO read(
            @PathVariable
            @NotNull(message = "广播id不能为空")
            @NotEmpty(message = "广播id不能为空")
            @ApiParam(value = "广播id，多个用逗号分隔", required = true) List<Integer> ids) {
        User currentUser = broadcastMessageService.currentUser();
        if (currentUser==null) {
            throw new LogicException("获取当前用户失败");
        }
        List<BroadcastToUserLink> links = Lists.newArrayList();
        ids.forEach(id -> {
            BroadcastToUserLink link = new BroadcastToUserLink(id, currentUser.getId(), IBroadcastMessageService.READ);
            links.add(link);
        });
        broadcastToUserLinkService.customUpdateBatchById(links);
        return new ResultVO<>(SUCCESS, "更新消息状态成功！", null);
    }

}
