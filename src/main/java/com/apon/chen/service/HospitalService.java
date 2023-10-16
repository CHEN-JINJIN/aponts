package com.apon.chen.service;

import com.apon.chen.entity.Hospital;
import com.apon.chen.mapper.HospitalMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author chen
 * service,mybatisplus
 */
@Service
public class HospitalService extends ServiceImpl<HospitalMapper, Hospital> {
    public boolean saveHospital(Hospital hospital) {
        return saveOrUpdate(hospital);
    }

}
