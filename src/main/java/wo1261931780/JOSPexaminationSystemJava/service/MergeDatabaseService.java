package wo1261931780.JOSPexaminationSystemJava.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import wo1261931780.JOSPexaminationSystemJava.dao.MergeDatabaseMapper;
import wo1261931780.JOSPexaminationSystemJava.entity.MergeDatabase;
/**
*Created by Intellij IDEA.
*Project:JOSP-examinationSystemJava
*Package:wo1261931780.JOSPexaminationSystemJava.service
*@author liujiajun_junw
*@Date 2023-03-04-38  星期六
*@description
*/
@Service
public class MergeDatabaseService extends ServiceImpl<MergeDatabaseMapper, MergeDatabase> {

    
    
    public int updateBatch(List<MergeDatabase> list) {
        return baseMapper.updateBatch(list);
    }
    
    public int updateBatchSelective(List<MergeDatabase> list) {
        return baseMapper.updateBatchSelective(list);
    }
    
    public int batchInsert(List<MergeDatabase> list) {
        return baseMapper.batchInsert(list);
    }
    
    public int insertOrUpdate(MergeDatabase record) {
        return baseMapper.insertOrUpdate(record);
    }
    
    public int insertOrUpdateSelective(MergeDatabase record) {
        return baseMapper.insertOrUpdateSelective(record);
    }
}
