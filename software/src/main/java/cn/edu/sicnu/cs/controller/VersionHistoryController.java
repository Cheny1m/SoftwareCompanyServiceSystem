package cn.edu.sicnu.cs.controller;


import cn.edu.sicnu.cs.model.Faq;
import cn.edu.sicnu.cs.model.Versionhistory;
import cn.edu.sicnu.cs.pojo.VersionListPojo;
import cn.edu.sicnu.cs.service.impl.VersionHistoryServiceImpl;
import cn.edu.sicnu.cs.utils.ResUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.sql.SQLIntegrityConstraintViolationException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@Api(tags = "Version",value = "版本库")
@RequestMapping("/version/")
public class VersionHistoryController {

    @Autowired
    VersionHistoryServiceImpl versionHistoryService;

    @GetMapping("history/vbig")
    @ApiOperation(value = "FindVersionHistory",notes = "侧边拦")
    @ResponseBody
    public String FindVersionHistory(){
        List<VersionListPojo> list = versionHistoryService.FindVersionHistoryVBIG();

        for (VersionListPojo versionListPojo : list) {
            versionListPojo.setChildren(versionHistoryService.FindVersionHistoryListByVBIG(versionListPojo.getVbigversion()));
        }
        //List<Versionhistory> list2 = versionHistoryService.FindVersionHistoryListByVBIG(vbig);
        return ResUtil.getJsonStr(1, "成功", list);
    }


//    @GetMapping("history/list")
//    @ApiOperation(value = "FindVersionHistoryList",notes = "大版本下的历史版本名称列表")
//    @ResponseBody
//    public List<Versionhistory> FindVersionHistoryListByVBIG(String vbig){
//        List<Versionhistory> list = versionHistoryService.FindVersionHistoryListByVBIG(vbig);
//        return list;
//    }

    @GetMapping("big/list")
    @ApiOperation(value = "FindVersionBigList",notes = "所有大标题")
    @ResponseBody
    public String FindVersionBigList(){

        List<Versionhistory> list = versionHistoryService.FindVersionList();

        return ResUtil.getJsonStr(1, "成功", list);
    }

    @GetMapping("list")
    @ApiOperation(value = "FindVersionList",notes = "所有小标题")
    @ResponseBody
    public List<Versionhistory> FindVersionList(){
        List<Versionhistory> list = versionHistoryService.FindVersionList();
        return list;
    }


    @GetMapping("history/detail")
    @ApiOperation(value = "FindVersionHistoryByVersion",notes = "通过版本号查找内容")
    @ResponseBody
    public Versionhistory FindVersionHistoryByVersion(String version){
        Versionhistory versionhistory = versionHistoryService.FindVersionHistoryByVersion(version);
        return versionhistory;
    }

    @PostMapping("add")
    @ResponseBody
    @ApiOperation(value = "AddVersion",notes = "增加Version")
    public String AddVersion(@RequestBody Versionhistory versionhistory){

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            String time = sdf.format(new Date());
            Date day = new Date();
            day.equals(time);
            versionhistory.setVtime(day);
            versionHistoryService.AddVersion(versionhistory);
            return ResUtil.getJsonStr(1, "成功");

        }catch (Exception e){
            return ResUtil.getJsonStr(-1, "失败");
        }

    }

    @PostMapping("update")
    @ResponseBody
    @ApiOperation(value = "UpdateVersion",notes = "更新Version")
    public String UpdateFaqName(@RequestBody Versionhistory versionhistory){

        try{
            System.out.println("1111111" + versionhistory.toString());
            versionHistoryService.UpdateFaq(versionhistory);
            return ResUtil.getJsonStr(1, "成功");
        }catch (Exception e){

            return ResUtil.getJsonStr(-1, "失败");
        }

    }

    @PostMapping("delete")
    @ResponseBody
    @ApiOperation(value = "DeleteVersion",notes = "增加Faq")
    public String DeleteFaq(@RequestBody Versionhistory versionhistory){
        System.out.println(versionhistory.toString());

        try{
            versionHistoryService.Deleteversionhistory(versionhistory);
            return ResUtil.getJsonStr(1, "成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1, "失败");
        }
    }
}
