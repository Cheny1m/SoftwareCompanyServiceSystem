package cn.edu.sicnu.cs.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.edu.sicnu.cs.model.Userform;
import cn.edu.sicnu.cs.model.UserformExample;
import org.apache.ibatis.annotations.Param;

public interface UserformMapper {

    void updateFviewsByFid(long fid);

    List<Userform> todayUserFormCnt(long cid, String type, String today);

    List<Userform> todayUserForm(String type, String today);

    List<Map<String,Object>> findUserFormList(long cid, long page, long pagenum, String status);

    long countByExample(UserformExample example);

    int deleteByExample(UserformExample example);

    int deleteByPrimaryKey(Long fzid);

    int insert(Userform record);

    int insertSelective(Userform record);

    List<Userform> selectByExampleWithBLOBs(UserformExample example);

    List<Userform> selectByExample(UserformExample example);

    Userform selectByPrimaryKey(Long fzid);

    int updateByExampleSelective(@Param("record") Userform record, @Param("example") UserformExample example);

    int updateByExampleWithBLOBs(@Param("record") Userform record, @Param("example") UserformExample example);

    int updateByExample(@Param("record") Userform record, @Param("example") UserformExample example);

    int updateByPrimaryKeySelective(Userform record);

    int updateByPrimaryKeyWithBLOBs(Userform record);

    int updateByPrimaryKey(Userform record);



    long findUserFormCnt(long cid, String status);


    long findHotUserFormListCnt();

    long statisticsTimeCnt(long uid, String time);

    List<Map<String, Object>> findHotUserFormList(long page, long pagenum);

    List<Map<String, Object>> findSelfUserFormList(long page, long pagenum, long uid);

    List<Userform> findAllUserForm();

    long findAllSelfUserFormCnt(long uid);

    long findAllUserFormCnt(long cid);

    void finishUserForm(long fid, Date day);

    List<Map<String, Object>> searchUserFormt(long fid,long uid);

    List<Map<String, Object>> searchUserForm(long fid, long uid);

    List<Map<String, Object>> findAllUserFormList(long cid, long page, long pagenum);

    long statisticsTotalCnt(long uid);

    List<Map<String, Object>> adminFindUserFormList(long page, long pagenum);

    long adminFindUserFormCnt();
}