package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuInfoMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuInfoService;
import entity.Result;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Stream;

/**
 * @ClassName SkuInfoServiceImpl
 * @Description
 * @Author sanbai5
 * @Date 17:27 2019/9/24
 * @Version 2.1
 **/
@Service
public class SkuInfoServiceImpl implements SkuInfoService {
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired(required = false)
    private SkuFeign skuFeign;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
/*
        目前搜索条件里面的searchMap里面的KEY字段：
 keywords：关键词搜索
 category：分类
 brand：品牌
 spec_网络or其他：根据规格过滤
 price：价格区间，格式为：19-50或者19-
 sortRule:排序规则，即升序还是降序
 sortField:排序字段，根据哪个字段排序
 pageNum:当前页码
        目前返回结果resultMap里面的KEY字段：
 categoryList：分类集合
 brandList：品牌集合
 specList：描述集合
 rows：结果集
 totalElements：总条数
 pageSize：总页数

*/

    /**
     * @return
     * @Author mqy
     * @Description 将数据存入索引库中
     * @Date
     * @Param
     **/
    @Override
    public void importSkuInfoToEs() {
        Result<List<Sku>> result = skuFeign.findSkusByStatus();
        List<Sku> data = result.getData();
        String text = JSON.toJSONString(data);
        List<SkuInfo> skuInfos = JSON.parseArray(text, SkuInfo.class);
        for (SkuInfo skuInfo : skuInfos) {
            String spec = skuInfo.getSpec();
            Map<String, Object> map = JSON.parseObject(spec, Map.class);
            skuInfo.setSpecMap(map);
        }
        skuInfoMapper.saveAll(skuInfos);
    }

