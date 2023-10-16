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
@TableName(value = "hospital")
public class Hospital {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String hospital_no;
    private String hospital_name;
    private String region;  //大区
    private String province;    //省份
    private String hospital_level;
    private String department;
    private String sys_model;
    private String sys_version;
    private Integer customized; //是否定制1-定制，0-未定制
    private Integer ssh_port;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date install_time;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date update_time;
    private Integer visit;
    private Integer hospital_type;
    private Integer pump_num;
    private Integer hospital_star;
    private String other;
    private Integer ssh_status;
    private String emitter_type;

}
