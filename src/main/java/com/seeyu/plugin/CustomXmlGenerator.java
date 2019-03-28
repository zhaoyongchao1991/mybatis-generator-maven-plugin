package com.seeyu.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.AbstractXmlGenerator;

/**
 * @author seeyu
 * @date 2019/3/28
 */
public class CustomXmlGenerator extends AbstractXmlGenerator {

    public CustomXmlGenerator(IntrospectedTable introspectedTable){
        this.introspectedTable = introspectedTable;
    }

    protected XmlElement getSqlMapElement() {
        XmlElement answer = new XmlElement("mapper");
        answer.addAttribute(new Attribute("namespace", MapperPlugin.getCustomMapperType(introspectedTable).getFullyQualifiedName()));
        XmlElement resultMap = new XmlElement("resultMap");
        resultMap.addAttribute(new Attribute("id", "BaseResultMap"));
        resultMap.addAttribute(new Attribute("type", this.introspectedTable.getBaseRecordType()));
        resultMap.addAttribute(new Attribute("extends", MapperPlugin.getBaseMapperType(this.introspectedTable).getFullyQualifiedName() + "." + "BaseResultMap"));
        answer.addElement(resultMap);
        return answer;
    }


    @Override
    public Document getDocument() {
        Document document = new Document("-//mybatis.org//DTD Mapper 3.0//EN", "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
        document.setRootElement(this.getSqlMapElement());
        return document;
    }
}
