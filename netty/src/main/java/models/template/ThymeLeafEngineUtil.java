package models.template;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

/**
 * Created by Administrator on 2017/6/12.
 */
public class ThymeLeafEngineUtil {

    private static TemplateEngine classLoaderTemplateEngine;

    private static TemplateEngine fileTemplateEngine;

    //获取模板引擎
    public static TemplateEngine getClassLoaderTemplateEngine() {
        if (classLoaderTemplateEngine == null) {

            ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
            templateResolver.setOrder(2);
            templateResolver.setTemplateMode(TemplateMode.HTML);

            classLoaderTemplateEngine = new TemplateEngine();
            classLoaderTemplateEngine.setTemplateResolver(templateResolver);

        }
        return classLoaderTemplateEngine;
    }

    //按文件系统查找的模板引擎
    public  static TemplateEngine getFileTemplateEngine() {

        if (fileTemplateEngine == null) {
            //文件模板引擎
            FileTemplateResolver templateResolver = new FileTemplateResolver();
            templateResolver.setTemplateMode(TemplateMode.HTML);

            String resourceFilePath = ThymeLeafEngineUtil.class.getClass().getResource("/").getPath();
            System.out.println("aaaaa\t" + resourceFilePath);
            templateResolver.setPrefix(resourceFilePath);
            templateResolver.setSuffix(".html");

            fileTemplateEngine = new TemplateEngine();
            fileTemplateEngine.setTemplateResolver(templateResolver);
        }

        return fileTemplateEngine;
    }


}
