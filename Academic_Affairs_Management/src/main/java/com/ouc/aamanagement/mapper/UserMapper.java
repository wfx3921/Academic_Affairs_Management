package com.ouc.aamanagement.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ouc.aamanagement.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT user_id FROM user WHERE user_name = #{userName}")
    Long findIdByUserName(@Param("userName") String userName);
}
