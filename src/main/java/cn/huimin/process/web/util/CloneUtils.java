package cn.huimin.process.web.util;

import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;

import java.lang.reflect.Field;

public abstract class CloneUtils
{
    /**
     * 克隆对象
     * @param source
     * @param target
     * @param fieldNames
     */
    public static void copyFields(Object source, Object target, String... fieldNames)
    {
        Assert.assertNotNull(source);
        Assert.assertNotNull(target);
        Assert.assertSame(source.getClass(), target.getClass());

        for (String fieldName : fieldNames)
        {
            try
            {
                Field field = FieldUtils.getField(source.getClass(), fieldName, true);
                field.setAccessible(true);
                field.set(target, field.get(source));
            }
            catch (Exception e)
            {
                Logger.getLogger(CloneUtils.class).warn(e.getMessage());
            }
        }
    }
}
