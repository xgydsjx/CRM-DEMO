package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.mapper.ActivityMapper;
import com.bjpowernode.crm.workbench.service.ActivtyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("activtyService")
public class ActivtyServiceImpl implements ActivtyService {
    @Autowired
    private ActivityMapper activityMapper;

    @Override
    public int saveCreateActivity(Activity activity) {
        return activityMapper.insertActivity(activity);
    }

    @Override
    public List<Activity> queryActivityByConditionForPage(Map<String, Object> map) {
        return activityMapper.selectActivityByConditionForPage(map);
    }

    @Override
    public int queryCountOfActivityByCondition(Map<String, Object> map) {
        return activityMapper.selectCountOfActivityByCondition(map);
    }

    @Override
    public int deleteActivityByID (String[] ids) {
        return activityMapper.deleteActivityByID(ids);
    }

    @Override
    public Activity queryActivityByID (String id) {
        return activityMapper.selectActivityByID(id);
    }

    @Override
    public int saveActivity (Activity activity) {
        return activityMapper.updateActivity(activity);
    }

    @Override
    public List<Activity> queryActivities () {
        return activityMapper.selectAllActivities();
    }

    @Override
    public int saveCreateActivityByList (List<Activity> activityList) {
        return activityMapper.insertActivityByList(activityList);
    }

    @Override
    public Activity queryActivityForDetailByID (String id) {
        return activityMapper.selectActivityForDetailById(id);
    }


}
