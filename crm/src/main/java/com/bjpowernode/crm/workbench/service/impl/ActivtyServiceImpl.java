package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.mapper.ActivityMapper;
import com.bjpowernode.crm.workbench.service.ActivtyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("activtyService")
public class ActivtyServiceImpl implements ActivtyService {
    @Autowired
    private ActivityMapper activityMapper;
    @Override
    public int saveCreateActivty (Activity activity) {
        return activityMapper.insertActivity(activity);
    }
}
