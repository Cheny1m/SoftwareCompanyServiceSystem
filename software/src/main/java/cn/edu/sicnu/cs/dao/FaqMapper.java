package cn.edu.sicnu.cs.dao;

import cn.edu.sicnu.cs.model.Faq;
import cn.edu.sicnu.cs.model.FaqExample;
import java.util.List;

import cn.edu.sicnu.cs.pojo.FaqListPojo;
import org.apache.ibatis.annotations.Param;

public interface FaqMapper {
    long countByExample(FaqExample example);

    int deleteByExample(FaqExample example);

    int deleteByPrimaryKey(Integer qid);

    int insert(Faq record);

    int insertSelective(Faq record);

    List<Faq> selectByExampleWithBLOBs(FaqExample example);

    List<Faq> selectByExample(FaqExample example);

    Faq selectByPrimaryKey(Integer qid);

    int updateByExampleSelective(@Param("record") Faq record, @Param("example") FaqExample example);

    int updateByExampleWithBLOBs(@Param("record") Faq record, @Param("example") FaqExample example);

    int updateByExample(@Param("record") Faq record, @Param("example") FaqExample example);

    int updateByPrimaryKeySelective(Faq record);

    int updateByPrimaryKeyWithBLOBs(Faq record);

    int updateByPrimaryKey(Faq record);

    List<Faq> findFaqListByType(String type);

    List<FaqListPojo> findFaqVersionList();

    Faq findFaqByname(String name);

    List<Faq> searchFaq(String qname);
}