package cn.edu.sicnu.cs.service;

import cn.edu.sicnu.cs.model.User;
import cn.edu.sicnu.cs.pojo.EngInfoPojo;
import cn.edu.sicnu.cs.pojo.engineer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface EngineerManagerService {

    Map<String,Object> FindEngineer(int pagenum,int pagesize,int status);

    void updateRolesByEngManager(List<User> username, String rolename);

    void updateRoleByEngManager(String username,String rolename);

    void updateEngInfo(HttpServletRequest request) throws IOException;

    EngInfoPojo FindEngineerById(int uid);

    List<Map<String,Object>> FindUserMap();
}
