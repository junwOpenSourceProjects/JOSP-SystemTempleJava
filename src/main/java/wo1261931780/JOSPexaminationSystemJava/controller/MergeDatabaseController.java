package wo1261931780.JOSPexaminationSystemJava.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@GetMapping
	public ShowResult<Page<MergeDatabase>> showMePage(@RequestParam Integer pageSize, @RequestParam Integer currentPage) {
		Page<MergeDatabase> pageInfo = new Page<>();
		pageInfo.setSize(pageSize);
		pageInfo.setCurrent(currentPage);
		Page<MergeDatabase> testPage = mergeDatabaseService.page(pageInfo);
		return ShowResult.sendSuccess(testPage);
	}
	
	@DeleteMapping("/{id}")
	public ShowResult<String> delOne(@PathVariable Integer id) {
		if (StrUtil.isBlankIfStr(id)) {
			return ShowResult.sendError("id不能为空");
		}
		boolean removeById = mergeDatabaseService.removeById(id);
		return removeById ? ShowResult.sendSuccess("删除成功") : ShowResult.sendError("删除失败");
	}
}
