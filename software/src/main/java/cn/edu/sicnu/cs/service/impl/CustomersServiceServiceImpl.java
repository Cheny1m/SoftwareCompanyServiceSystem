package cn.edu.sicnu.cs.service.impl;


import cn.edu.sicnu.cs.dao.UserformMapper;
import cn.edu.sicnu.cs.model.Userform;
import cn.edu.sicnu.cs.service.CustomersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class CustomersServiceServiceImpl implements CustomersService {

    @Autowired
    FeedBackServiceImpl feedBackService;


    @Override
    public int TodayUserFormCnt(long cid,String type) {
        int cnt = feedBackService.TodayUserFormCnt(cid,type);
        return cnt;
    }

    @Override
    public List<Map<String,Object>> FindUserFormList(long cid, long page, long pagenum, String status) {
        return feedBackService.FindUserFormList(cid,(page - 1)*pagenum,pagenum,status);
    }

    @Override
    public Userform FindUserFormByFid(long fid) {
        return feedBackService.FindUserFormByFid(fid);
    }

    @Override
    public long FindUserFormCnt(long cid, String status) {
        long cnt = feedBackService.FindUserFormCnt(cid,status);
        return cnt;
    }

    @Override
    public List<Map<String,Object>> FindAllUserFormList(long cid, long page, long pagenum) {
        return feedBackService.FindAllUserFormList(cid,page,pagenum);
    }

    @Override
    public List<Map<String, Object>> AdminFindUserFormList(long page, long pagenum) {
        return feedBackService.AdminFindUserFormList(page,pagenum);
    }

    @Override
    public long AdminFindUserFormCnt() {
        return feedBackService.AdminFindUserFormCnt();
    }


    public long FindAllUserFormCnt(long cid) {
        long total = feedBackService.FindAllUserFormCnt(cid);
        return total;
    }
}
