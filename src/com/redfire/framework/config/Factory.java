package com.redfire.framework.config;

/**
 * 通用工厂接口
 * @author 获取特定工厂
 * @param <T>
 */
public interface Factory<T> {
  public T getBean(String id);
}
