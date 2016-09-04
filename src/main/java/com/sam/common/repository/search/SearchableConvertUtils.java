package com.sam.common.repository.search;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.sam.common.repository.search.exception.InvalidSearchPropertyException;
import com.sam.common.repository.search.exception.InvalidSearchValueException;
import com.sam.common.repository.search.exception.SearchException;
import com.sam.common.repository.search.filter.AndCondition;
import com.sam.common.repository.search.filter.Condition;
import com.sam.common.repository.search.filter.OrCondition;
import com.sam.common.repository.search.filter.SearchFilter;
import com.sam.common.repository.search.util.SpringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public final class SearchableConvertUtils {

    private static volatile ConversionService conversionService;

    public static void setConversionService(ConversionService conversionService) {
        SearchableConvertUtils.conversionService = conversionService;
    }

    public static ConversionService getConversionService() {
        if (conversionService == null) {
            synchronized (SearchableConvertUtils.class) {
                if (conversionService == null) {
                    try {
                        conversionService = SpringUtils.getBean(ConversionService.class);
                    } catch (Exception e) {
                        throw new SearchException("conversionService is null, " +
                                "search param convert must use conversionService. " +
                                "please see [com.chinamobile.iot.portal.common.entity.search.utils." +
                                "SearchableConvertUtils#setConversionService]");
                    }
                }
            }
        }
        return conversionService;
    }

   
    public static <T> void convertSearchValueToEntityValue(final Searchable search, final Class<T> entityClass) {

        if (search.isConverted()) {
            return;
        }

        Collection<SearchFilter> searchFilters = search.getSearchFilters();
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(entityClass);
        beanWrapper.setAutoGrowNestedPaths(true);
        beanWrapper.setConversionService(getConversionService());

        for (SearchFilter searchFilter : searchFilters) {
            convertSearchValueToEntityValue(beanWrapper, searchFilter);


        }
    }

    private static void convertSearchValueToEntityValue(BeanWrapperImpl beanWrapper, SearchFilter searchFilter) {
        if (searchFilter instanceof Condition) {
            Condition condition = (Condition) searchFilter;
            convert(beanWrapper, condition);
            return;
        }

        if (searchFilter instanceof OrCondition) {
            for (SearchFilter orFilter : ((OrCondition) searchFilter).getOrFilters()) {
                convertSearchValueToEntityValue(beanWrapper, orFilter);
            }
            return;
        }

        if (searchFilter instanceof AndCondition) {
            for (SearchFilter andFilter : ((AndCondition) searchFilter).getAndFilters()) {
                convertSearchValueToEntityValue(beanWrapper, andFilter);
            }
            return;
        }


    }

    private static void convert(BeanWrapperImpl beanWrapper, Condition condition) {
        String searchProperty = condition.getSearchProperty();

        //自定义的也不转换
        if (condition.getOperator() == SearchOperator.custom) {
            return;
        }

        //一元运算符不需要计算
        if (condition.isUnaryFilter()) {
            return;
        }


        String entityProperty = condition.getEntityProperty();

        Object value = condition.getValue();

        Object newValue = null;
        boolean isCollection = value instanceof Collection;
        boolean isArray = value != null && value.getClass().isArray();
        if (isCollection || isArray) {
            List<Object> list = new ArrayList();
            if (isCollection) {
                list.addAll((Collection) value);
            } else {
                list = CollectionUtils.arrayToList(value);
            }
            int length = list.size();
            for (int i = 0; i < length; i++) {
                try {
					list.set(i, getConvertedValue(beanWrapper, searchProperty, entityProperty, list.get(i)));
				} catch (org.springframework.beans.TypeMismatchException e) {
					list.set(i, 0L);//如果查询编号框中本来应该输入编号,结果输入了字符,则更改编号为0
				}
            }
            newValue = list;
        } else {
            try {
				newValue = getConvertedValue(beanWrapper, searchProperty, entityProperty, value);
			} catch (org.springframework.beans.TypeMismatchException e) {
				newValue = 0L;//如果查询编号框中本来应该输入编号,结果输入了字符,则更改编号为0
			}
        }
        condition.setValue(newValue);
    }

    private static Object getConvertedValue(
            final BeanWrapperImpl beanWrapper,
            final String searchProperty,
            final String entityProperty,
            final Object value) {

        Object newValue;
        try {

            beanWrapper.setPropertyValue(entityProperty, value);
            newValue = beanWrapper.getPropertyValue(entityProperty);
        } catch (InvalidPropertyException e) {
            throw new InvalidSearchPropertyException(searchProperty, entityProperty, e);
        } catch (org.springframework.beans.TypeMismatchException e){
        	throw e;
        } catch (Exception e) {
            throw new InvalidSearchValueException(searchProperty, entityProperty, value, e);
        }

        return newValue;
    }

}
