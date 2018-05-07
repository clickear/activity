package cn.huimin.process.web.service;

import cn.huimin.process.web.dto.Page;
import cn.huimin.process.web.model.Replace;

import java.util.List;

/**
 * 设置代签人
 */
public interface ReplaceService {
        public void insert(Replace replace);

        public void deleteById(List<String> id);

        public List<Replace> query(Replace replace);

        public Page queryPage(Replace replace, Integer start, Integer max);

        /**
         * 更新同意的状态
         * @param agree
         * @param id
         */
        public void updateAgree( Integer agree, Integer id);

}
