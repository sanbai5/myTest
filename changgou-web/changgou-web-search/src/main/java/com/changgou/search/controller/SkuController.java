package com.changgou.search.controller;

import com.changgou.search.feign.SkuInfoFeign;
import entity.Page;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.service.ApiListing;

import java.util.Map;
import java.util.Set;

/**
 * @ClassName SkuController
 * @Description
 * @Author sanbai5
 * @Date 10:16 2019/9/28
 * @Version 2.1
 **/
@Controller
@RequestMapping("/search")
public class SkuController {
    @Autowired(required = false)
    private SkuInfoFeign skuInfoFeign;

    /**
     * @author 栗子
     * @Description 用户检索
     * @Date 16:10 2019/9/27
     * @param
     * @return java.lang.String 搜索页面
     **/
    @RequestMapping("/list")
    public String list(@RequestParam(required = false) Map<String, String> searchMap, Model model){
        Map<String, Object> resultMap = skuInfoFeign.search(searchMap);

        // 回显检索数据
        model.addAttribute("resultMap", resultMap);
        // 回显检索条件
        model.addAttribute("searchMap", searchMap);
        // 拼接请求的url
        String url = getUrl(searchMap);
        model.addAttribute("url", url);

        // 构建分页对象
        // total：总条数  currentpage：当前页面  pagesize：每页显示的条数
        long total = Long.parseLong(resultMap.get("totalElements").toString());
        int currentpage = Integer.parseInt(resultMap.get("pageNum").toString());
        int pagesize = Integer.parseInt(resultMap.get("pageSize").toString());
        Page page = new Page(total, currentpage, pagesize);
        model.addAttribute("page", page);
        return "search";
    }

    /**
     * @author 栗子
     * @Description 拼接url
     * @Date 17:08 2019/9/27
     * @param searchMap
     * @return java.lang.String
     **/
    private String getUrl(Map<String, String> searchMap) {
        // 默认：/search/list
        // 条件1：/search/list?keywords=xx
        // 条件2：/search/list?keywords=xx&price=xxx
        String url = "/search/list";
        if (searchMap != null && searchMap.size() > 0){
            url += "?";
            Set<Map.Entry<String, String>> entries = searchMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                if ("pageNum".equals(key)){
                    continue;
                }
                url += key + "=" + value + "&";
            }
            url = url.substring(0, url.length() -1);
        }
        return url;
    }
}
