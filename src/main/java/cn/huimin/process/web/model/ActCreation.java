package cn.huimin.process.web.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 运行期的活动节点mode实体
 */
public class ActCreation implements RuntimeActivityCreateInterface
{
    String _factoryName;

    String _processDefinitionId;

    public Map<String, Object> getProperties()
    {
        return _properties;
    }

    public void setProperties(Map<String, Object> properties)
    {
        _properties = properties;
    }

    public void setFactoryName(String factoryName)
    {
        _factoryName = factoryName;
    }

    public void setProcessDefinitionId(String processDefinitionId)
    {
        _processDefinitionId = processDefinitionId;
    }

    public void setProcessInstanceId(String processInstanceId)
    {
        _processInstanceId = processInstanceId;
    }

    public void setPropertiesText(String propertiesText)
    {
        _propertiesText = propertiesText;
    }

    String _processInstanceId;

    Map<String, Object> _properties = new HashMap<String, Object>();

    String _propertiesText;


    public void deserializeProperties()  throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        _properties = objectMapper.readValue(_propertiesText, Map.class);
    }


    public String getDoUserId() {
        return doUserId;
    }

    public void setDoUserId(String doUserId) {
        this.doUserId = doUserId;
    }
    //任务节点id
    public  String getActId() {
        return actId;
    }public void   setActId(String actId) {
        this.actId = actId;
    }private String actId;

    private String doUserId;


    public String getFactoryName()
    {
        return _factoryName;
    }


    public String getProcessDefinitionId()
    {
        return _processDefinitionId;
    }

    public String getProcessInstanceId()
    {
        return _processInstanceId;
    }


    public String getPropertiesText()
    {
        return _propertiesText;
    }


    public <T> T getProperty(String name)
    {
        return (T) _properties.get(name);
    }

    public void serializeProperties() throws JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        _propertiesText = objectMapper.writeValueAsString(_properties);
    }


    public <T> void setProperty(String name, T value)
    {
        _properties.put(name, value);
    }
}
