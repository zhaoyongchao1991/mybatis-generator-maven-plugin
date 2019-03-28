package com.seeyu.plugin;

import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.internal.util.messages.Messages;

/**
 * @author seeyu
 * @date 2019/3/28
 */
public class CustomXmlGenerator extends AbstractXmlGenerator {

    public CustomXmlGenerator(IntrospectedTable introspectedTable){
        this.introspectedTable = introspectedTable;
    }

    /*

    <resultMap id="BaseResultMap" type="com.example.demo.biz.entity.User" extends="com.example.demo.biz.dao.mapper.base.BaseUserMapper.BaseResultMap" >

    </resultMap>

    */
    protected XmlElement getSqlMapElement() {
        //FullyQualifiedTable table = this.introspectedTable.getFullyQualifiedTable();
        //this.progressCallback.startTask(Messages.getString("Progress.12", table.toString()));
        XmlElement answer = new XmlElement("mapper");
       //String namespace = this.introspectedTable.getMyBatis3SqlMapNamespace();
        answer.addAttribute(new Attribute("namespace", MapperPlugin.getCustomMapperType(introspectedTable).getFullyQualifiedName()));
        //this.context.getCommentGenerator().addRootComment(answer);

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
