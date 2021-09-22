package com.lifesup.gbtd.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableCustom implements Pageable {

    private final Integer page;
    private final Integer pageSize;
    private final Sort sort;

    public PageableCustom(Integer page, Integer pageSize, Sort sort) {
        this.page = (page != null && page > 0) ? page - 1 : 0;
        this.pageSize = (pageSize == null || pageSize <= 0) ? 10 : pageSize;
        this.sort = (sort == null) ? Sort.unsorted() : sort;
    }

    @Override
    public int getPageNumber() {
        return this.page;
    }

    @Override
    public int getPageSize() {
        return this.pageSize;
    }

    @Override
    public long getOffset() {
        return (long) this.page * (long) this.pageSize;
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    @Override
    public Pageable next() {
        return PageRequest.of(this.getPageNumber() + 1, this.getPageSize(), this.getSort());
    }

    @Override
    public Pageable previousOrFirst() {
        return this.hasPrevious() ? this.previous() : this.first();
    }


    public PageRequest previous() {
        return this.getPageNumber() == 0
                ? PageRequest.of(0, this.getPageSize(), this.getSort())
                : PageRequest.of(this.getPageNumber() - 1, this.getPageSize(), this.getSort());
    }

    @Override
    public Pageable first() {
        return PageRequest.of(0, this.getPageSize(), this.getSort());
    }

    @Override
    public boolean hasPrevious() {
        return this.page > 0;
    }
}
