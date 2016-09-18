package com.platform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 
原理：在新建页面中Session保存token随机码，当保存时验证，通过后删除，
当再次点击保存时由于服务器端的Session中已经不存在了，所有无法验证通过。

 * 防止重复提交注解，用于方法上<br/>
 * 在新建页面方法上，设置needSaveToken()为true，此时拦截器会在Session中保存一个token，
 * 同时需要在新建的页面中添加
 * <input type="hidden" name="token" value="${token}">
 * <br/>
 * 保存Controller方法需要验证重复提交的，@AvoidDuplicateSubmission(needRemoveToken = true
 * 设置needRemoveToken为true
 * 此时会在拦截器中验证是否重复提交
 * </p>
 * @date: 2013-6-27上午11:14:02
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AvoidDuplicateSubmission {
    boolean needSaveToken() default false;
    boolean needRemoveToken() default false;
}