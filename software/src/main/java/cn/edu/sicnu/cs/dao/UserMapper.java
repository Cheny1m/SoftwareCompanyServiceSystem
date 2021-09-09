package cn.edu.sicnu.cs.dao;

import cn.edu.sicnu.cs.model.User;

import java.util.List;
import java.util.Map;

import cn.edu.sicnu.cs.model.UserExample;
import cn.edu.sicnu.cs.pojo.EngInfoPojo;
import cn.edu.sicnu.cs.pojo.engineer;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface UserMapper {

    List<Map<String,Object>> FindUserMap();

    EngInfoPojo selectAllByUid(int uid);

    void updateRoleByUsername(String urealname,String rolename);

    int selectUidByUsername(String username);

    List<engineer> selectengidByRolename(int pagenum,int pagesize);

    List<Integer> selectEngidByRolename();

    long countByExample(UserExample example);
    @Transactional
    int deleteByExample(UserExample example);
    @Transactional
    int deleteByPrimaryKey(Integer uid);
    @Transactional
    int insert(User record);
    @Transactional
    int insertSelective(User record);
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    List<User> selectByExample(UserExample example);
    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
    User selectByPrimaryKey(Integer uid);
    @Transactional
    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);
    @Transactional
    int updateByExample(@Param("record") User record, @Param("example") UserExample example);
    @Transactional
    int updateByPrimaryKeySelective(User record);
    @Transactional
    int updateByPrimaryKey(User record);

    List<User> selectPage(String blurry);

    Map<String, Object> GetSubmitInfo(long uid);
}