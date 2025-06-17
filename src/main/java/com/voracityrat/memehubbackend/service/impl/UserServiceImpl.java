package com.voracityrat.memehubbackend.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.voracityrat.memehubbackend.exception.BusinessException;
import com.voracityrat.memehubbackend.exception.ErrorCode;
import com.voracityrat.memehubbackend.model.entity.User;
import com.voracityrat.memehubbackend.model.enums.UserRoleEnum;
import com.voracityrat.memehubbackend.service.UserService;
import com.voracityrat.memehubbackend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * @author grey
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2025-06-17 22:21:55
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {

        //校验参数不为空
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        //校验账户长度大于等于4小于等于20
        if (userAccount.length() < 4 || userAccount.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户长度应为4-20之间");
        }
        //校验密码长度大于等于8 小于等于20
        if (userPassword.length() < 8 || userPassword.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度应为8-20之间");
        }
        //校验密码和确认密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        //校验账号唯一   （查表）
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUserAccount, userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户已存在");
        }
        //密码加密
        String encryptPassword = this.getEncryptPassword(userPassword);
        //组装用户注册数据 插入数据库
        User registerUser = new User();
        registerUser.setUserAccount(userAccount);
        registerUser.setUserPassword(encryptPassword);
        registerUser.setUserRole(UserRoleEnum.COMMON_USER.getValue());
        registerUser.setUserName("无名");
        //TODO 可以给用户设置默认头像
        registerUser.setUserAvatar("");
        boolean saveResult = this.save(registerUser);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户注册失败");
        }
        //返回用户主键
        return registerUser.getId();
    }


    @Override
    public String getEncryptPassword(String userPassword) {
        final String salt = "Ciallo～(∠・ω< )⌒★";
        return DigestUtils.md5DigestAsHex((salt + userPassword).getBytes());
    }
}




