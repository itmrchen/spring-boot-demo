package com.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.group.MailInsert;
import com.example.group.MailUpdate;
import com.example.model.po.Mail;
import com.example.model.vo.MailVO;
import com.example.model.vo.ResultVO;
import com.example.params.Params;
import com.example.service.IMailService;
import com.example.util.ModelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.example.model.vo.ResultVO.SUCCESS;

@RestController
@RequestMapping(value = "/mail", produces = "application/json; charset=UTF-8")
@Api(tags = "邮件")
@Validated
public class MailController {

    @Value("${mail.max-retry}")
    private Integer maxRetry;
    @Autowired
    private IMailService mailService;

    @GetMapping("/{current}/{size}")
    @ApiOperation(value = "查询邮件列表")
    public ResultVO findPage(@PathVariable @NotNull(message = "当前页不能为空") @ApiParam(value = "当前页", defaultValue = "1", required = true) long current,
                         @PathVariable @NotNull(message = "每页显示条数不能为空") @ApiParam(value = "每页显示条数", defaultValue = "10", required = true) long size,
                         MailVO mailVO) {
        Page<Mail> page = new Page<>(current, size);
        Params<Mail> params = new Params<>(mailVO);
        IPage<Mail> iPage = mailService.customPage(page, params);
        IPage mails = (IPage) ModelUtil.copy(iPage, new ModelUtil.Mapping(Mail.class, MailVO.class));
        return new ResultVO<>(SUCCESS, "", mails);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询邮件")
    public ResultVO<MailVO> findById(@PathVariable @NotNull(message = "邮件id不能为空") @ApiParam(value = "邮件id", required = true) Integer id) {
        Mail mail = mailService.customGetById(id);
        MailVO mailVO = (MailVO) ModelUtil.copy(mail, new ModelUtil.Mapping(Mail.class, MailVO.class));
        return new ResultVO<>(SUCCESS, "", mailVO);
    }

    @PostMapping
    @ApiOperation(value = "保存邮件")
    public ResultVO<MailVO> save(@Validated({MailInsert.class}) @RequestBody MailVO mailVO) {
        Mail mail = (Mail) ModelUtil.copy(mailVO, new ModelUtil.Mapping(MailVO.class, Mail.class));
        mailService.customSave(mail);
        return new ResultVO<>(SUCCESS, "保存邮件成功！", (MailVO) ModelUtil.copy(mail, new ModelUtil.Mapping(Mail.class, MailVO.class)));
    }

    @PutMapping
    @ApiOperation(value = "更新邮件")
    public ResultVO<MailVO> update(@Validated({MailUpdate.class}) @RequestBody MailVO mailVO) {
        Mail mail = (Mail) ModelUtil.copy(mailVO, new ModelUtil.Mapping(MailVO.class, Mail.class));
        mailService.customUpdateById(mail);
        return new ResultVO<>(SUCCESS, "更新邮件成功！", (MailVO) ModelUtil.copy(mail, new ModelUtil.Mapping(Mail.class, MailVO.class)));
    }

    @PatchMapping("/{id}")
    @ApiOperation(value = "发送邮件")
    public ResultVO send(
            @PathVariable
            @NotNull(message = "邮件id不能为空")
            @ApiParam(value = "邮件id", required = true) Integer id) {
        Mail mail = mailService.send(id);
        mailService.send(mail, maxRetry);
        return new ResultVO<>(SUCCESS, "发送邮件成功！", null);
    }

    @DeleteMapping("/{ids}")
    @ApiOperation(value = "删除邮件")
    public ResultVO delete(
            @PathVariable
            @NotNull(message = "邮件id不能为空")
            @NotEmpty(message = "邮件id不能为空")
            @ApiParam(value = "邮件id，多个用逗号分隔", required = true) List<Integer> ids) {
        mailService.removeByIds(ids);
        return new ResultVO<>(SUCCESS, "删除邮件成功！", null);
    }

}
