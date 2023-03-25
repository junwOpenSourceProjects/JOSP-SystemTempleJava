package wo1261931780.JOSPexaminationSystemJava.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wo1261931780.JOSPexaminationSystemJava.entity.ComplexTable;

/**
*Created by Intellij IDEA.
*Project:JOSP-examinationSystemJava
*Package:wo1261931780.JOSPexaminationSystemJava.dao
*@author liujiajun_junw
*@Date 2023-03-16-16  星期六
*@description 
*/
@Mapper
public interface ComplexTableMapper extends BaseMapper<ComplexTable> {
    int updateBatch(List<ComplexTable> list);

    int updateBatchSelective(List<ComplexTable> list);

    int batchInsert(@Param("list") List<ComplexTable> list);

    int insertOrUpdate(ComplexTable record);

    int insertOrUpdateSelective(ComplexTable record);
}