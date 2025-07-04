package com.voracityrat.memehubbackend.mapper;

import com.voracityrat.memehubbackend.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author grey
 * @description 针对表【user(用户表)】的数据库操作Mapper
 * @createDate 2025-06-17 22:21:55
 * @Entity com.voracityrat.memehubbackend.model.entity.User
 */
public interface UserMapper extends BaseMapper<User> {

    List<User> getUserNameByIds(@Param("userIds") List<Long> userIds);
}




