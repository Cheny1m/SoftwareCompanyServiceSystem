package cn.edu.sicnu.cs.dao;

import cn.edu.sicnu.cs.model.Front_dic_item;
import cn.edu.sicnu.cs.model.Front_dic_itemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface Front_dic_itemMapper {
    long countByExample(Front_dic_itemExample example);

    int deleteByExample(Front_dic_itemExample example);

    int deleteByPrimaryKey(Integer fdiId);

    int insert(Front_dic_item record);

    int insertSelective(Front_dic_item record);

    List<Front_dic_item> selectByExample(Front_dic_itemExample example);

    Front_dic_item selectByPrimaryKey(Integer fdiId);

    int updateByExampleSelective(@Param("record") Front_dic_item record, @Param("example") Front_dic_itemExample example);

    int updateByExample(@Param("record") Front_dic_item record, @Param("example") Front_dic_itemExample example);

    int updateByPrimaryKeySelective(Front_dic_item record);

    int updateByPrimaryKey(Front_dic_item record);
}