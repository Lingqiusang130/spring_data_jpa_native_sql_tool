package com.sam.common.repository.search.filter;


import java.util.ArrayList;
import java.util.List;

/**
 * and 条件
 */
public class AndCondition implements SearchFilter {

    private List<SearchFilter> andFilters = new ArrayList();

    AndCondition() {
    }

    public AndCondition add(SearchFilter filter) {
        this.andFilters.add(filter);
        return this;
    }

    public List<SearchFilter> getAndFilters() {
        return andFilters;
    }

    @Override
    public String toString() {
        return "AndCondition{" + andFilters + '}';
    }
}
