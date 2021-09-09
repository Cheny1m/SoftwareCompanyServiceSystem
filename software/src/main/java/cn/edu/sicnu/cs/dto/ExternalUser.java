package cn.edu.sicnu.cs.dto;

import cn.edu.sicnu.cs.anotations.Log;
import cn.edu.sicnu.cs.validation.Create;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * @Classname ExternalUser
 * @Description TODO
 * @Date 2020/12/17 15:02
 * @Created by Huan
 */
@Data
public class ExternalUser {

    private Integer cateinfo;
    @Pattern(regexp = "^[A-Za-z_@.0-9]{4,16}$",groups = {Create.class})
    private String username;
    @Email
    private String email;
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)(?![0-9a-zA-Z]+$)(?![0-9\\W]+$)(?![a-zA-Z\\W]+$)[0-9A-Za-z\\W]{6,18}$",groups = {Create.class})
    private String password;
    @Pattern(regexp = "^1(3([0-35-9]\\d|4[1-8])|4[14-9]\\d|5([0-35689]\\d|7[1-79])|66\\d|7[2-35-8]\\d|8\\d{2}|9[13589]\\d)\\d{7}$",groups = {Create.class})
    private String phone;
    @Range(min = 100000,max = 1000000,groups = {Create.class})
    @NotBlank
    private String code;
}
