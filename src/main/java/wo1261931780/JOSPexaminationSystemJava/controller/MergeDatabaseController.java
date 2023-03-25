package wo1261931780.JOSPexaminationSystemJava.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wo1261931780.JOSPexaminationSystemJava.config.ShowResult;
import wo1261931780.JOSPexaminationSystemJava.entity.MergeDatabase;
import wo1261931780.JOSPexaminationSystemJava.service.MergeDatabaseService;

/**
 * Created by Intellij IDEA.
 * Project:JOSP-examinationSystemJava
 * Package:wo1261931780.JOSPexaminationSystemJava.controller
 *
 * @author liujiajun_junw
 * @Date 2023-03-04-40  星期六
 * @description
 */
@RestController
@RequestMapping("/vue-element-admin/MergeDatabase")
public class MergeDatabaseController {
	
	@Autowired
	private MergeDatabaseService mergeDatabaseService;
	
	@GetMapping("/list")
	public ShowResult<Page<MergeDatabase>> showMePage(@RequestParam Integer page
			, @RequestParam Integer limit
			, @RequestParam String sort
			, String studentName, String importance, String type) {
		Page<MergeDatabase> pageInfo = new Page<>();// 页码，每页条数
		pageInfo.setCurrent(page);// 当前页
		pageInfo.setSize(limit);// 每页条数
		LambdaQueryWrapper<MergeDatabase> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.like(studentName != null, MergeDatabase::getStudentName, studentName);
		//lambdaQueryWrapper.like(importance != null, MergeDatabase::getImportance, importance);
		//lambdaQueryWrapper.like(type != null, MergeDatabase::getType, type);
		lambdaQueryWrapper.orderByAsc(MergeDatabase::getRank);
		
		Page<MergeDatabase> testPage = mergeDatabaseService.page(pageInfo, lambdaQueryWrapper);
		return ShowResult.sendSuccess(testPage);
	}
}
