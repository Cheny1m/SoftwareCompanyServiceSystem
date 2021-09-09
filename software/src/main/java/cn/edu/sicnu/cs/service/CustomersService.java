package cn.edu.sicnu.cs.service;

import cn.edu.sicnu.cs.model.Userform;

import java.util.List;
import java.util.Map;

public interface CustomersService {


    int TodayUserFormCnt(long cid,String type);

    List<Map<String,Object>> FindUserFormList(long cid,long page,long pagenum,String status);

    Userform FindUserFormByFid(long fid);

    long FindUserFormCnt(long cid, String status);

    Object FindAllUserFormList(long cid, long page, long pagenum);

    List<Map<String, Object>> AdminFindUserFormList(long page, long pagenum);

    long AdminFindUserFormCnt();
}
