package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivtyService {
    int saveCreateActivity(Activity activity);

    List<Activity> queryActivityByConditionForPage(Map<String,Object> map);

    int queryCountOfActivityByCondition(Map<String,Object> map);
    //删除市场活动
    int deleteActivityByID(String[] ids);
    //修改市场活动
    Activity queryActivityByID(String id);

    /**
     * 更新市场活动
     */
    int saveActivity(Activity activity);

}
