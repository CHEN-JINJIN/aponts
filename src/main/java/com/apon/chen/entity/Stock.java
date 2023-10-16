package com.apon.chen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author chen
 * 主表
 */
@Data
@TableName(value = "stock")
public class Stock {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer zzb_three;
    private Integer zzb_three_new;
    private Integer zzb_four;
    private Integer zzb_four_new;
    private Integer emitter_four;
    private Integer emitter_four_wifi;
    private String operator;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date update_time;
}
