package com.apon.chen.mapper;

import com.apon.chen.entity.Stock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author chen
 */
@Mapper
public interface StockMapper extends BaseMapper<Stock> {
    @Select("SELECT top 1 * FROM [dbo].[stock] ORDER BY update_time desc")
    List<Stock> selectOneStock();

}
