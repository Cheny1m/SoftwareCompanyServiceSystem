package cn.edu.sicnu.cs.service.impl;


import cn.edu.sicnu.cs.dao.RemarksMapper;
import cn.edu.sicnu.cs.dao.UserformMapper;
import cn.edu.sicnu.cs.model.Remarks;
import cn.edu.sicnu.cs.model.Userform;
import cn.edu.sicnu.cs.model.UserformExample;
import cn.edu.sicnu.cs.service.FeedBackService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class FeedBackServiceImpl implements FeedBackService {

    @Resource
    UserformMapper userformMapper;

    @Override
    public void updateFviewsByFid(long fid) {
        userformMapper.updateFviewsByFid(fid);
    }

    @Override
    public List<Map<String,Object>> FindHotUserFormList(long page,long pagenum) {

        page = ((page-1) * pagenum);
        List<Map<String,Object>> list = userformMapper.findHotUserFormList(page,pagenum);
        return list;
    }

    @Override
    public Userform FindUserFormByFid(long fid) {

        UserformExample userformExample = new UserformExample();
        UserformExample.Criteria criteria = userformExample.createCriteria();
        criteria.andFidEqualTo(fid);
        List<Userform> list = userformMapper.selectByExample(userformExample);

        return list.get(0);
    }

    @Override
    public List<Map<String,Object>> FindSelfUserFormList(long page,long pagenum,long uid) {
        page = (page-1)*pagenum;
        List<Map<String,Object>> list = userformMapper.findSelfUserFormList(page,pagenum,uid);

        return list;
    }

    @Override
    public String SubmitUserForm(Userform userform) {

         userformMapper.insert(userform);
         return "success";
    }


    @Override
    public List<Map<String,Object>> FindUserFormList(long cid, long page, long pagenum, String status) {
//        page =((page-1) * pagenum);
        List<Map<String,Object>> list = userformMapper.findUserFormList(cid,page,pagenum,status);
        return list;
    }


    @Override
    public int TodayUserFormCnt(long cid,String type) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        today = today + "%";
        List<Userform> list = userformMapper.todayUserFormCnt(cid,type,today);

        return list.size();
    }

    @Override
    public List<Userform> todayUserForm(String type) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        today = today + "%";

        return userformMapper.todayUserForm(type, today);
    }

    @Override
    public long FindUserFormCnt(long cid, String status) {
        return userformMapper.findUserFormCnt(cid,status);
    }

    @Override
    public long FindHotUserFormListCnt() {
        return userformMapper.findHotUserFormListCnt();
    }

    @Override
    public long StatisticsYouthCnt(long uid) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String time = sdf.format(new Date());
        System.out.println(time);
        time = time + "%";
        return userformMapper.statisticsTimeCnt(uid,time);
    }

    @Override
    public long StatisticsWeekCnt(long uid) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String time = sdf.format(new Date());
        time = time + "%";
        return userformMapper.statisticsTimeCnt(uid,time);
    }

    @Override
    public long StatisticsTotalCnt(long uid) {
        return userformMapper.statisticsTotalCnt(uid);
    }

    @Override
    public long FindAllSelfUserFormCnt(long uid) {
        return userformMapper.findAllSelfUserFormCnt(uid);
    }

    @Override
    public List<Map<String, Object>> FindAllUserFormList(long cid, long page, long pagenum) {
        page = (page - 1) * pagenum;
        return userformMapper.findAllUserFormList(cid,page,pagenum);
    }

    @Override
    public long FindAllUserFormCnt(long cid) {
        //return 10;
        return userformMapper.findAllUserFormCnt(cid);
    }

    @Override
    public void FinishUserForm(long fid) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        Date day = new Date();
        day.equals(today);
        userformMapper.finishUserForm(fid,day);
    }

    @Override
    public void DeleteUserForm(long fid) {
        UserformExample userformExample = new UserformExample();
        UserformExample.Criteria criteria = userformExample.createCriteria();
        criteria.andFidEqualTo(fid);
        userformMapper.deleteByExample(userformExample);

    }

    @Override
    public void WriteRemarks(String s, long id, String restate, String recontent) {
        Remarks remarks = new Remarks();
        remarks.setRetype(s);
        remarks.setRestate(restate);
        remarks.setRecontent(recontent);
        remarks.setReaffairtId(id);
        remarksMapper.insert(remarks);
    }

    @Override
    public List<Map<String, Object>> SearchUserFormt(long fid,long uid) {
        return userformMapper.searchUserFormt(fid,uid);
    }

    @Override
    public List<Map<String, Object>> SearchUserForm(long fid, long uid) {
        return userformMapper.searchUserForm(fid,uid);
    }

    @Override
    public List<Map<String, Object>> AdminFindUserFormList(long page, long pagenum) {
        page = (page - 1)*pagenum;
        return userformMapper.adminFindUserFormList(page,pagenum);
    }

    @Override
    public long AdminFindUserFormCnt() {
        return userformMapper.adminFindUserFormCnt();
    }

    @Resource
    RemarksMapper remarksMapper;



}
