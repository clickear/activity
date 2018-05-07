package cn.huimin.process.web.dto;

import java.io.Serializable;

/**
 * Created by wyp on 2017/5/5.
 * 查询需要的参数
 */
public class APIQuerySimpleResult extends APISimpleResult  implements Serializable{
    private static final long serialVersionUID = 1L;
    protected Long totalCount;
    protected Integer totalPage;

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    @Override
    public String toString() {
        return "APIQuerySimpleResult{" +
                "totalCount=" + totalCount +
                ", totalPage=" + totalPage +
                '}';
    }
}
