package cn.edu.sicnu.cs.service;


import cn.edu.sicnu.cs.model.Userform;

import java.util.List;
import java.util.Map;

public interface FeedBackService {

    void updateFviewsByFid(long fid);

    List<Map<String,Object>> FindHotUserFormList(long page,long pagenum);

    Userform FindUserFormByFid(long fid);


    List<Map<String,Object>> FindSelfUserFormList(long page,long pagenum,long uid);

    String SubmitUserForm(Userform userform);

    List<Map<String,Object>> FindUserFormList(long cid, long page, long pagenum, String status);


    int TodayUserFormCnt(long cid,String type);

    List<Userform> todayUserForm(String type);

    long FindUserFormCnt(long cid, String status);

    long FindHotUserFormListCnt();

    long StatisticsYouthCnt(long uid);

    long StatisticsWeekCnt(long uid);

    long StatisticsTotalCnt(long uid);

    long FindAllSelfUserFormCnt(long uid);

    List<Map<String, Object>> FindAllUserFormList(long cid, long page, long pagenum);

    long FindAllUserFormCnt(long cid);

    void FinishUserForm(long fid);

    void DeleteUserForm(long fid);

    void WriteRemarks(String s, long id, String restate , String recontent);

    List<Map<String, Object>> SearchUserFormt(long fid,long uid);

    List<Map<String, Object>> SearchUserForm(long fid, long uid);

    List<Map<String, Object>> AdminFindUserFormList(long page, long pagenum);

    long AdminFindUserFormCnt();
}
