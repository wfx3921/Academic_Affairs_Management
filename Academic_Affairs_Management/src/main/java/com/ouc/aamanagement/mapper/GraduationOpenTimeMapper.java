package com.ouc.aamanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ouc.aamanagement.entity.GraduationOpenTime;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

@Mapper
public interface GraduationOpenTimeMapper extends BaseMapper<GraduationOpenTime> {
    @Insert("insert into graduation_open_time values(#{id},#{startTime},#{endTime})")
    public int insertTime(Integer id, Date startTime, Date endTime);
    @Update("update graduation_open_time set start_time=#{startTime},end_time=#{endTime} where id=#{id}")
    public int updateTime(Integer id, Date startTime, Date endTime);
}
