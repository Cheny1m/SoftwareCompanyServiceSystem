package cn.edu.sicnu.cs.service;

import cn.edu.sicnu.cs.model.User;
import cn.edu.sicnu.cs.model.UserTeam;
import cn.edu.sicnu.cs.pojo.AddMemberpojo;
import cn.edu.sicnu.cs.pojo.UserInTeam;
import cn.edu.sicnu.cs.pojo.engineer;

import java.util.List;
import java.util.Map;

public interface UserTeamService {

    List<Map<String,Object>> GetTeamMember(Integer fzid);

    List<UserInTeam> findTeamByName(long uid);

    void insertUserTeam(List<AddMemberpojo> addMemberpojoList);

    int deleteUserTeam(String fzname,String username);

    List<User> FindMember();

    void deleteTeam(Integer fzid);

    List<UserInTeam> getinfo();
}
