package com.ouc.aamanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ouc.aamanagement.entity.Schedule;

import java.util.List;

/**
 * 排课管理 Service 接口
 */
public interface ScheduleService extends IService<Schedule> {
    public List<String> checkScheduleConflict(Schedule schedule,Long excludeId);
}
