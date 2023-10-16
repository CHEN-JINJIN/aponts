package com.apon.chen.service;

import com.apon.chen.entity.Stock;
import com.apon.chen.mapper.StockMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chen
 * service,mybatisplus
 */
@Service
public class StockService extends ServiceImpl<StockMapper, Stock> {

    //保存更新
    public boolean saveStock(Stock stock) {

        return saveOrUpdate(stock);
    }

    //查询所有
    public List<Stock> getAllStock() {

        return list();
    }

}
