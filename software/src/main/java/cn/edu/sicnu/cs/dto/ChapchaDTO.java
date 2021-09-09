package cn.edu.sicnu.cs.dto;

import cn.edu.sicnu.cs.validation.Create;
import cn.edu.sicnu.cs.validation.Select;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;

/**
 * @Classname ChapchaDTO
 * @Description TODO
 * @Date 2020/12/17 14:28
 * @Created by Huan
 */
@Data
public class ChapchaDTO {
    @NotEmpty(groups = {Create.class})
    private String identifier;
    @Length(min = 5,max = 5,groups = {Select.class})
    private String code;
}
