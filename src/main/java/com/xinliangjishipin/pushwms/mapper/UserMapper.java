package com.xinliangjishipin.pushwms.mapper;

import com.xinliangjishipin.pushwms.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author gengbeijun
 */
@Mapper
public interface UserMapper {
    /**
     * @param  userName, password
     * @return user
     */
    User getUser(@Param(value = "userName") String userName, @Param(value = "password") String password);
}
