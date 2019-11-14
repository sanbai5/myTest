package com.changgou.seckill.timer;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.service.SeckillGoodsService;
import entity.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @ClassName SeckillGoodsPushTask
 * @Description 开启定时任务
 * @Author sanbai5
 * @Date 20:24 2019/10/14
 * @Version 2.1
 **/
@Component
public class SeckillGoodsPushTask {
    @Autowired(required = false)
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0/5 * * * * ?")
    public void pushGoodsToRedis() {
        System.out.println("每十五秒执行一次");
        // 将数据库中的数据压入数据库
        List<Date> dateMenus = DateUtil.getDateMenus();//获取时间段日期
        for (Date dateMenu : dateMenus) {
            String patternYyyymmddhh = DateUtil.PATTERN_YYYYMMDDHH;
            String time = DateUtil.data2str(dateMenu, patternYyyymmddhh);//time=2019101420
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("status", "1");            // 审核通过
            criteria.andGreaterThan("stockCount", 0);          // 库存大于0
            criteria.andGreaterThanOrEqualTo("startTime", dateMenu); // 开始时间等于当前时间或者大于
            criteria.andLessThanOrEqualTo("endTime", DateUtil.addDateHour(dateMenu, 2));// 结束时间少于当前时间的2小时
            // 如果redis数据库中不存在当前时间的数据，则设置查询条件为主键不是查到的ID
            Set keys = redisTemplate.boundHashOps("SeckillGoods_" + time).keys();
            if (keys != null && keys.size() > 0) {
                criteria.andNotIn("id", keys);
            }
            //获取符合时间的Goods清单
            List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
            if (seckillGoodsList != null && seckillGoodsList.size() > 0) {
                for (SeckillGoods seckillGoods : seckillGoodsList) {
                    redisTemplate.boundHashOps("SeckillGoods_" + time).put(seckillGoods.getId(), seckillGoods);
                    //获取该商品的个数，封装成一个数组
                    Long[] idArrays = pushId(seckillGoods.getStockCount(), seckillGoods.getId());
                    //将商品的放入redis队列中
                    redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillGoods.getId()).leftPushAll(idArrays);
                    redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillGoods.getId(), seckillGoods.getStockCount());

                }
            }
        }
    }

    /**
     * @return
     * @Author mqy
     * @Description 获取订单id集合
     * @Date
     * @Param
     **/
    private Long[] pushId(Integer stockCount, Long id) {
        if (stockCount > 0) {
            Long[] ids = new Long[stockCount];
            for (int i = 0; i < stockCount; i++) {
                ids[i] = id;
            }
            return ids;
        }
        return null;
    }
}
