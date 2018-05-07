package cn.huimin.process.web.service.imp;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.reflect.ParameterizedType;


/**
 * 查询activity的数据库
 * @param <MapperClass>
 */
public abstract class BaseActivityServiceImpl<MapperClass> implements InitializingBean{

    protected MapperClass mapperClass;

    public void set_sqlSessionFactory(SqlSessionFactory _sqlSessionFactory) {
        this._sqlSessionFactory = _sqlSessionFactory;
    }

    @Autowired
    @Qualifier("sqlSessionFactory")
    private SqlSessionFactory _sqlSessionFactory;


    @Override
    public void afterPropertiesSet() throws Exception {
        MapperFactoryBean factory = new MapperFactoryBean();
        factory.setMapperInterface((Class<MapperClass>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0]);
        factory.setSqlSessionFactory(_sqlSessionFactory);
        factory.afterPropertiesSet();

        mapperClass = (MapperClass) factory.getObject();
    }
}
