package com.changgou.canal.lisenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * @ClassName ContentDataEventListener
 * @Description 自定义监听器
 * @Author sanbai5
 * @Date 19:54 2019/9/22
 * @Version 2.1
 **/
@CanalEventListener
public class ContentDataEventListener {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired(required = false)
    private ContentFeign contentFeign;

    @ListenPoint(destination = "example",
            schema = {"changgou_content"},
            table = {"tb_content"},
            eventType = {CanalEntry.EventType.INSERT, CanalEntry.EventType.UPDATE})
    public void onEventContent(CanalEntry.EntryType entryType, CanalEntry.RowData rowData) {
        String categoryId = getColumnValueForCategoryId(rowData, "category_id");
        System.out.println("String categoryId::::::"+categoryId);
        Result<List<Content>> result =
                contentFeign.findListByCategoryId(Long.parseLong(categoryId));
        List<Content> data = result.getData();
        stringRedisTemplate.boundValueOps("content_" + categoryId).set(JSON.toJSONString(data));
    }

    private String getColumnValueForCategoryId(CanalEntry.RowData rowData, String columnName) {
        List<CanalEntry.Column> columns = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : columns) {
            System.out.println("column.getValue()的值是======"+column.getValue());
            System.out.println("column.getName()的值是======"+column.getName());
            System.out.println("column.getIndex()的值是======"+column.getIndex());
            System.out.println("column.getMysqlType()的值是======"+column.getMysqlType());
            if (column.getName().equals(columnName)) {
                return column.getValue();
            }
        }
        return null;
    }
}
