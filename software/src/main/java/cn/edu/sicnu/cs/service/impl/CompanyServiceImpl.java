package cn.edu.sicnu.cs.service.impl;

import cn.edu.sicnu.cs.dao.CompanyMapper;
import cn.edu.sicnu.cs.model.Company;
import cn.edu.sicnu.cs.model.CompanyExample;
import cn.edu.sicnu.cs.service.CompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Classname CompanyServiceImpl
 * @Description TODO
 * @Date 2020/12/9 21:15
 * @Created by Huan
 */
@Service
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    @Resource
    CompanyMapper companyMapper;

    @Override
    public List<Company> findAll() {
        CompanyExample companyExample = new CompanyExample();
        companyExample.createCriteria().andCidIsNotNull();
        return companyMapper.selectByExample(companyExample);
    }

    @Override
    public Company findCompanyById(Integer id) {
        return companyMapper.selectByPrimaryKey(id);
    }
}
