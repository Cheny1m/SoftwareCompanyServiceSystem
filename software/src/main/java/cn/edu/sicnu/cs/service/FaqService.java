package cn.edu.sicnu.cs.service;

import cn.edu.sicnu.cs.model.Faq;
import cn.edu.sicnu.cs.pojo.FaqListPojo;

import java.util.List;
import java.util.Map;

public interface FaqService {
    List<Faq> FindFaqListByType(String type);

    Faq FindFaqName(String name);

    List<FaqListPojo> FindFaqVersionList();

    void AddFaqName(Faq faq);

    void UpdateFaq(Faq faq);

    void DeleteFaq(Faq faq);

    List<Faq> SearchFaq(String qname);
}
