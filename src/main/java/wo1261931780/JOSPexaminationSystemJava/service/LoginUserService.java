package wo1261931780.JOSPexaminationSystemJava.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import wo1261931780.JOSPexaminationSystemJava.dao.LoginUserMapper;
import wo1261931780.JOSPexaminationSystemJava.entity.LoginUser;

import java.util.List;

/**
*Created by Intellij IDEA.
*Project:JOSP-javaFirst
*Package:wo1261931780.javaFirst.demo
*@author liujiajun_junw
*@Date 2023-03-20-20  星期四
*@description
*/
@Service
public class LoginUserService extends ServiceImpl<LoginUserMapper, LoginUser> {

    
    public int updateBatch(List<LoginUser> list) {
        return baseMapper.updateBatch(list);
    }
    
    public int updateBatchSelective(List<LoginUser> list) {
        return baseMapper.updateBatchSelective(list);
    }
    
    public int batchInsert(List<LoginUser> list) {
        return baseMapper.batchInsert(list);
    }
    
    public boolean insertOrUpdate(LoginUser record) {
        return baseMapper.insertOrUpdate(record);
    }
    
    public int insertOrUpdateSelective(LoginUser record) {
        return baseMapper.insertOrUpdateSelective(record);
    }
}
