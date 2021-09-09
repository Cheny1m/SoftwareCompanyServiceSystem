package cn.edu.sicnu.cs.controller;

import cn.edu.sicnu.cs.anotations.Log;
import cn.edu.sicnu.cs.anotations.NewLog;
import cn.edu.sicnu.cs.constant.LogConstant;
import cn.edu.sicnu.cs.constant.ResultCode;
import cn.edu.sicnu.cs.dto.CompanyListDTO;
import cn.edu.sicnu.cs.model.Company;
import cn.edu.sicnu.cs.service.CompanyService;
import cn.edu.sicnu.cs.utils.ResUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname CompanyController
 * @Description TODO
 * @Date 2020/12/9 21:11
 * @Created by Huan
 */
@RestController
@Slf4j
public class CompanyController {

    @Autowired
    CompanyService companyService;

    /**
     * 查询所有公司列表
     * @return 公司列表
     */
    @NewLog(value = "查询系统公司列表",type = LogConstant.OPERATION)
    @GetMapping("/${soft_version}/_companys")
    public String findAllCompany(){
        List<Company> all = companyService.findAll();
        List<CompanyListDTO> companyListDTOs = new ArrayList<>();
        for (Company company : all) {
            companyListDTOs.add(new CompanyListDTO(company));
        }
        return ResUtil.getJsonStr(ResultCode.OK,"查询成功",companyListDTOs);
    }

    @NewLog(value = "查询指定id的公司信息",type = LogConstant.OPERATION)
    @GetMapping("/${soft_version}/company/{id}")
    public String findCompanyByCompanyId(@PathVariable("id") Integer companyId){
        if (companyId == null) {
            return ResUtil.getJsonStr(ResultCode.BAD_REQUEST,"参数不能为空");
        }
        Company companyById1 = companyService.findCompanyById(companyId);
        if (companyById1 == null) {
            return ResUtil.getJsonStr(ResultCode.BAD_REQUEST,"公司不存在");
        }

        return ResUtil.getJsonStr(ResultCode.OK,"查询公司成功",companyById1);
    }
}
