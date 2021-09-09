package cn.edu.sicnu.cs.dto;

import cn.edu.sicnu.cs.model.User;
import cn.edu.sicnu.cs.validation.Update;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.Embedded;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Classname ReturningUser
 * @Description TODO
 * @Date 2020/11/23 18:03
 * @Created by Huan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {
    private static final long serialVersionUID = -6157793221485871880L;
    @NotNull
    @Min(1)
    private Integer uid;

    private Integer ucompanyId;

    private String username;

//    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)(?![0-9a-zA-Z]+$)(?![0-9\\W]+$)(?![a-zA-Z\\W]+$)[0-9A-Za-z\\W]{6,18}$",groups = {Update.class})
    private String password;

    private Integer uroleId;
    @NotBlank
    private String urealname;
    @NotNull
    private String ugender;
    @Email
    private String email;
    @Pattern(regexp = "^1(3([0-35-9]\\d|4[1-8])|4[14-9]\\d|5([0-35689]\\d|7[1-79])|66\\d|7[2-35-8]\\d|8\\d{2}|9[13589]\\d)\\d{7}$",groups = {Update.class})
    private String mobile;

    private Date ujoin;

    private Integer uvisits;

    private String uip;

    private Date ulasttime;

    private String ulocked;

    private String udeleted;

    private String role_name;
    /**
     * 新密码
     */
    private String password_new;
    /**
     * 新密码重复
     */
    private String password_new_two;

    public UserDto(User user, String roledesc) {
        this.uid = user.getUid();
        this.ucompanyId = user.getUcompanyId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.uroleId = user.getUroleId();
        this.urealname = user.getUrealname();
        this.ugender = user.getUgender();
        this.email = user.getUemail();
        this.mobile = user.getUmobile();
        this.ujoin = user.getUjoin();
        this.uvisits = user.getUvisits();
        this.uip = user.getUip();
        this.ulasttime = user.getUlasttime();
        this.ulocked = user.getUlocked();
        this.udeleted = user.getUdeleted();
        this.role_name=roledesc;
    }
}
