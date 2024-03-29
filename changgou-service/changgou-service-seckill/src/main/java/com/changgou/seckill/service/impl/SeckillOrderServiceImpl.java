package com.changgou.seckill.service.impl;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import com.changgou.seckill.service.SeckillOrderService;
import com.changgou.seckill.task.MultiThreadingCreateOrder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:SeckillOrder业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired(required = false)
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired(required = false)
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private MultiThreadingCreateOrder multiThreadingCreateOrder;

    /**
     * @return
     * @Author mqy
     * @Description 支付成功更新订单
     * @Date
     * @Param
     **/
    @Override
    public void updateStatus(String username, String out_trade_no, String transaction_id, String time_end) {
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);
        if (seckillOrder != null) {
            seckillOrder.setPayTime(new Date());//支付时间
            seckillOrder.setTransactionId(transaction_id);//支付流水号
            seckillOrder.setStatus("1");//支付状态
            seckillOrderMapper.insertSelective(seckillOrder);
            //删除redis中的数据
            redisTemplate.boundHashOps("SeckillOrder").delete(username);
            redisTemplate.boundHashOps("UserQueryCount" + seckillOrder.getId()).delete(username);
            redisTemplate.boundHashOps("UserQueueStatus").delete(username);
        }
    }

    /**
     * @return
     * @Author mqy
     * @Description 支付失败删除订单
     * @Date
     * @Param
     **/
    @Override
    public void deleteOrder(String username, String out_trade_no, String transaction_id, String time_end) {
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundHashOps("UserQueueStatus").get(username);
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);
        if (seckillOrder != null && seckillStatus != null) {
            redisTemplate.boundHashOps("SeckillOrder").delete(username);
            Long seckillId = seckillStatus.getGoodsId();
            redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillId).leftPush(seckillId);
            Long seckillGoodsCount = redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillId, 1);
            String time = seckillStatus.getTime();
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + time).get(seckillId);
            seckillGoods.setStockCount(seckillGoodsCount.intValue());
            redisTemplate.boundHashOps("SeckillGoods_" + time).put(seckillId, seckillGoods);
            redisTemplate.boundHashOps("UserQueueStatus").delete(username);
            redisTemplate.boundHashOps("UserQueryCount" + seckillId).delete(username);
        }
    }

    /**
     * @return
     * @Author mqy
     * @Description 查询抢购的订单状态
     * @Date
     * @Param
     **/
    @Override
    public SeckillStatus queryStatus(String username) {
        SeckillStatus queryStatus = (SeckillStatus) redisTemplate.boundHashOps("UserQueueStatus").get(username);
        return queryStatus;
    }

    /**
     * @return
     * @Author mqy
     * @Description 将秒杀订单存入redis数据库
     * @Date
     * @Param
     **/

    @Override
    public Boolean add(String time, Long seckillId, String userId) {

        Long userQueryCount = redisTemplate.boundHashOps("UserQueryCount" + seckillId).increment(userId, 1);
        if (userQueryCount > 1) {
            throw new RuntimeException("对不起，不能重复下单");
        }
        //异步执行
        multiThreadingCreateOrder.creatOrder();
        SeckillStatus seckillStatus = new SeckillStatus(userId, new Date(), 1, seckillId, time);
        //将用用户抢单信息存入redis队列中
        redisTemplate.boundListOps("SeckillOrderQueue").leftPush(seckillStatus);
        //将抢单状态存入redis中
        redisTemplate.boundHashOps("UserQueueStatus").put(userId, seckillStatus);
        return true;
    }

    /**
     * SeckillOrder条件+分页查询
     *
     * @param seckillOrder 查询条件
     * @param page         页码
     * @param size         页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<SeckillOrder> findPage(SeckillOrder seckillOrder, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExample(seckillOrder);
        //执行搜索
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectByExample(example));
    }

    /**
     * SeckillOrder分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<SeckillOrder> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectAll());
    }

    /**
     * SeckillOrder条件查询
     *
     * @param seckillOrder
     * @return
     */
    @Override
    public List<SeckillOrder> findList(SeckillOrder seckillOrder) {
        //构建查询条件
        Example example = createExample(seckillOrder);
        //根据构建的条件查询数据
        return seckillOrderMapper.selectByExample(example);
    }


    /**
     * SeckillOrder构建查询对象
     *
     * @param seckillOrder
     * @return
     */
    public Example createExample(SeckillOrder seckillOrder) {
        Example example = new Example(SeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (seckillOrder != null) {
            // 主键
            if (!StringUtils.isEmpty(seckillOrder.getId())) {
                criteria.andEqualTo("id", seckillOrder.getId());
            }
            // 秒杀商品ID
            if (!StringUtils.isEmpty(seckillOrder.getSeckillId())) {
                criteria.andEqualTo("seckillId", seckillOrder.getSeckillId());
            }
            // 支付金额
            if (!StringUtils.isEmpty(seckillOrder.getMoney())) {
                criteria.andEqualTo("money", seckillOrder.getMoney());
            }
            // 用户
            if (!StringUtils.isEmpty(seckillOrder.getUserId())) {
                criteria.andEqualTo("userId", seckillOrder.getUserId());
            }
            // 创建时间
            if (!StringUtils.isEmpty(seckillOrder.getCreateTime())) {
                criteria.andEqualTo("createTime", seckillOrder.getCreateTime());
            }
            // 支付时间
            if (!StringUtils.isEmpty(seckillOrder.getPayTime())) {
                criteria.andEqualTo("payTime", seckillOrder.getPayTime());
            }
            // 状态，0未支付，1已支付
            if (!StringUtils.isEmpty(seckillOrder.getStatus())) {
                criteria.andEqualTo("status", seckillOrder.getStatus());
            }
            // 收货人地址
            if (!StringUtils.isEmpty(seckillOrder.getReceiverAddress())) {
                criteria.andEqualTo("receiverAddress", seckillOrder.getReceiverAddress());
            }
            // 收货人电话
            if (!StringUtils.isEmpty(seckillOrder.getReceiverMobile())) {
                criteria.andEqualTo("receiverMobile", seckillOrder.getReceiverMobile());
            }
            // 收货人
            if (!StringUtils.isEmpty(seckillOrder.getReceiver())) {
                criteria.andEqualTo("receiver", seckillOrder.getReceiver());
            }
            // 交易流水
            if (!StringUtils.isEmpty(seckillOrder.getTransactionId())) {
                criteria.andEqualTo("transactionId", seckillOrder.getTransactionId());
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
        seckillOrderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改SeckillOrder
     *
     * @param seckillOrder
     */
    @Override
    public void update(SeckillOrder seckillOrder) {
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 增加SeckillOrder
     *
     * @param seckillOrder
     */
    @Override
    public void add(SeckillOrder seckillOrder) {
        seckillOrderMapper.insert(seckillOrder);
    }

    /**
     * 根据ID查询SeckillOrder
     *
     * @param id
     * @return
     */
    @Override
    public SeckillOrder findById(Long id) {
        return seckillOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询SeckillOrder全部数据
     *
     * @return
     */
    @Override
    public List<SeckillOrder> findAll() {
        return seckillOrderMapper.selectAll();
    }
}
