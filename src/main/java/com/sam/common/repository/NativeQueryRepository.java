package com.sam.common.repository;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.sam.common.repository.search.SearchCallback;
import com.sam.common.repository.search.Searchable;

/**
 * SQL语句生成类。
 * 根据Searchable，生成where语句和order by及limit语句
 */

@Repository
public class NativeQueryRepository {
	@Resource
	private EntityManager entityManager;

	public <M> List<M> findAll(final String ql, final Searchable searchable) {
		return findAll(ql, searchable, SearchCallback.DEFAULT);
	}
	
    public <M> List<M> findAll(final String ql, final Searchable searchable, final SearchCallback searchCallback) {

        StringBuilder s = new StringBuilder(ql);
        s.append(" where 1=1");
        searchCallback.prepareQL(s, searchable);
        searchCallback.prepareOrder(s, searchable);
        
        Query query = entityManager.createNativeQuery(delOneOne(s.toString()));
        searchCallback.setValues(query, searchable);
        searchCallback.setPageable(query, searchable);
        return query.getResultList();
    }

    public long count(final String ql, final Searchable searchable) {
    	return count(ql, searchable, SearchCallback.DEFAULT);
    }
    
    public long count(final String ql, final Searchable searchable, final SearchCallback searchCallback) {

        StringBuilder s = new StringBuilder(ql);
        s.append(" where 1=1");
        searchCallback.prepareQL(s, searchable);
        
        Query query = entityManager.createNativeQuery(delOneOne(s.toString()));
        
        searchCallback.setValues(query, searchable);

        Number count = (Number) query.getSingleResult();
        
        return count.longValue();
    }
  
    /**
     * 删除sql中的1=1
     */
    private static String delOneOne(String s){
    	if(s != null && s.indexOf("1=1") != -1 ){
    		s=s.replaceAll("  ", " ");
    		
    		if( s.indexOf(" where 1=1 order ") != -1 )
    			s=s.replaceAll(" where 1=1 order ", " order ");
    		
    		if( s.indexOf("1=1") == -1 ) return s;
    		
    		if( s.indexOf(" where 1=1 and ") != -1 )
    			s=s.replaceAll(" where 1=1 and ", " where ");
    		
    		if( s.indexOf("1=1") == -1 ) return s;
    		
			if( s.indexOf(" and 1=1 and ") != -1 )
				s=s.replaceAll(" and 1=1 and ", " and ");
			
			if( s.indexOf("1=1") == -1 ) return s;
				
			if( s.indexOf(" where 1=1 or ") != -1 )
				s=s.replaceAll(" where 1=1 or ", " where ");
			
			if( s.indexOf(" and 1=1 or ") != -1 )
				s=s.replaceAll(" and 1=1 or ", " or ");
			
			if( s.indexOf(" or 1=1 and ") != -1 )
				s=s.replaceAll(" or 1=1 and ", " or ");
				
			if( s.indexOf(" or 1=1 or ") != -1 )
				s=s.replaceAll(" or 1=1 or ", " or ");
				
			if( s.indexOf(" 1=1 (") != -1 )
				s=s.replaceAll(" 1=1 (", " (");
				
			if( s.indexOf(") 1=1 ") != -1 )
				s=s.replaceAll(") 1=1 ", ") ");
				
    		return s; 
    	}
    	return s;
    }

}
