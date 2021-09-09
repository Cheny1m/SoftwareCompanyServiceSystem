package cn.edu.sicnu.cs.service;

import cn.edu.sicnu.cs.model.Company;

import java.util.List;

/**
 * @Classname CompanyService
 * @Description TODO
 * @Date 2020/12/9 21:13
 * @Created by Huan
 */
public interface CompanyService {

    /**
     * 查询所有的公司列表
     * @return
     */
    List<Company> findAll();

    /**
     * 根据公司id查询公司详细情况
     * @param id 公司id
     * @return
     */
    Company findCompanyById(Integer id);
}
