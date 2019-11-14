package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.SkuMapper;
import com.changgou.goods.dao.SpuMapper;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/****
 * @Author:shenkunlin
 * @Description:Spu业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired(required = false)
    private SpuMapper spuMapper;
    @Autowired(required = false)
    private IdWorker idWorker;
    @Autowired(required = false)
    private CategoryMapper categoryMapper;
    @Autowired(required = false)
    private BrandMapper brandMapper;
    @Autowired(required = false)
    private SkuMapper skuMapper;

    @Override
    public void pullMany(Long[] ids) {
        for (Long spuId : ids) {
            Spu spu = spuMapper.selectByPrimaryKey(spuId);
            if ("1".equals(spu.getIsDelete())) {
                throw new RuntimeException("该商品已经删除");
            }
            spu.setIsMarketable("0");
            spuMapper.updateByPrimaryKeySelective(spu);
        }
    }

    /**
     * @return
     * @Author mqy
     * @Description 批量上架
     * @Date
     * @Param
     **/
    @Override
    public void putMany(Long[] ids) {
        for (Long spuId : ids) {
            Spu spu = spuMapper.selectByPrimaryKey(spuId);
            if ("1".equals(spu.getIsDelete())) {
                throw new RuntimeException("该商品已经删除");
            }
            if (!spu.getStatus().equals("1")) {
                throw new RuntimeException("未审核的商品不能上架");
            }
            spu.setIsMarketable("1");
            spuMapper.updateByPrimaryKeySelective(spu);
        }
    }

    /**
     * @return
     * @Author mqy
     * @Description 恢复逻辑删除商品
     * @Date
     * @Param
     **/
    @Override
    public void restore(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if ("0".equals(spu.getIsDelete())) {
            throw new RuntimeException("此商品未删除");
        }
        spu.setStatus("0");
        spu.setIsDelete("0");

        spuMapper.updateByPrimaryKeySelective(spu);

    }

    /**
     * @return
     * @Author mqy
     * @Description 逻辑删除
     * @Date
     * @Param
     **/
    @Override
    public void logicDelete(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if ("1".equals(spu.getIsMarketable())) {
            throw new RuntimeException("上架的商品不能删除");
        }
        spu.setIsDelete("1");
        spu.setStatus("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * @return
     * @Author mqy
     * @Description 商品下架
     * @Date
     * @Param
     **/
    @Override
    public void soldOut(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if ("1".equals(spu.getIsDelete())) {
            throw new RuntimeException("该商品已经删除");
        }
        spu.setIsMarketable("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * @return
     * @Author mqy
     * @Description 商品上架
     * @Date
     * @Param
     **/
    @Override
    public void pull(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if ("1".equals(spu.getIsDelete())) {
            throw new RuntimeException("该商品已经删除");
        }
        if (!spu.getStatus().equals("1")) {
            throw new RuntimeException("未审核的商品不能上架");
        }
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spu);

    }

    /**
     * @return
     * @Author mqy
     * @Description 商品审核
     * @Date
     * @Param
     **/
    @Override
    public void audit(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if ("1".equals(spu.getIsDelete())) {
            throw new RuntimeException("该商品已经删除");
        }
        spu.setIsMarketable("1");
        spu.setStatus("1");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * @return
     * @Author mqy
     * @Description 根据SpuId查询Goods
     * @Date
     * @Param
     **/
    @Override
    public Goods findGoodsBySupId(Long supId) {
        Goods goods = new Goods();
        Spu spu = spuMapper.selectByPrimaryKey(supId);
        Sku sku = new Sku();
        sku.setSpuId(supId);
        List<Sku> skus = skuMapper.select(sku);
        goods.setSpu(spu);
        goods.setSkuList(skus);
        return goods;
    }

    /**
     * @return
     * @Author mqy
     * @Description 保存Goods
     * @Date
     * @Param
     **/
    @Override
    public void saveGoods(Goods goods) {
        //保存SPU
        Spu spu = goods.getSpu();
        if (spu.getId() == null) {
            spu.setId(idWorker.nextId());
            spu.setIsDelete("0");
            spu.setStatus("0");
            spu.setIsMarketable("0");
            spuMapper.insertSelective(spu);
        } else {
            spuMapper.updateByPrimaryKeySelective(spu);
            Sku sku = new Sku();
            sku.setSpuId(spu.getId());
            skuMapper.delete(sku);
        }

        //保存SKU

        List<Sku> skuList = goods.getSkuList();
        for (Sku sku : skuList) {
            sku.setId(idWorker.nextId());
            String name = spu.getName();
            if (StringUtils.isEmpty(sku.getSpec())) {
                sku.setSpec("{}");
            }
            String spec = sku.getSpec();
            Map<String, String> map = JSON.parseObject(spec, Map.class);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String value = entry.getValue();
                name += " " + value;
            }
            sku.setName(name);
            sku.setCreateTime(new Date());
            sku.setUpdateTime(new Date());
            sku.setSpuId(spu.getId());
            sku.setCategoryId(spu.getCategory3Id());
            sku.setBrandName(brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());
            sku.setStatus("2");
            skuMapper.insertSelective(sku);
        }

    }

    /**
     * Spu条件+分页查询
     *
     * @param spu  查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Spu> findPage(Spu spu, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExample(spu);
        //执行搜索
        return new PageInfo<Spu>(spuMapper.selectByExample(example));
    }

    /**
     * Spu分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Spu> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<Spu>(spuMapper.selectAll());
    }

    /**
     * Spu条件查询
     *
     * @param spu
     * @return
     */
    @Override
    public List<Spu> findList(Spu spu) {
        //构建查询条件
        Example example = createExample(spu);
        //根据构建的条件查询数据
        return spuMapper.selectByExample(example);
    }


    /**
     * Spu构建查询对象
     *
     * @param spu
     * @return
     */
    public Example createExample(Spu spu) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (spu != null) {
            // 主键
            if (!StringUtils.isEmpty(spu.getId())) {
                criteria.andEqualTo("id", spu.getId());
            }
            // 货号
            if (!StringUtils.isEmpty(spu.getSn())) {
                criteria.andEqualTo("sn", spu.getSn());
            }
            // SPU名
            if (!StringUtils.isEmpty(spu.getName())) {
                criteria.andLike("name", "%" + spu.getName() + "%");
            }
            // 副标题
            if (!StringUtils.isEmpty(spu.getCaption())) {
                criteria.andEqualTo("caption", spu.getCaption());
            }
            // 品牌ID
            if (!StringUtils.isEmpty(spu.getBrandId())) {
                criteria.andEqualTo("brandId", spu.getBrandId());
            }
            // 一级分类
            if (!StringUtils.isEmpty(spu.getCategory1Id())) {
                criteria.andEqualTo("category1Id", spu.getCategory1Id());
            }
            // 二级分类
            if (!StringUtils.isEmpty(spu.getCategory2Id())) {
                criteria.andEqualTo("category2Id", spu.getCategory2Id());
            }
            // 三级分类
            if (!StringUtils.isEmpty(spu.getCategory3Id())) {
                criteria.andEqualTo("category3Id", spu.getCategory3Id());
            }
            // 模板ID
            if (!StringUtils.isEmpty(spu.getTemplateId())) {
                criteria.andEqualTo("templateId", spu.getTemplateId());
            }
            // 运费模板id
            if (!StringUtils.isEmpty(spu.getFreightId())) {
                criteria.andEqualTo("freightId", spu.getFreightId());
            }
            // 图片
            if (!StringUtils.isEmpty(spu.getImage())) {
                criteria.andEqualTo("image", spu.getImage());
            }
            // 图片列表
            if (!StringUtils.isEmpty(spu.getImages())) {
                criteria.andEqualTo("images", spu.getImages());
            }
            // 售后服务
            if (!StringUtils.isEmpty(spu.getSaleService())) {
                criteria.andEqualTo("saleService", spu.getSaleService());
            }
            // 介绍
            if (!StringUtils.isEmpty(spu.getIntroduction())) {
                criteria.andEqualTo("introduction", spu.getIntroduction());
            }
            // 规格列表
            if (!StringUtils.isEmpty(spu.getSpecItems())) {
                criteria.andEqualTo("specItems", spu.getSpecItems());
            }
            // 参数列表
            if (!StringUtils.isEmpty(spu.getParaItems())) {
                criteria.andEqualTo("paraItems", spu.getParaItems());
            }
            // 销量
            if (!StringUtils.isEmpty(spu.getSaleNum())) {
                criteria.andEqualTo("saleNum", spu.getSaleNum());
            }
            // 评论数
            if (!StringUtils.isEmpty(spu.getCommentNum())) {
                criteria.andEqualTo("commentNum", spu.getCommentNum());
            }
            // 是否上架,0已下架，1已上架
            if (!StringUtils.isEmpty(spu.getIsMarketable())) {
                criteria.andEqualTo("isMarketable", spu.getIsMarketable());
            }
            // 是否启用规格
            if (!StringUtils.isEmpty(spu.getIsEnableSpec())) {
                criteria.andEqualTo("isEnableSpec", spu.getIsEnableSpec());
            }
            // 是否删除,0:未删除，1：已删除
            if (!StringUtils.isEmpty(spu.getIsDelete())) {
                criteria.andEqualTo("isDelete", spu.getIsDelete());
            }
            // 审核状态，0：未审核，1：已审核，2：审核不通过
            if (!StringUtils.isEmpty(spu.getStatus())) {
                criteria.andEqualTo("status", spu.getStatus());
            }
        }
        return example;
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(Long id) {

        Spu spu = spuMapper.selectByPrimaryKey(id);
        if ("0".equals(spu.getIsDelete())) {
            throw new RuntimeException("未逻辑删除的商品不能删除");
        }
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Spu
     *
     * @param spu
     */
    @Override
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 增加Spu
     *
     * @param spu
     */
    @Override
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }

    /**
     * 根据ID查询Spu
     *
     * @param id
     * @return
     */
    @Override
    public Spu findById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Spu全部数据
     *
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }
}
