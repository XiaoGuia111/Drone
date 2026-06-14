package com.uav.system.domain.vo;

import java.util.List;

//通用分页结果封装
public class PageResult<T> {
    /** 当前页的数据列表 */
    private List<T> content;
    /** 符合条件的总记录数 */
    private long totalElements;
    /** 总页数（自动计算：ceil(totalElements / size)） */
    private int totalPages;
    /** 当前页码（从 1 开始） */
    private int page;
    /** 每页大小 */
    private int size;

    public PageResult() {
    }

    /**
     * 构造分页结果
     * <p>自动根据 totalElements 和 size 计算 totalPages。</p>
     *
     * @param content       当前页数据列表
     * @param totalElements 总记录数
     * @param page          当前页码
     * @param size          每页大小
     */
    public PageResult(List<T> content, long totalElements, int page, int size) {
        this.content = content;
        this.totalElements = totalElements;
        this.page = page;
        this.size = size;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
