package com.sam.test.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;

import com.sam.common.repository.search.SearchOperator;
import com.sam.common.repository.search.Searchable;
import com.sam.test.service.TestService;

public class Main {
	public static void main(String[] args) {
		ApplicationContext app = new ClassPathXmlApplicationContext(new String[]{"spring-config.xml"});
		
		TestService service = app.getBean(TestService.class);
		
		Searchable searchable = Searchable.newSearchable();
		searchable.addSort(Direction.ASC, "id");
		searchable.addSearchFilter("content", SearchOperator.like, "text");
		
		Page page = service.findAllId(searchable,1);
		for(Object objs : page.getContent()){
			Object[] obj = (Object[]) objs;
			for(Object o : obj){
				System.out.println(o.toString());
			}
		}

	}
}
