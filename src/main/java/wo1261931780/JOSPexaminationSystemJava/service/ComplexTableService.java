package wo1261931780.JOSPexaminationSystemJava.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import wo1261931780.JOSPexaminationSystemJava.entity.ComplexTable;
import wo1261931780.JOSPexaminationSystemJava.dao.ComplexTableMapper;
/**
*Created by Intellij IDEA.
*Project:JOSP-examinationSystemJava
*Package:wo1261931780.JOSPexaminationSystemJava.service
*@author liujiajun_junw
*@Date 2023-03-16-16  星期六
*@description
*/
@Service
public class ComplexTableService extends ServiceImpl<ComplexTableMapper, ComplexTable> {

    
    public int updateBatch(List<ComplexTable> list) {
        return baseMapper.updateBatch(list);
    }
    
    public int updateBatchSelective(List<ComplexTable> list) {
        return baseMapper.updateBatchSelective(list);
    }
    
    public int batchInsert(List<ComplexTable> list) {
        return baseMapper.batchInsert(list);
    }
    
    public int insertOrUpdate(ComplexTable record) {
        return baseMapper.insertOrUpdate(record);
    }
    
    public int insertOrUpdateSelective(ComplexTable record) {
        return baseMapper.insertOrUpdateSelective(record);
    }
}
