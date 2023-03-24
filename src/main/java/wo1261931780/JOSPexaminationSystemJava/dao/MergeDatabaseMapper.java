package wo1261931780.JOSPexaminationSystemJava.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wo1261931780.JOSPexaminationSystemJava.entity.MergeDatabase;

/**
*Created by Intellij IDEA.
*Project:JOSP-examinationSystemJava
*Package:wo1261931780.JOSPexaminationSystemJava.dao
*@author liujiajun_junw
*@Date 2023-03-04-38  星期六
*@description
*/
@Mapper
public interface MergeDatabaseMapper extends BaseMapper<MergeDatabase> {

    int updateBatch(List<MergeDatabase> list);

    int updateBatchSelective(List<MergeDatabase> list);

    int batchInsert(@Param("list") List<MergeDatabase> list);

    int insertOrUpdate(MergeDatabase record);

    int insertOrUpdateSelective(MergeDatabase record);
}
