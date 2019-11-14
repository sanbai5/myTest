package com.changgou.canal.lisenter;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.DeleteListenPoint;
import com.xpand.starter.canal.annotation.InsertListenPoint;
import com.xpand.starter.canal.annotation.UpdateListenPoint;

import java.util.List;

/**
 * @ClassName CanalDataEventLisenter
 * @Description
 * @Author sanbai5
 * @Date 16:30 2019/9/22
 * @Version 2.1
 **/

@CanalEventListener
public class CanalDataEventListener {

    /**
     * @param
     * @param
     * @return void
     * @author mqy
     * @Description 监听数据库的新增操作
     * @Date 12:18 2019/9/22
     **/
    @InsertListenPoint
    public void onEventInsert(CanalEntry.EntryType entryType, CanalEntry.RowData rowData) {
        List<CanalEntry.Column> list = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : list) {
            System.out.println("列名：" + column.getName() + "<--->列值：" + column.getValue());
        }
    }

    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.EntryType entryType, CanalEntry.RowData rowData) {
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            System.out.println("列名：" + column.getName() + "<--->列值：" + column.getValue());
        }
        for (CanalEntry.Column column : beforeColumnsList) {
            System.out.println("列名：" + column.getName() + "<--->列值：" + column.getValue());
        }
    }

    @DeleteListenPoint
    public void onEventDelete(CanalEntry.EntryType entryType, CanalEntry.RowData rowData) {
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        for (CanalEntry.Column column : beforeColumnsList) {
            System.out.println("列名：" + column.getName() + "<--->列值：" + column.getValue());
        }
    }
}
