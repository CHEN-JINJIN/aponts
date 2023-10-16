package com.apon.chen.controller;

import com.apon.chen.entity.Stock;
import com.apon.chen.mapper.StockMapper;
import com.apon.chen.service.StockService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author chen
 * <p>
 * StockController
 */
@RestController
@RequestMapping("/stock")
@Slf4j
public class StockController {

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private StockService stockService;

    //    GET:查询   POST：新增   PUT：修改    DELETE：删除
    @ApiOperation(value = "查询所有")
    @GetMapping("")
    public List<Stock> selectAll() {
        return stockService.list();
    }

    @ApiOperation(value = "查询最新库存")
    @GetMapping("/last")
    public List<Stock> selectOneStock() {
        return stockMapper.selectOneStock();
    }

    //mybatis-plus实现分页查询
    @ApiOperation(value = "分页查询")
    @GetMapping("/page")
    public IPage<Stock> findPage(@RequestParam Integer pageNum,
                                 @RequestParam Integer pageSize,
                                 @RequestParam(defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") Date update_time

    ) {
        IPage<Stock> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();

        if (update_time != null) {
            //CJRQ加一天!!!!  WHERE (CJRQ >= ? AND CJRQ < ?)
            Calendar cal = Calendar.getInstance();
            cal.setTime(update_time);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            Date endTime = cal.getTime();

            queryWrapper.ge("update_time", update_time);
            queryWrapper.lt("update_time", endTime);

        }
        Calendar cal = Calendar.getInstance();
        //设置开始时间
        cal.setTime(new Date());
        //获取昨天的日历信息 -1
        //cal.add(Calendar.DAY_OF_YEAR, -1);
        //取最近一周
        cal.add(Calendar.DAY_OF_YEAR, -30);
        Date sysTime = cal.getTime();
        //去重 select("DISTINCT userId")
        queryWrapper.orderByDesc("update_time");
        return stockService.page(page, queryWrapper);
    }

    //新增 或 更新
    @ApiOperation(value = "更新或新增Stock")
    @PostMapping("")
    public boolean save(@RequestBody Stock Stock) {       //@RequestBody前端json对象转成后台java对象
        //调用service，新增或更新
        return stockService.saveStock(Stock);
    }

    @ApiOperation(value = "删除指定id")
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return stockService.removeById(id);
    }


}
