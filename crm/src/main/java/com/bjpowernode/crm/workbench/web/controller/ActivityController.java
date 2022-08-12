package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.HSSFUtils;
import com.bjpowernode.crm.commons.utils.UUIDutils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.mapper.ActivityMapper;
import com.bjpowernode.crm.workbench.service.ActivityRemarkService;
import com.bjpowernode.crm.workbench.service.ActivtyService;
import com.sun.deploy.net.HttpResponse;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

@Controller
public class ActivityController {

    @Autowired
    private UserService userService;
    @Autowired
    private ActivtyService activtyService;
    @Autowired
    private  ActivityRemarkService activityRemarkService;


    @RequestMapping("/workbench/activity/index.do")
    public String index(HttpServletRequest request){
        //调用service层方法，查询所有的用户
        List<User> userList=userService.queryAllUsers();
        //把数据保存到request中
        request.setAttribute("userList",userList);
        //请求转发到市场活动的主页面
        return "workbench/activity/index";
    }

    @RequestMapping("/workbench/activity/saveCreateActivity.do")
    @ResponseBody
    public Object  saveCreateActivty(Activity activity, HttpSession session){
        User  user =(User) session.getAttribute(Contants.SESSION_USER);
        //封装参数
        activity.setId(UUIDutils.getUUID());
        activity.setCreateTime(DateUtils.formateDateTime(new Date()));
        activity.setCreateBy(user.getId());

        ReturnObject returnObject=new ReturnObject();
        try {
            //调用service层方法，保存创建的市场活动
            int ret = activtyService.saveCreateActivity(activity);

            if(ret>0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            }else{
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙,请稍后重试....");
            }
        }catch (Exception e){
            e.printStackTrace();

            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙,请稍后重试....");
        }

        return returnObject;

    }
    @RequestMapping("/workbench/activity/queryActivityByConditionForPage.do")
    public @ResponseBody Object queryActivityByConditionForPage(String name,String owner,String startDate,String endDate,
                                                                int pageNo,int pageSize){
        //封装参数
        Map<String,Object> map=new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("beginNo",(pageNo-1)*pageSize);
        map.put("pageSize",pageSize);
        //调用service层方法，查询数据
        List<Activity> activityList=activtyService.queryActivityByConditionForPage(map);
        int totalRows=activtyService.queryCountOfActivityByCondition(map);
        //根据查询结果结果，生成响应信息
        Map<String,Object> retMap=new HashMap<>();
        retMap.put("activityList",activityList);
        retMap.put("totalRows",totalRows);
        return retMap;
    }
    @RequestMapping("/workbench/activity/deleteActivityByID.do")
    @ResponseBody
    public Object deleteActivityByID(String[] id){
        ReturnObject returnObject =new ReturnObject();
        try{
        int result= activtyService.deleteActivityByID(id);
        if (result>0){
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
        }else{
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        return  returnObject;
    }


    @RequestMapping("workbench/activity/queryActivityByID.do")
    @ResponseBody
    public Object queryActivityByID(String id){
        Activity activity = activtyService.queryActivityByID(id);
        return activity;
    }

    @RequestMapping("workbench/activity/saveActivity.do")
    @ResponseBody
    public Object saveActivity(Activity activity,HttpSession session){
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        activity.setEditTime(DateUtils.formateDateTime(new Date()));
        activity.setEditBy(user.getId());
        ReturnObject returnObject=new ReturnObject();
        try {
            int result = activtyService.saveActivity(activity);
            if (result>0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setMessage("更新成功");
            }else {
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试。。。。");
            }

        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试....");
        }
       return  returnObject;
    }

    /**
     * 测试批量导出市场活动
     */
    @RequestMapping("/workbench/activity/fileDownload.do")
    public void fileDownload(HttpServletResponse response) throws Exception{
        //1.设置响应类型
        response.setContentType("application/octet-stream;charset=UTF-8");
        //2.获取输出流
        OutputStream out=response.getOutputStream();

        //浏览器接收到响应信息之后，默认情况下，直接在显示窗口中打开响应信息；即使打不开，也会调用应用程序打开；只有实在打不开，才会激活文件下载窗口。
        //可以设置响应头信息，使浏览器接收到响应信息之后，直接激活文件下载窗口，即使能打开也不打开
        response.addHeader("Content-Disposition","attachment;filename=mystudentList.xls");

        //读取excel文件(InputStream)，把输出到浏览器(OutoutStream)
        InputStream is=new FileInputStream("D:\\course\\18-CRM\\阶段资料\\serverDir\\studentList.xls");
        byte[] buff=new byte[256];
        int len=0;
        while((len=is.read(buff))!=-1){
            out.write(buff,0,len);
        }

        //关闭资源
        is.close();
        out.flush();
    }

    /**
     * 批量导出市场活动
     * @param response
     * @throws IOException
     */
    @RequestMapping("/workbench/activity/ExportActivities.do")
    public void ExportActivities(HttpServletResponse response) throws IOException {
        List<Activity> activityList = activtyService.queryActivities();
        HSSFWorkbook hssfWorkbook=new HSSFWorkbook();
        //创建sheet表的名称
        HSSFSheet sheet = hssfWorkbook.createSheet("市场活动");
        //创建行
        HSSFRow row = sheet.createRow(0);
        //创建列
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("id");
        cell=row.createCell(1);
        cell.setCellValue("所有者");
        cell= row.createCell(2);
        cell.setCellValue("名称");
        cell= row.createCell(3);
        cell.setCellValue("开始日期");
        cell=row.createCell(4);
        cell.setCellValue("结束日期");
        cell=row.createCell(5);
        cell.setCellValue("成本");
        cell=row.createCell(6);
        cell.setCellValue("描述");
        cell=row.createCell(7);
        cell.setCellValue("创建日期");
        cell=row.createCell(8);
        cell.setCellValue("创建者");
        cell=row.createCell(9);
        cell.setCellValue("修改日期");
        cell=row.createCell(10);
        cell.setCellValue("修改人");

        /**
         * 创建新的行，并写入数据
         */
        if (activityList!=null&&activityList.size()>0) {
            Activity activity = null;
            for (int i = 0; i < activityList.size(); i++) {
                activity=activityList.get(i);
                //每次遍历都新增一行
                row=sheet.createRow(i+1);
                //往每一列里注入数据
                cell= row.createCell(0);
                cell.setCellValue(activity.getId());
                cell=row.createCell(1);
                cell.setCellValue(activity.getOwner());
                cell=row.createCell(2);
                cell.setCellValue(activity.getName());
                cell=row.createCell(3);
                cell.setCellValue(activity.getStartDate());
                cell=row.createCell(4);
                cell.setCellValue(activity.getEndDate());
                cell=row.createCell(5);
                cell.setCellValue(activity.getCost());
                cell=row.createCell(6);
                cell.setCellValue(activity.getDescription());
                cell=row.createCell(7);
                cell.setCellValue(activity.getCreateTime());
                cell=row.createCell(8);
                cell.setCellValue(activity.getCreateBy());
                cell=row.createCell(9);
                cell.setCellValue(activity.getEditTime());
                cell=row.createCell(10);
                cell.setCellValue(activity.getEditBy());
            }

        }
        response.setContentType("application/octet-stream;charset=UTF-8");
        ////filename=activityList 生成excel的文件名
        response.addHeader("Content-Disposition","attachment;filename=activityList.xls");
        OutputStream out=response.getOutputStream();
        hssfWorkbook.write(out);
        hssfWorkbook.close();
        out.flush();
    }


    @RequestMapping("/workbench/activity/uploadDemo.do")
    @ResponseBody
    public Object uploadDemo(MultipartFile myfile) throws IOException {
        //参数是存放的位置
        String originalFilename = myfile.getOriginalFilename();
        File file =new File("E:\\IO\\Server\\",originalFilename);
        myfile.transferTo(file);
        ReturnObject returnObjectobject =new ReturnObject();
        //返回想应信息
        returnObjectobject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
        returnObjectobject.setMessage("上传成功");
        return returnObjectobject;
    }

    /**
     * 批量导入市场活动
     * @param activityFile
     * @param userName
     * @param session
     * @return
     */
    @RequestMapping("/workbench/activity/importActivity.do")
    @ResponseBody
    public Object importActivity(MultipartFile activityFile,String userName,HttpSession session){
        System.out.println("userName="+userName);
        User user=(User) session.getAttribute(Contants.SESSION_USER);
        ReturnObject returnObject=new ReturnObject();
        try {
            //把excel文件写到磁盘目录中
            /*String originalFilename = activityFile.getOriginalFilename();
            File file = new File("D:\\course\\18-CRM\\阶段资料\\serverDir\\", originalFilename);//路径必须手动创建好，文件如果不存在，会自动创建
            activityFile.transferTo(file);*/

            //解析excel文件，获取文件中的数据，并且封装成activityList
            //根据excel文件生成HSSFWorkbook对象，封装了excel文件的所有信息
            //InputStream is=new FileInputStream("D:\\course\\18-CRM\\阶段资料\\serverDir\\"+originalFilename);

            InputStream is=activityFile.getInputStream();
            HSSFWorkbook wb=new HSSFWorkbook(is);
            //根据wb获取HSSFSheet对象，封装了一页的所有信息
            HSSFSheet sheet=wb.getSheetAt(0);//页的下标，下标从0开始，依次增加
            //根据sheet获取HSSFRow对象，封装了一行的所有信息
            HSSFRow row=null;
            HSSFCell cell=null;
            Activity activity=null;
            List<Activity> activityList=new ArrayList<>();
            for(int i=1;i<=sheet.getLastRowNum();i++) {//sheet.getLastRowNum()：最后一行的下标
                row=sheet.getRow(i);//行的下标，下标从0开始，依次增加
                activity=new Activity();
                activity.setId(UUIDutils.getUUID());
                activity.setOwner(user.getId());
                activity.setCreateTime(DateUtils.formateDateTime(new Date()));
                activity.setCreateBy(user.getId());

                for(int j=0;j<row.getLastCellNum();j++) {//row.getLastCellNum():最后一列的下标+1
                    //根据row获取HSSFCell对象，封装了一列的所有信息
                    cell=row.getCell(j);//列的下标，下标从0开始，依次增加

                    //获取列中的数据
                    String cellValue=HSSFUtils.getCellValueForString(cell);
                    if(j==0){
                        activity.setName(cellValue);
                    }else if(j==1){
                        activity.setStartDate(cellValue);
                    }else if(j==2){
                        activity.setEndDate(cellValue);
                    }else if(j==3){
                        activity.setCost(cellValue);
                    }else if(j==4){
                        activity.setDescription(cellValue);
                    }
                }

                //每一行中所有列都封装完成之后，把activity保存到list中
                activityList.add(activity);
            }

            //调用service层方法，保存市场活动
            int ret=activtyService.saveCreateActivityByList(activityList);

            returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            returnObject.setRetData(ret);
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试....");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/activity/queryActivityDetail.do")
    public Object queryActivityDetail(String id,HttpServletRequest request){
        //调用service方法，查询数据
        Activity activity =activtyService.queryActivityForDetailByID(id);
        List<ActivityRemark> remarkList= activityRemarkService.queryActivityRemarkForDetailByActivityId(id);
        //把数据存到request中
        request.setAttribute("activity",activity);
        request.setAttribute("remarkList",remarkList);
        //转发请求
        return "workbench/activity/detail";

    }
}


