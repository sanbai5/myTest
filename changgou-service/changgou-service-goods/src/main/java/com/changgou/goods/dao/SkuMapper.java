package com.changgou.goods.dao;

import com.changgou.goods.pojo.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

/****
 * @Author:shenkunlin
 * @Description:Sku的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface SkuMapper extends Mapper<Sku> {
    /**
     * @return
     * @Author mqy
     * @Description 更新库存
     * @Date
     * @Param
     **/
    @Update("UPDATE tb_sku SET num = num - #{num} WHERE id = #{id} AND num > #{num}")
    int decrCount(@Param("num") Integer num, @Param("id")Long id);
}
