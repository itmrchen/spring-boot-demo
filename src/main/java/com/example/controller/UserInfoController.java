package com.example.controller;

import com.example.exception.LogicException;
import com.example.group.*;
import com.example.model.po.*;
import com.example.model.vo.*;
import com.example.params.Params;
import com.example.service.*;
import com.example.util.ModelUtil;
import io.jsonwebtoken.lang.Collections;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.model.vo.ResultVO.SUCCESS;

@RestController
@RequestMapping(value = "/userinfo", produces="application/json; charset=UTF-8")
@Api(tags="用户个人信息")
public class UserInfoController {

    @Value("${retrieve-password.max-retry}")
    private Integer maxRetry;
    @Autowired
    private IUserService userService;
    @Autowired
    private ITokenService tokenService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IDeptService deptService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IMailService mailService;

    @GetMapping
    @ApiOperation(value = "查询个人信息")
    public ResultVO<UserVO> currentUser() {
        User user = userService.currentUser();
        UserVO userVO = (UserVO) ModelUtil.copy(user,
                new ModelUtil.Mapping(User.class, UserVO.class, "password"),
                new ModelUtil.Mapping(Dept.class, DeptVO.class),
                new ModelUtil.Mapping(Role.class, RoleVO.class),
                new ModelUtil.Mapping(Resource.class, ResourceVO.class));
        return new ResultVO<>(SUCCESS, "", userVO);
    }

    @PostMapping
    @ApiOperation(value = "注册个人信息")
    public ResultVO save(@Validated({UserInfoInsert.class}) UserVO userVO) {
        User user = (User) ModelUtil.copy(userVO, new ModelUtil.Mapping(UserVO.class, User.class));
        String password = user.getPassword();
        if (!StringUtils.isEmpty(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }
        List<Dept> defaultDepts = deptService.customList(new Params<>(new Dept().setIsDefault(IDeptService.DEFAULT)));
        if (!Collections.isEmpty(defaultDepts)) {
            user.setDeptId(defaultDepts.get(0).getId());
        }
        List<Role> defaultRoles = roleService.customList(new Params<>(new Role().setIsDefault(IRoleService.DEFAULT)));
        userService.customSave(user.setRoles(defaultRoles));
        user = userService.customGetOne(new Params<>(new User().setUsername(user.getUsername())));
        // 自动登录
        TokenVO tokenVO = tokenService.saveToken(new UserDetailsImpl(user));
        return new ResultVO<>(SUCCESS, "注册个人信息成功！", tokenVO);
    }

    @PutMapping
    @ApiOperation(value = "更新个人信息")
    public ResultVO update(@Validated({UserInfoUpdate.class}) UserVO userVO) {
        User user = (User) ModelUtil.copy(userVO, new ModelUtil.Mapping(UserVO.class, User.class));
        user.setPassword(null);
        userService.updateById(user.setId(userService.currentUser().getId()));
        return new ResultVO<>(SUCCESS, "更新个人信息成功！", null);
    }

    @PatchMapping
    @ApiOperation(value = "修改密码")
    public ResultVO modpwd(@Validated({UserInfoModPwd.class}) UserVO userVO) {
        // 当前用户id
        Integer id = userService.currentUser().getId();
        // 查询当前用户
        User user = userService.getById(id);
        // 比较密码
        if (!passwordEncoder.matches(userVO.getPassword(), user.getPassword())) {
            throw new LogicException("原密码错误");
        }
        // 修改密码
        String newpassword = passwordEncoder.encode(userVO.getNewpassword());
        user.setPassword(newpassword);
        userService.updateById(user);
        return new ResultVO<>(SUCCESS, "修改密码成功！", null);
    }

    @PostMapping("/retrieve_password_mail")
    @ApiOperation(value = "发送找回密码邮件")
    public ResultVO retrievePasswordMail(@Validated({RetrievePasswordMail.class}) UserVO userVO) {
        User user = (User) ModelUtil.copy(userVO, new ModelUtil.Mapping(UserVO.class, User.class));
        Mail mail = userService.retrievePasswordMail(user);
        mail = mailService.send(mail.getId());
        mailService.send(mail, maxRetry);
        String emailAddr = mail.getToUsers().get(0).getEmail();
        String[] emailArr = emailAddr.split("@");
        String emailName = emailArr[0];
        String emailExt = emailArr[1];
        int length = emailName.length();
        String email_;
        if (length>3) {
            email_ = emailName.substring(0, 3) + "*@" + emailExt;
        } else {
            email_ = "*@" + emailExt;
        }
        return new ResultVO<>(SUCCESS, "验证码已发送至"+email_, null);
    }

    @PostMapping("/retrieve_password")
    @ApiOperation(value = "找回密码")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "verification", value = "验证码", required = true, dataTypeClass = String.class)
    })
    public ResultVO retrievePassword(@Validated({RetrievePassword.class}) UserVO userVO, String verification) {
        User user = (User) ModelUtil.copy(userVO, new ModelUtil.Mapping(UserVO.class, User.class));
        String password = user.getPassword();
        if (!StringUtils.isEmpty(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }
        userService.retrievePassword(user, verification);
        return new ResultVO<>(SUCCESS, "修改密码成功！", null);
    }

}
