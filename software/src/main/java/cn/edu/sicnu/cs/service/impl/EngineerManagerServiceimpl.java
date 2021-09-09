package cn.edu.sicnu.cs.service.impl;

import cn.edu.sicnu.cs.dao.RoleMapper;
import cn.edu.sicnu.cs.dao.UserMapper;
import cn.edu.sicnu.cs.dao.UserTeamMapper;
import cn.edu.sicnu.cs.dao.WorkordersMapper;
import cn.edu.sicnu.cs.model.User;
import cn.edu.sicnu.cs.model.UserExample;
import cn.edu.sicnu.cs.pojo.EngInfoPojo;
import cn.edu.sicnu.cs.pojo.engineer;
import cn.edu.sicnu.cs.service.EngineerManagerService;
import com.alibaba.fastjson.JSON;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EngineerManagerServiceimpl implements EngineerManagerService {

    @Resource
    UserMapper userMapper;

    @Resource
    UserTeamMapper userTeamMapper;

    @Resource
    RoleMapper roleMapper;



    @Override
    public Map<String,Object> FindEngineer(int pagenum, int pagesize,int status) {
        pagenum = (pagenum-1)*pagesize;
        List<engineer> engineers = userMapper.selectengidByRolename(pagenum,pagesize);
        List<engineer> ans1 = new ArrayList<>();
        List<engineer> ans2 = new ArrayList<>();
        System.out.println(engineers);
        for (engineer engineer : engineers) {
            int uid = engineer.getUid();
            String fzname = userTeamMapper.selectFznameByUid(uid);
            if(status == 1){
                if(fzname != null){
                    engineer.setFzname(fzname);
                    ans1.add(engineer);
                }
            }else if(status == 0){
                if(fzname == null){
                    engineer.setFzname(fzname);
                    ans2.add(engineer);
                }
            }else{
                engineer.setFzname(fzname);
            }
        }
        Map<String,Object> data = new HashMap<>();

        int total = 0;
        if(status == 1) {
            total = ans1.size();
            data.put("list",ans1);
        }
        if(status == 0) {
            total = ans2.size();
            data.put("list",ans2);
        }
        if(status == 4) {
            total = engineers.size();
            data.put("list",engineers);
        }
        data.put("total",total);
        return data;
    }


    @Override
    public void updateRolesByEngManager(List<User> username, String rolename) {
        for (int i = 0;i < username.size();i++){
            userMapper.updateRoleByUsername(username.get(i).getUrealname(),rolename);
        }
    }

    @Override
    public void updateRoleByEngManager(String username, String rolename) {
        userMapper.updateRoleByUsername(username,rolename);
    }

    @Override
    public void updateEngInfo(HttpServletRequest request) throws IOException {

        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        if(StringUtils.hasText(body)) {
            User user = JSON.parseObject(body, User.class);
            UserExample userExample = new UserExample();
            UserExample.Criteria criteria = userExample.createCriteria();
            criteria.andUidEqualTo(user.getUid());
            userMapper.updateByExampleSelective(user,userExample);
        }
    }

    @Override
    public EngInfoPojo FindEngineerById(int uid) {
        EngInfoPojo engInfoPojo = userMapper.selectAllByUid(uid);
        engInfoPojo.setRdesc(roleMapper.selectByPrimaryKey(engInfoPojo.getUrole_id()).getRdesc());
        return engInfoPojo;
    }

    @Override
    public List<Map<String, Object>> FindUserMap() {
        return userMapper.FindUserMap();
    }

}

