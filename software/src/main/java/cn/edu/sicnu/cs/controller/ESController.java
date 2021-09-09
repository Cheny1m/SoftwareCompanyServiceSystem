package cn.edu.sicnu.cs.controller;

import cn.edu.sicnu.cs.service.ElasticSearchService;
import cn.edu.sicnu.cs.utils.ResUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Classname ESController
 * @Description TODO
 * @Date 2020/12/4 12:13
 * @Created by Songyz
 */

@Controller
@RequestMapping("/essearch")
public class ESController {

    @Resource
    ElasticSearchService elasticSearchService;

    @GetMapping("/suggestion")
    @ResponseBody
    @ApiOperation(value = "searchsuggestion",notes = "搜索建议")
    public String searchsuggestion( String keyword,
                                    int pagenum,
                                    int pagesize){
        try {
            return ResUtil.getJsonStr(1,"成功",elasticSearchService.searchsuggest(keyword, pagenum, pagesize));
        }catch (Exception e){
            return ResUtil.getJsonStr(1,"失败");
        }

    }

//    public static void main(String[] args) {
//        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//        String pwd = bCryptPasswordEncoder.encode("123456");
//        System.out.println(pwd);
//    }
}
