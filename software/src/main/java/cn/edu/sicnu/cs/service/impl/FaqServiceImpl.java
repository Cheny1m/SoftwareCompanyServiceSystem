package cn.edu.sicnu.cs.service.impl;


import cn.edu.sicnu.cs.pojo.FaqListPojo;
import cn.edu.sicnu.cs.service.FaqService;
import cn.edu.sicnu.cs.dao.FaqMapper;
import cn.edu.sicnu.cs.model.Faq;
import cn.edu.sicnu.cs.model.FaqExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class FaqServiceImpl implements FaqService {

    @Resource
    private FaqMapper faqMapper;

    @Override
    public List<Faq> FindFaqListByType(String type) {

        List<Faq> list = faqMapper.findFaqListByType(type);
        //返回FAQ的名字
        return list;
    }

    @Override
    public Faq FindFaqName(String name) {
        FaqExample faqExample = new FaqExample();
        FaqExample.Criteria criteria = faqExample.createCriteria();
        criteria.andQnameEqualTo(name);
        List<Faq> list = faqMapper.selectByExample(faqExample);

        return faqMapper.findFaqByname(name);
    }

    @Override
    public List<FaqListPojo> FindFaqVersionList() {
        return faqMapper.findFaqVersionList();
    }

    @Override
    public void AddFaqName(Faq faq) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        Date day = new Date();
        day.equals(today);
        faq.setQtime(day);
        faqMapper.insert(faq);
    }

    @Override
    public void UpdateFaq(Faq faq) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        Date day = new Date();
        day.equals(today);
        faq.setQuptimestamp(day);

        FaqExample faqExample = new FaqExample();
        faqExample.createCriteria().andQnameEqualTo(faq.getQname());

        //faqExample.createCriteria();
        faqMapper.updateByExampleSelective(faq,faqExample);
    }

    @Override
    public void DeleteFaq(Faq faq) {
        FaqExample faqExample = new FaqExample();
        faqExample.createCriteria().andQnameEqualTo(faq.getQname());

        faqMapper.deleteByExample(faqExample);
    }

    @Override
    public List<Faq> SearchFaq(String qname) {
        String name = "%" + qname + "%";
        return faqMapper.searchFaq(name);
    }
}
