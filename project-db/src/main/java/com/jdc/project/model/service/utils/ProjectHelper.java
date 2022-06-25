package com.jdc.project.model.service.utils;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.jdc.project.model.ProjectDbException;
import com.jdc.project.model.dto.Project;

@Component
public class ProjectHelper {
	
	@Value("${project.empty.name}")
	private String noName;
	@Value("${project.empty.manager}")
	private String noManager;
	@Value("${project.empty.start}")
	private String noStartDate;	

		
	public void validate(Project dto) {
		
		if(!StringUtils.hasLength(dto.getName())) {
			throw new ProjectDbException(noName);
		}
		if(dto.getManagerId() == 0) {
			throw new ProjectDbException(noManager);
		}
		
		if(dto.getStartDate() == null) {
			throw new ProjectDbException(noStartDate);
		}
	}

	public Map<String, Object> insertParams(Project dto) {
		var map= new HashMap<String, Object>();
		map.put("name", dto.getName());
		map.put("description", dto.getDescription());
		map.put("start", Date.valueOf(dto.getStartDate()));
		map.put("months", dto.getMonths());
		map.put("manager", dto.getManagerId());
		return map;
	}
}
