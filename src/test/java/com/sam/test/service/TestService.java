package com.sam.test.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sam.common.repository.NativeQueryRepository;
import com.sam.common.repository.search.Searchable;

@Service
public class TestService {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Page findAllId(Searchable searchable,int pageNo){
		Pageable pageable = new PageRequest(pageNo-1, PAGE_SIZE);

		List list = repository.findAll("select id,content from module_laborunion_home_article_data", searchable);

		Long count = repository.count("select count(*) from module_laborunion_home_article_data", searchable);

		return new PageImpl<>(list, pageable, count);
		
	}
	
	@Resource
	private NativeQueryRepository repository;
	
	private static final int PAGE_SIZE = 10;
}
