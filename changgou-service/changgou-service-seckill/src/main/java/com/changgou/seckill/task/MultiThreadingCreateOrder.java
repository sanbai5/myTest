package com.changgou.seckill.task;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @ClassName MultiThreadingCreateOrder
 * @Description
 * @Author sanbai5
 * @Date 15:29 2019/10/15
 * @Version 2.1
 **/
@Component
public class MultiThreadingCreateOrder {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired(required = false)
    private SeckillGoodsMapper seckillGoodsMapper;

    @Async
    public void creatOrder() {
        try {
            SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps("SeckillOrderQueue").rightPop();
            String time = seckillStatus.getTime();
            Long seckillId = seckillStatus.getGoodsId();
            String userId = seckillStatus.getUsername();
            Object goodsId = redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillId).rightPop();
            if (goodsId == null) {
                //顶顶顶顶顶顶顶顶顶顶顶
                //删除用户的订单状态
                redisTemplate.boundHashOps("UserQueueStatus").delete(userId);
                //删除用户的下单次数
                redisTemplate.boundHashOps("UserQueryCount" + seckillId).delete(userId);
                return;
            }
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + time).get(seckillId);
            if (seckillGoods == null) {
                throw new RuntimeException("对不起，该商品已售罄");
            }
            SeckillOrder seckillOrder = new SeckillOrder();     // 创建秒杀订单对象
            seckillOrder.setId(idWorker.nextId());              // 订单id
            seckillOrder.setSeckillId(seckillId);               // 秒杀商品id
            seckillOrder.setMoney(seckillGoods.getCostPrice()); // 秒杀金额
            seckillOrder.setUserId(userId);                     // 用户id
            seckillOrder.setCreateTime(new Date());             // 创建时间
            //seckillOrder.setPayTime(new Date());              // 支付时间
            seckillOrder.setStatus("0");

            //将秒杀的商品订单存入redis中
            redisTemplate.boundHashOps("SeckillOrder").put(userId, seckillOrder);

            //将库存减少
            //seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
            Long seckillGoodsCount = redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillId, -1);
            seckillGoods.setStockCount(seckillGoodsCount.intValue());

            if (seckillGoodsCount > 0) {
                //将减一之后的数据存入redis中
                redisTemplate.boundHashOps("SeckillGoods_" + time).put(seckillId, seckillGoods);
            } else {
                //如果商品数量=0，则将数据持久化到数据库中
                seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                //如果商品数量=0，将redis数据库中的此商品删除
                redisTemplate.boundHashOps("SeckillGoods_" + time).delete(seckillId);
            }
            seckillStatus.setStatus(2);
            seckillStatus.setOrderId(seckillOrder.getId());
            seckillStatus.setMoney(Float.valueOf(seckillOrder.getMoney()));
            redisTemplate.boundHashOps("UserQueueStatus").put(userId, seckillStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
