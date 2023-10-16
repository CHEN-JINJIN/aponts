package com.apon.chen.controller;

import cn.hutool.core.collection.CollUtil;
import com.apon.chen.common.Result;
import com.apon.chen.entity.Hospital;
import com.apon.chen.mapper.HospitalMapper;
import com.apon.chen.service.HospitalService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chen
 * <p>
 * HospitalController
 */
@RestController
@RequestMapping("/hospital")
@Slf4j
public class HospitalController {

    @Autowired
    private HospitalMapper hospitalMapper;

    @Autowired
    private HospitalService hospitalService;

    //    GET:查询   POST：新增   PUT：修改    DELETE：删除
    @ApiOperation(value = "查询所有")
    @GetMapping("")
    public List<Hospital> selectAll() {
        return hospitalService.list();
    }

    @ApiOperation(value = "系统总览")
    @GetMapping("/sysModel")
    public Result selectSys() {
        List<Hospital> list = hospitalMapper.selectAllHospital();
        int total = list.size();
        //在集合中查询sys_model为"4G"的集合
        List<Hospital> hospitalList = list.stream().filter(item -> "4G".equals(item.getSys_model())).collect(Collectors.toList());
        int iot = hospitalList.size();
        int wifi = total - iot;
        //定义Map的时候,泛型中前面的K是指键值对中Key的数据类型,后面的V指的是键值对中value的数据类型
        Map<String, Integer> map = new HashMap<>();//Map的实现类有很多,我们主要用HashMap和TreeMap
        map.put("total", total);//Map的添加元素的方法是put
        map.put("iot", iot);
        map.put("wifi", wifi);

        //CollUtil是hutool组件
        return Result.success(CollUtil.newArrayList(map));
    }

    @ApiOperation(value = "区域总览")
    @GetMapping("/region")
    public Result selectRegion() {
        List<Hospital> list = hospitalMapper.selectAllHospital();
        //对数据进行分组并且计算该组中元素的数量，Java List GroupBy同样可以帮助我们实现这个功能
        Map regionMap = list.stream().collect(Collectors.groupingBy(Hospital::getRegion, Collectors.counting()));

        List<Hospital> list2023 = hospitalMapper.select2023();
        Map region2023Map = list2023.stream().collect(Collectors.groupingBy(Hospital::getRegion, Collectors.counting()));


        List<Hospital> thisYearList = list.stream().
                filter(item -> item.getInstall_time() != null && item.getInstall_time().toString().compareTo("2023-1-1") < 0)
                .collect(Collectors.toList());
        Map thisYearMap = thisYearList.stream().collect(Collectors.groupingBy(Hospital::getRegion, Collectors.counting()));
        //CollUtil是hutool组件
        return Result.success(CollUtil.newArrayList(regionMap, region2023Map));
    }

    @ApiOperation(value = "全国省份总览")
    @GetMapping("/province")
    public Result selectProvince() {
        List<Hospital> list = hospitalMapper.selectAllHospital();
        //对数据进行分组并且计算该组中元素的数量，Java List GroupBy同样可以帮助我们实现这个功能
        Map provinceMap = list.stream().collect(Collectors.groupingBy(Hospital::getProvince, Collectors.counting()));

        //CollUtil是hutool组件
        return Result.success(CollUtil.newArrayList(provinceMap));
    }

    @ApiOperation(value = "SSH总览")
    @GetMapping("/ssh")
    public Result selectSsh() {
        List<Hospital> list = hospitalMapper.selectAllHospital();
        //在集合中查询ssh_status为 1 的集合
        List<Hospital> onlineList = list.stream().filter(item -> item.getSsh_status() != null && item.getSsh_status() == 1).collect(Collectors.toList());
        int online = onlineList.size();
        List<Hospital> offlineList = list.stream().filter(item -> item.getSsh_status() != null && item.getSsh_status() == 0).collect(Collectors.toList());
        int offline = offlineList.size();

        //定义Map的时候,泛型中前面的K是指键值对中Key的数据类型,后面的V指的是键值对中value的数据类型
        Map<String, Integer> map = new HashMap<>();//Map的实现类有很多,我们主要用HashMap和TreeMap
        map.put("total", online + offline);//Map的添加元素的方法是put
        map.put("online", online);
        map.put("offline", offline);

        //CollUtil是hutool组件
        return Result.success(CollUtil.newArrayList(map));
    }

    //mybatis-plus实现分页查询
    @ApiOperation(value = "分页查询")
    @GetMapping("/page")
    public IPage<Hospital> findPage(@RequestParam Integer pageNum,
                                    @RequestParam Integer pageSize,
                                    @RequestParam(defaultValue = "") String hospital_no, //默认值为空
                                    @RequestParam(defaultValue = "") String hospital_name,
                                    @RequestParam(defaultValue = "") String region,
                                    @RequestParam(defaultValue = "") String province,
                                    @RequestParam(defaultValue = "") String ssh_port,
                                    @RequestParam(defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") Date install_time

    ) {
        IPage<Hospital> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Hospital> queryWrapper = new QueryWrapper<>();

        if (!"".equals(hospital_no)) {
            queryWrapper.like("hospital_no", hospital_no);
        }
        if (!"".equals(hospital_name)) {
            queryWrapper.like("hospital_name", hospital_name);
        }
        if (!"".equals(region)) {
            queryWrapper.like("region", region);
        }
        if (!"".equals(province)) {
            queryWrapper.like("province", province);
        }
        if (!"".equals(ssh_port)) {
            queryWrapper.like("ssh_port", ssh_port);
        }
        if (install_time != null) {
            //CJRQ加一天!!!!  WHERE (CJRQ >= ? AND CJRQ < ?)
            Calendar cal = Calendar.getInstance();
            cal.setTime(install_time);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            Date endTime = cal.getTime();

            queryWrapper.ge("install_time", install_time);
            queryWrapper.lt("install_time", endTime);

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
        queryWrapper.orderByDesc("install_time").orderByAsc("hospital_no");
//        queryWrapper.orderByAsc("hospital_no");
        return hospitalService.page(page, queryWrapper);
    }

    //新增 或 更新
    @ApiOperation(value = "更新或新增hospital")
    @PostMapping("")
    public boolean save(@RequestBody Hospital hospital) {       //@RequestBody前端json对象转成后台java对象
        //调用service，新增或更新
        return hospitalService.saveHospital(hospital);
    }

    //更新ssh_port的status=0
    @ApiOperation(value = "更新ssh_status=0")
    @PostMapping("ssh0/{ssh_port}")
    public Integer updateErrStatus(@PathVariable Integer ssh_port) {       //@RequestBody前端json对象转成后台java对象
        return hospitalMapper.updateErrStatusByPort(ssh_port);
    }

    //ssh状态更新为正常1
    @ApiOperation(value = "更新ssh_status=1")
    @PostMapping("ssh1/{ssh_port}")
    public Integer updateStatus(@PathVariable Integer ssh_port) {

        return hospitalMapper.updateStatusByPort(ssh_port);
    }

    @ApiOperation(value = "删除指定id")
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return hospitalService.removeById(id);
    }


}