    /**
     * @return
     * @Author mqy
     * @Description 检索
     * @Date
     * @Param
     **/
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        //创建检索条件
        NativeSearchQueryBuilder builder = creatBasicQuery(searchMap);
        //根据关键字检索
        Map<String, Object> resultMap = searchForPage(builder);
        //查询分类
        List<String> categoryList = searchCategoryList(builder);
        resultMap.put("categoryList", categoryList);
        //品牌分组查询
        List<String> brandList = searchBrandList(builder);
        resultMap.put("brandList", brandList);
        //规格统计
        Map<String, Set<String>> specList = searchSpec(builder);
        resultMap.put("specList", specList);
        return resultMap;
    }

    //规格统计
    private Map<String, Set<String>> searchSpec(NativeSearchQueryBuilder builder) {
        builder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword").size(10000));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);
        Aggregations aggregations = aggregatedPage.getAggregations();
        StringTerms skuSpec = aggregations.get("skuSpec");
        List<StringTerms.Bucket> buckets = skuSpec.getBuckets();
        List<String> list = new ArrayList<>();
        for (StringTerms.Bucket bucket : buckets) {
            String spec = bucket.getKeyAsString();
            list.add(spec);
        }
        Map<String, Set<String>> resultMap = result(list);
        return resultMap;
    }

    //将查出的spec集合封装成map集合
    private Map<String, Set<String>> result(List<String> list) {
        //list的值:{"":"","":""}{"":"","":""}{"":"","":""}
        Map<String, Set<String>> resultMap = new HashMap<>();
        if (list != null && list.size() > 0) {
            for (String s : list) {
                //s的值:{"":"","":""}
                Map<String, String> map = JSON.parseObject(s, Map.class);
                //map的值：["":""]
                //map的值：["":""]
                Set<Map.Entry<String, String>> entries = map.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    Set<String> set = resultMap.get(key);
                    if (set == null) {
                        set = new HashSet<>();
                    }
                    set.add(value);
                    resultMap.put(key, set);
                }
            }
        }
        return resultMap;
    }

    //品牌分组查询
    private List<String> searchBrandList(NativeSearchQueryBuilder builder) {
        builder.addAggregation(AggregationBuilders.terms("skuBland").field("brandName"));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);
        Aggregations aggregations = aggregatedPage.getAggregations();
        StringTerms skuBland = aggregations.get("skuBland");
        List<StringTerms.Bucket> buckets = skuBland.getBuckets();
        List<String> list = new ArrayList<>();
        for (StringTerms.Bucket bucket : buckets) {
            list.add(bucket.getKeyAsString());
        }
        return list;
    }

    //根据关键字检索
    private Map<String, Object> searchForPage(NativeSearchQueryBuilder builder) {
        HighlightBuilder.Field field = new HighlightBuilder.Field("name");
        field.preTags("<font color='red'>");
        field.postTags("</font>");
        builder.withHighlightFields(field);
        SearchResultMapper searchResultMapper = new SearchResultMapper() {

            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                SearchHits hits = response.getHits();
                List<T> content = new ArrayList<>();
                if (hits != null){
                    for (SearchHit hit : hits) {
                        // 获取原始的数据（普通的数据）
                        String source = hit.getSourceAsString(); // json
                        SkuInfo skuInfo = JSON.parseObject(source, SkuInfo.class);
                        // 获取的高亮的对象
                        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                        HighlightField highlightField = highlightFields.get("name");
                        //HighlightField highlightField = hit.getHighlightFields().get("name");
                        if (highlightField != null){
                            // 获取高亮结果
                            Text[] texts = highlightField.getFragments();
                            String name = texts[0].toString();  // 高亮的商品名称

                            // 替换原始的name
                            skuInfo.setName(name);
                        }
                        content.add((T) skuInfo);
                    }
                }
                return new AggregatedPageImpl<>(content, pageable, hits.getTotalHits());
            }
        };


        NativeSearchQuery build = builder.build();
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(build, SkuInfo.class,searchResultMapper);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("rows", aggregatedPage.getContent());                     // 商品列表数据，普通数据
        resultMap.put("totalElements", aggregatedPage.getTotalElements());      // 总条数
        resultMap.put("totalPages", aggregatedPage.getTotalPages());            // 总页数
        resultMap.put("pageNum", build.getPageable().getPageNumber() + 1); // 页面需要的当前页码
        resultMap.put("pageSize", build.getPageable().getPageSize());     // 每页显示的条数
        return resultMap;
    }


    //查询分类
    private List<String> searchCategoryList(NativeSearchQueryBuilder builder) {
        builder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName"));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);
        Aggregations aggregations = aggregatedPage.getAggregations();
        StringTerms stringTerms = aggregations.get("skuCategory");
        List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
        List<String> list = new ArrayList<>();
        for (StringTerms.Bucket bucket : buckets) {
            list.add(bucket.getKeyAsString());
        }
        return list;
    }

    //创建检索条件
    private NativeSearchQueryBuilder creatBasicQuery(Map<String, String> searchMap) {
        //封装检索条件
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //封装过滤条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (searchMap != null) {
            //1、根据关键字搜索
            String keywords = searchMap.get("keywords");
            if (!StringUtils.isEmpty(keywords)) {
                builder.withQuery(QueryBuilders.matchQuery("name", keywords));
            }
            //2、根据商品分类过滤
            String category = searchMap.get("category");
            if (!StringUtils.isEmpty(category)) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("categoryName", category));
            }
            //3、根据品牌过滤
            String brand = searchMap.get("brand");
            if (!StringUtils.isEmpty(brand)) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("brandName", brand));
            }
            //4、根据规格过滤
            Set<String> keySet = searchMap.keySet();//将所有的key查询出来
            for (String key : keySet) {//遍历所有的key
                if (key.startsWith("spec_")) {//查出以spec_开头的key
                    boolQueryBuilder.must
                            (QueryBuilders.matchQuery("specMap." + key.substring(5) + ".keyword"
                                    , searchMap.get(key)));//封装条件"specMap."+key.substring(5)+".keyword"与索引库一致
                }
            }
            //5、根据价格过滤
            String price = searchMap.get("price");//例如price可能是：15-90
            if (!StringUtils.isEmpty(price)) {
                String[] priceArray = price.split("-");//将15-90分成【15，90】
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gte(priceArray[0]));//设置最小值，即大于等于
                if (priceArray.length == 2) {//有可能没有最大值，即‘10-’此时的数组的长度为1，所以不需要最大值即小于等于
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lte(priceArray[1]));
                }
            }
            //6、结果排序
            String sortRule = searchMap.get("sortRule");//排序规则，即升序还是降序
            String sortField = searchMap.get("sortField");//排序字段，根据哪个字段排序
            if (!StringUtils.isEmpty(sortField)) {
                builder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.valueOf(sortRule)));
            }
        }
        builder.withFilter(boolQueryBuilder);
        //添加分页条件
        String pageNum = searchMap.get("pageNum");
        if (StringUtils.isEmpty(pageNum)) {
            pageNum = "1";
        }
        int page = Integer.parseInt(pageNum);
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        builder.withPageable(pageable);
        return builder;
    }

}
