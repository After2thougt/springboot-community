package com.bee.community.config;


import com.bee.community.common.Constants;
import com.bee.community.interceptor.MyBBSLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 实现WebMvcConfigurer接口进行拦截器配置
 */
@Configuration
public class MyBBSWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private MyBBSLoginInterceptor myBBSLoginInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        // 登陆拦截
        registry.addInterceptor(myBBSLoginInterceptor)
                .excludePathPatterns("/register")
                .excludePathPatterns("/login")
                .addPathPatterns("/logout")
                .addPathPatterns("/addPostPage")
                .addPathPatterns("/addPost")
                .addPathPatterns("/addCollect/**")
                .addPathPatterns("/editPostPage/**")
                .addPathPatterns("/editPost")
                .addPathPatterns("/detail/**")
                .addPathPatterns("/uploadFile")
                .addPathPatterns("/uploadFiles")
                .addPathPatterns("/updateUserInfo")
                .addPathPatterns("/updateHeadImg")
                .addPathPatterns("/updatePassword")
                .addPathPatterns("/userCenter")
                .addPathPatterns("/userCenter/**")
                .addPathPatterns("/myCenter")
                .addPathPatterns("/userSet");
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 所有以 /upload/ 开头的静态资源请求都会映射到设置的目录下
        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + Constants.FILE_UPLOAD_DIC);
    }
}
