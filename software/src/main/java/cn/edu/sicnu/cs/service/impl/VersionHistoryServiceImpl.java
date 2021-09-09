package cn.edu.sicnu.cs.service.impl;

import cn.edu.sicnu.cs.dao.VersionhistoryMapper;
import cn.edu.sicnu.cs.model.FaqExample;
import cn.edu.sicnu.cs.model.Versionhistory;
import cn.edu.sicnu.cs.model.VersionhistoryExample;
import cn.edu.sicnu.cs.pojo.VersionListPojo;
import cn.edu.sicnu.cs.service.VersionHistoryService;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class VersionHistoryServiceImpl implements VersionHistoryService {

    @Resource
    VersionhistoryMapper versionhistoryMapper;


    @Override
    public List<VersionListPojo> FindVersionHistoryVBIG() {
        List<VersionListPojo> list = versionhistoryMapper.findVersionHistoryVBIG();

        return list;
    }

    @Override
    public List<Versionhistory> FindVersionHistoryListByVBIG(String vbig) {

        List<Versionhistory> list = versionhistoryMapper.findVersionHistoryListByVBIG(vbig);
        return list;
    }

    @Override
    public Versionhistory FindVersionHistoryByVersion(String version) {

        List<Versionhistory> list = versionhistoryMapper.findVersionHistoryByVersion(version);
        return list.get(0);
    }

    @Override
    public void AddVersion(Versionhistory versionhistory) {
        versionhistoryMapper.insert(versionhistory);
    }

    @Override
    public List<Versionhistory> FindVersionList() {
        return versionhistoryMapper.findVersionList();
    }

    @Override
    public void UpdateFaq(Versionhistory versionhistory) {

        VersionhistoryExample versionhistoryExample = new VersionhistoryExample();
        versionhistoryExample.createCriteria().andVversionEqualTo(versionhistory.getVversion());
        System.out.println(versionhistoryExample.toString());
        //versionhistoryExample.createCriteria();

        //faqExample.createCriteria();

        versionhistoryMapper.updateByExampleSelective(versionhistory,versionhistoryExample);
    }

    @Override
    public void Deleteversionhistory(Versionhistory versionhistory) {
        VersionhistoryExample versionhistoryExample = new VersionhistoryExample();
        versionhistoryExample.createCriteria().andVversionEqualTo(versionhistory.getVversion());

        versionhistoryMapper.deleteByExample(versionhistoryExample);
    }
}
