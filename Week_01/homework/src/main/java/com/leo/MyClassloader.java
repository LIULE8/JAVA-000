package com.leo;

import cn.hutool.core.io.IoUtil;
import com.google.common.base.Preconditions;
import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.InputStream;

public class MyClassloader extends ClassLoader {

  public static final String CLASS_NAME = "Hello";
  public static final String METHOD_NAME = "hello";
  public static final String RESOURCE_NAME = "Hello.xlass";

  @SneakyThrows(Exception.class)
  public static void main(String[] args) {
    Class<?> hello = new MyClassloader().findClass(CLASS_NAME);
    hello.getMethod(METHOD_NAME).invoke(hello.newInstance());
  }

  @Override
  @SneakyThrows(Exception.class)
  protected Class<?> findClass(String name) {
    @Cleanup
    InputStream in = MyClassloader.class.getClassLoader().getResourceAsStream(RESOURCE_NAME);
    Preconditions.checkNotNull(in);
    byte[] bytes = IoUtil.read(in).toByteArray();
    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = (byte) (255 - bytes[i]);
    }
    return defineClass(name, bytes, 0, bytes.length);
  }
}
