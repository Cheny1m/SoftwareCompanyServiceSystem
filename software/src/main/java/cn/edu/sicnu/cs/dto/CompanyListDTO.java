package cn.edu.sicnu.cs.dto;

import cn.edu.sicnu.cs.model.Company;
import lombok.Data;

/**
 * @Classname CompanyListDTO
 * @Description TODO
 * @Date 2020/12/9 21:19
 * @Created by Huan
 */
@Data
public class CompanyListDTO {

    private Integer id;
    private String companyName;

    public CompanyListDTO(Company company){
        this.id = company.getCid();
        this.companyName = company.getCname();
    }


}
