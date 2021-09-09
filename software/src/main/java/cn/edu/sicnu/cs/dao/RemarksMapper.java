package cn.edu.sicnu.cs.dao;

import cn.edu.sicnu.cs.model.Remarks;
import cn.edu.sicnu.cs.model.RemarksExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RemarksMapper {
    long countByExample(RemarksExample example);

    int deleteByExample(RemarksExample example);

    int deleteByPrimaryKey(Long reid);

    int insert(Remarks record);

    int insertSelective(Remarks record);

    List<Remarks> selectByExampleWithBLOBs(RemarksExample example);

    List<Remarks> selectByExample(RemarksExample example);

    Remarks selectByPrimaryKey(Long reid);

    int updateByExampleSelective(@Param("record") Remarks record, @Param("example") RemarksExample example);

    int updateByExampleWithBLOBs(@Param("record") Remarks record, @Param("example") RemarksExample example);

    int updateByExample(@Param("record") Remarks record, @Param("example") RemarksExample example);

    int updateByPrimaryKeySelective(Remarks record);

    int updateByPrimaryKeyWithBLOBs(Remarks record);

    int updateByPrimaryKey(Remarks record);
}