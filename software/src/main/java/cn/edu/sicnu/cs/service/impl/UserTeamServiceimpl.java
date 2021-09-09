package cn.edu.sicnu.cs.service.impl;

import cn.edu.sicnu.cs.dao.TeamMapper;
import cn.edu.sicnu.cs.dao.UserMapper;
import cn.edu.sicnu.cs.dao.UserTeamMapper;
import cn.edu.sicnu.cs.model.*;
import cn.edu.sicnu.cs.pojo.AddMemberpojo;
import cn.edu.sicnu.cs.pojo.UserInTeam;
import cn.edu.sicnu.cs.pojo.engineer;
import cn.edu.sicnu.cs.service.UserTeamService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserTeamServiceimpl implements UserTeamService {

    @Resource
    UserTeamMapper userTeamMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    TeamMapper teamMapper;

    @Override
    public List<Map<String,Object>> GetTeamMember(Integer fzid) {
        int tid = teamMapper.selectTeidByFzid(fzid);
        List<Map<String,Object>> list = userTeamMapper.selectEngByTid(tid);
        return list;
    }

    @Override
    public List<UserInTeam> findTeamByName(long uid) {

        List<UserInTeam> userTeams = userTeamMapper.selectTeamByName(uid);
        return userTeams;
    }

    @Override
    public void insertUserTeam(List<AddMemberpojo> addMemberpojoList) {
        for (int i = 0;i < addMemberpojoList.size();i++) {
            UserTeam userTeam = new UserTeam();
            userTeam.setTehours(addMemberpojoList.get(i).getTehours());
            userTeam.setTedays(addMemberpojoList.get(i).getTedays());
            userTeam.setUtUid(addMemberpojoList.get(i).getUid());
            userTeam.setUtTid(teamMapper.selectTeidByFzname(addMemberpojoList.get(i).getFzname()));
            userTeamMapper.insert(userTeam);
        }
    }

    @Override
    public int deleteUserTeam(String fzname, String username) {
        int teamid = teamMapper.selectTeidByFzname(fzname);
        int userid = userMapper.selectUidByUsername(username);
        UserTeamExample userTeamExample = new UserTeamExample();
        UserTeamExample.Criteria criteria = userTeamExample.createCriteria();
        criteria.andUtTidEqualTo(teamid);
        criteria.andUtUidEqualTo(userid);
        userTeamMapper.deleteByExample(userTeamExample);
        return 1;
    }

    @Override
    public List<User> FindMember() {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andUroleIdEqualTo(4);
        List<User> eng = userMapper.selectByExample(userExample);
//        List<String> engname = new ArrayList<>();
//        for (int i = 0;i < eng.size();i++){
//            if (userTeamMapper.selectFznameByUid(eng.get(i)) == null){
//                engname.add(userMapper.selectByPrimaryKey(eng.get(i)).getUrealname());
//            }
//        }
        return eng;
    }

    @Override
    public void deleteTeam(Integer fzid) {
        TeamExample teamExample = new TeamExample();
        UserTeamExample userTeamExample = new UserTeamExample();
        UserTeamExample.Criteria criteria1 = userTeamExample.createCriteria();
        TeamExample.Criteria criteria2 = teamExample.createCriteria();
        criteria2.andTeUidEqualTo(fzid);
        criteria1.andUtTidEqualTo(teamMapper.selectByExample(teamExample).get(0).getTezid());
        teamMapper.deleteByExample(teamExample);
        userTeamMapper.deleteByExample(userTeamExample);
    }

    @Override
    public List<UserInTeam> getinfo() {
        List<UserInTeam> userTeams = userTeamMapper.getinfo();
        return userTeams;
    }

}
