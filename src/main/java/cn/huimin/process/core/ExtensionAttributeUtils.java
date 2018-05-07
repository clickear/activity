package cn.huimin.process.core;

import org.activiti.bpmn.model.ExtensionAttribute;
/**
 * 
 *jz 属性扩展类
 */
public class ExtensionAttributeUtils  implements HmXMLConstants{
	public static ExtensionAttribute generate(String key,String val){
		ExtensionAttribute ea=new ExtensionAttribute();
		ea.setNamespace(NEMESPACE);
		ea.setName(key);
		ea.setNamespacePrefix(NEMESPACE_PREFIX);
		ea.setValue(val);
		return ea;
		
	}
}
