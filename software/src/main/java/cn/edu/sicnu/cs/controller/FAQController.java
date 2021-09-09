package cn.edu.sicnu.cs.controller;

import cn.edu.sicnu.cs.model.Faq;
import cn.edu.sicnu.cs.pojo.FaqListPojo;
import cn.edu.sicnu.cs.service.FaqService;
import cn.edu.sicnu.cs.service.impl.FaqServiceImpl;
import cn.edu.sicnu.cs.utils.ResUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Api(tags = "Faq",value = "知识库")
@RequestMapping("/faq/")
public class FAQController {

    @Autowired
    FaqServiceImpl faqService;

    @GetMapping("browse/type")
    @ResponseBody
    @ApiOperation(value = "findFaqByType",notes = "根据Faq侧栏")
    public String findFaqByType(){
        List <FaqListPojo> list = faqService.FindFaqVersionList();

        for (FaqListPojo faqListPojo : list) {
            faqListPojo.setChildren(faqService.FindFaqListByType(faqListPojo.getQtype()));
        }
        //List<Faq> list = faqService.FindFaqListByType(type);
        Map<String,Object> map = new HashMap<>();
        map.put("list",list);
        return ResUtil.getJsonStr(1, "成功", map);
    }

    @GetMapping("browse/name")
    @ResponseBody
    @ApiOperation(value = "FindFaqName",notes = "根据标题查询Faq")
    public String FindFaqName(String name){
        Faq faq = faqService.FindFaqName(name);
        Map<String,Object> map = new HashMap<>();
        map.put("detail",faq);
        return ResUtil.getJsonStr(1, "成功", map);
    }

    @PostMapping("add")
    @ResponseBody
    @ApiOperation(value = "AddFaqName",notes = "增加Faq")
    public String AddFaqName(@RequestBody Faq faq){
        try{
            faqService.AddFaqName(faq);
            return ResUtil.getJsonStr(1, "成功");
        }catch (Exception e){
            return ResUtil.getJsonStr(-1, "失败");
        }
    }

    @PostMapping("update")
    @ResponseBody
    @ApiOperation(value = "UpdateFaqName",notes = "增加Faq")
    public String UpdateFaqName(@RequestBody Faq faq){
        System.out.println(faq.toString());

        try{
            faqService.UpdateFaq(faq);
            return ResUtil.getJsonStr(1, "成功");
        }catch (Exception e){
            return ResUtil.getJsonStr(-1, "失败");
        }

    }

    @PostMapping("delete")
    @ResponseBody
    @ApiOperation(value = "DeleteFaq",notes = "增加Faq")
    public String DeleteFaq(@RequestBody Faq faq){
        System.out.println(faq.toString());

        try{
            faqService.DeleteFaq(faq);
            return ResUtil.getJsonStr(1, "成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1, "失败");
        }
    }

    @GetMapping("search")
    @ResponseBody
    @ApiOperation(value = "SearchFaq",notes = "查找Faq")
    public String SearchFaq(String qname){

        try{
            List <Faq > list = faqService.SearchFaq(qname);
            Map <String,Object> map = new HashMap<>();
            for (Faq faq : list) {
                faq.setQdec("");
            }
            map.put("list",list);
            return ResUtil.getJsonStr(1, "成功",map);
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1, "失败");
        }
    }

}
