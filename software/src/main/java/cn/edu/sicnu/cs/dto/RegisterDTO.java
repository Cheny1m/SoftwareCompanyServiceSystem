package cn.edu.sicnu.cs.dto;

import cn.edu.sicnu.cs.validation.Create;
import cn.edu.sicnu.cs.validation.Select;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.nio.channels.Selector;

/**
 * @Classname RegisterDTO
 * @Description TODO
 * @Date 2020/12/17 10:41
 * @Created by Huan
 */
@Data
public class RegisterDTO {
    @Pattern(regexp = "^1(3([0-35-9]\\d|4[1-8])|4[14-9]\\d|5([0-35689]\\d|7[1-79])|66\\d|7[2-35-8]\\d|8\\d{2}|9[13589]\\d)\\d{7}$"
            ,groups = {Create.class, Select.class})
    private String mobile;

    @Range(min = 100000,max = 1000000,groups = {Select.class})
    private String code;
}
