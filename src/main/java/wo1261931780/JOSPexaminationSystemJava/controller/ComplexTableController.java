package wo1261931780.JOSPexaminationSystemJava.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wo1261931780.JOSPexaminationSystemJava.config.ShowResult;
import wo1261931780.JOSPexaminationSystemJava.entity.ComplexTable;
import wo1261931780.JOSPexaminationSystemJava.service.ComplexTableService;

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
@RequestMapping("/vue-element-admin/article")
public class ComplexTableController {
	
	@Autowired
	private ComplexTableService complexTableService;
	
	/**
	 * 分页查询
	 *
	 * @param page       页码
	 * @param limit      每页条数
	 * @param sort       排序
	 * @param title      标题
	 * @param importance 重要性
	 * @param type       类型
	 * @return 分页结果
	 */
	@GetMapping("/list")
	public ShowResult<Page<ComplexTable>> showMePage(@RequestParam Integer page
			, @RequestParam Integer limit
			, @RequestParam String sort
			, String title, String importance, String type) {
		Page<ComplexTable> pageInfo = new Page<>();// 页码，每页条数
		pageInfo.setCurrent(page);// 当前页
		pageInfo.setSize(limit);// 每页条数
		LambdaQueryWrapper<ComplexTable> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.like(title != null, ComplexTable::getTitle, title);
		lambdaQueryWrapper.like(importance != null, ComplexTable::getImportance, importance);
		lambdaQueryWrapper.like(type != null, ComplexTable::getType, type);
		lambdaQueryWrapper.orderByDesc(ComplexTable::getId);
		
		Page<ComplexTable> testPage = complexTableService.page(pageInfo, lambdaQueryWrapper);
		return ShowResult.sendSuccess(testPage);
	}
}
