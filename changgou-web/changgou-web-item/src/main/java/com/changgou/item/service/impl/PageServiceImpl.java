package com.changgou.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.CategoryFeign;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.item.service.PageService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName PageServiceImpl
 * @Description
 * @Author sanbai5
 * @Date 11:23 2019/9/28
 * @Version 2.1
 **/
@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired(required = false)
    private SkuFeign skuFeign;

    @Autowired(required = false)
    private SpuFeign spuFeign;

    @Autowired(required = false)
    private CategoryFeign categoryFeign;

    @Value("${pagepath}")
    private String pagepath;

    /**
     * @author 栗子
     * @Description 生成静态页
     * @Date 2:42 2019/8/19
     * @param spuId
     * @return void
     **/
    @Override
    public void createHtml(Long spuId) {
        try {
            Map<String, Object> dataModel = getDataModel(spuId);
            // 构建模板数据
            Context context = new Context();
            context.setVariables(dataModel);
            // 指定静态页面生成的位置
            File dir = new File(pagepath);
            if (!dir.exists()){
                dir.mkdirs();   // 目录为空，创建
            }
            File dest = new File(dir, spuId + ".html");
            // 生成页面
            PrintWriter writer = new PrintWriter(dest, "UTF-8");
            templateEngine.process("item", context, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @author 栗子
     * @Description 获取模板需要的数据
     * @Date 2:34 2019/8/19
     * @param spuId
     * @return java.util.Map<java.lang.String,java.lang.Object>
     **/
    private Map<String,Object> getDataModel(Long spuId) {
        Map<String,Object> dataModel = new HashMap<>();
        // 商品信息
        Result<Spu> spuResult = spuFeign.findById(spuId);
        Spu spu = spuResult.getData();
        dataModel.put("spu", spu);
        // 库存信息
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        Result<List<Sku>> skuResult = skuFeign.findList(sku);
        List<Sku> skuList = skuResult.getData();
        dataModel.put("skuList", skuList);
        // 商品分类信息
        Category category1 = categoryFeign.findById(spu.getCategory1Id()).getData();
        Category category2 = categoryFeign.findById(spu.getCategory2Id()).getData();
        Category category3 = categoryFeign.findById(spu.getCategory3Id()).getData();
        dataModel.put("category1", category1);
        dataModel.put("category2", category2);
        dataModel.put("category3", category3);
        // 商品小图
        String[] images = spu.getImages().split(",");
        dataModel.put("images", images);
        // 商品规格
        Map<String, String> specificationList = JSON.parseObject(spu.getSpecItems(), Map.class);
        dataModel.put("specificationList", specificationList);
        return dataModel;
    }
}
