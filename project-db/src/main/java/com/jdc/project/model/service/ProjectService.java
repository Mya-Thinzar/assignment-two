package com.jdc.project.model.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jdc.project.model.dto.Project;
import com.jdc.project.model.service.utils.ProjectHelper;

@Service
public class ProjectService {

	@Autowired
	private ProjectHelper projectHelper;

	@Autowired
	private SimpleJdbcInsert projectInsert;

	@Autowired
	private NamedParameterJdbcTemplate template;

	private RowMapper<Project> rowMapper;

	public ProjectService() {
		rowMapper = new BeanPropertyRowMapper<>(Project.class);
	}

	public int create(Project project) {
		projectHelper.validate(project);
		return projectInsert.executeAndReturnKey(projectHelper.insertParams(project)).intValue();
	}

	public Project findById(int id) {
		var sql = "select p.id,p.name,p.description,p.start startDate,m.id managerId,m.login_id managerLogin,m.name managerName,p.months\r\n"
				+ "from project p inner join members m on p.manager=m.id where p.id=:id";
		return template.queryForObject(sql, Map.of("id", id), rowMapper);
	}

	public List<Project> search(String project, String manager, LocalDate dateFrom, LocalDate dateTo) {
		var sb = new StringBuffer(
				"select p.id,p.name,p.description,p.start startDate,m.id managerId,m.login_id managerLogin,m.name managerName,p.months\r\n"
						+ "from project p inner join members m on p.manager=m.id where 1 = 1");

		var params = new HashMap<String, Object>();

		if (null != project) {
			sb.append(" and lower(p.name) like :projectName");
			params.put("projectName", project.toLowerCase().concat("%"));
		}

		if (StringUtils.hasLength(manager)) {
			sb.append(" and lower(m.name) like :managerName");
			params.put("managerName", manager.toLowerCase().concat("%"));
		}

		if (null != dateFrom && dateTo == null) {
			sb.append(" and p.start BETWEEN :startDate and :endDate");
			params.put("startDate", dateFrom);
			params.put("endDate", LocalDate.now());
		}

		if (null != dateFrom && null != dateTo) {
			sb.append(" and p.start BETWEEN :startDate and :endDate");
			params.put("startDate", dateFrom);
			params.put("endDate", dateTo);
		}

		if (null != dateTo && dateFrom == null) {
			sb.append(" and p.start BETWEEN :startDate and :endDate");
			params.put("startDate", LocalDate.of(2022, 02, 15));
			params.put("endDate", dateTo);
		}

		return template.queryForStream(sb.toString(), params, rowMapper).map(a -> (Project) a).toList();
	}

	public int update(int id, String name, String description, LocalDate startDate, int month) {
		return template.update(
				"update project set name = :name, description = :description, start = :startDate, months = :month"
						+ " where id = :id",
				Map.of("id", id, "name", name, "description", description, "startDate", startDate, "month", month));
	}

	public int deleteById(int id) {
		return template.update("delete from project where id = :id", Map.of("id", id));
	}

}
