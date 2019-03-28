package com.seeyu.plugin;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.maven.MavenShellCallback;
import org.mybatis.generator.maven.MyBatisGeneratorMojo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author seeyu
 * @date 2019/3/26
 */
@Slf4j
public class MapperPlugin extends PluginAdapter {


    public static final String prefix = "Base";
    ShellCallback callback = new MavenShellCallback(MyBatisGeneratorMojo.getInstance(), true);

    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        return super.contextGenerateAdditionalJavaFiles();
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> answer = new ArrayList();
        FullyQualifiedJavaType baseType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        FullyQualifiedJavaType type = getCustomMapperType(introspectedTable);
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        interfaze.addSuperInterface(baseType);
        interfaze.addImportedType(baseType);
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));
        interfaze.addAnnotation("@Mapper");
        GeneratedJavaFile gjf = new GeneratedJavaFile(interfaze, this.context.getJavaClientGeneratorConfiguration().getTargetProject(), this.context.getProperty("javaFileEncoding"), this.context.getJavaFormatter());
        if(!generatedFileExists(gjf)){
            answer.add(gjf);
        }
        return answer;
    }


    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
        try{
            List<GeneratedXmlFile> answer = new ArrayList();
            CustomXmlGenerator xmlMapperGenerator = new CustomXmlGenerator(introspectedTable);
            Document document = xmlMapperGenerator.getDocument();
            GeneratedXmlFile gxf = new GeneratedXmlFile(document, getCustomXmlMapperFileName(introspectedTable), this.context.getSqlMapGeneratorConfiguration().getTargetPackage(), this.context.getSqlMapGeneratorConfiguration().getTargetProject(), false, this.context.getXmlFormatter());
            if(!generatedFileExists(gxf)){
                answer.add(gxf);
            }
            return answer;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        return true;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return super.clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(method, topLevelClass, introspectedTable);
    }

    @Override
    public void setContext(Context context) {
        CommentGeneratorConfiguration commentCfg = new CommentGeneratorConfiguration();
        commentCfg.setConfigurationType(MapperCommentGenerator.class.getCanonicalName());
        context.setCommentGeneratorConfiguration(commentCfg);
        super.setContext(context);
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        renameJavaMapperName(introspectedTable);
        renameXmlMapperFileName(introspectedTable);
    }


    private void renameXmlMapperFileName(IntrospectedTable introspectedTable){
        introspectedTable.setMyBatis3XmlMapperPackage(introspectedTable.getMyBatis3XmlMapperPackage() + "." + prefix.toLowerCase());
        introspectedTable.setMyBatis3XmlMapperFileName(prefix + introspectedTable.getMyBatis3XmlMapperFileName());
    }


    private void renameJavaMapperName(IntrospectedTable introspectedTable){
        String oldType = introspectedTable.getMyBatis3JavaMapperType();
        FullyQualifiedJavaType fqj = new FullyQualifiedJavaType(oldType);
        introspectedTable.setMyBatis3JavaMapperType(fqj.getPackageName() + "." + prefix.toLowerCase() + "." + prefix + fqj.getShortName());
    }

    public static FullyQualifiedJavaType getBaseMapperType(IntrospectedTable introspectedTable){
        return new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
    }

    public static FullyQualifiedJavaType getCustomMapperType(IntrospectedTable introspectedTable){
        FullyQualifiedJavaType baseType = getBaseMapperType(introspectedTable);
        return new FullyQualifiedJavaType(introspectedTable.getContext().getJavaClientGeneratorConfiguration().getTargetPackage() + "." + baseType.getShortName().substring(prefix.length()));
    }

    public static String getCustomXmlMapperFileName(IntrospectedTable introspectedTable){
        return introspectedTable.getMyBatis3XmlMapperFileName().substring(prefix.length());
    }


    public boolean generatedFileExists(GeneratedFile gxf){
        try {
            File directory = callback.getDirectory(gxf.getTargetProject(), gxf.getTargetPackage());
            File targetFile = new File(directory, gxf.getFileName());
            return targetFile.exists();
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}