package cn.edu.sicnu.cs.service;



import cn.edu.sicnu.cs.model.Versionhistory;
import cn.edu.sicnu.cs.pojo.VersionListPojo;

import java.util.List;

public interface VersionHistoryService {

    List<VersionListPojo>  FindVersionHistoryVBIG();

    List<Versionhistory> FindVersionHistoryListByVBIG(String vbig);

    Versionhistory FindVersionHistoryByVersion(String version);

    void AddVersion(Versionhistory versionhistory);

    List<Versionhistory> FindVersionList();

    void UpdateFaq(Versionhistory versionhistory);

    void Deleteversionhistory(Versionhistory versionhistory);
}
