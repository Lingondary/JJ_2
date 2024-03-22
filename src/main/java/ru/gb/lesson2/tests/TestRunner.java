package ru.gb.lesson2.tests;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {

  public static void run(Class<?> testClass) {
    Object testObj = initTestObj(testClass);
    List<Method> beforeAllMethods = getMethodsAnnotatedWith(testClass, BeforeAll.class);
    List<Method> beforeEachMethods = getMethodsAnnotatedWith(testClass, BeforeEach.class);
    List<Method> afterEachMethods = getMethodsAnnotatedWith(testClass, AfterEach.class);
    List<Method> afterAllMethods = getMethodsAnnotatedWith(testClass, AfterAll.class);

    invokeMethods(beforeAllMethods, null); // BeforeAll methods

    for (Method testMethod : testClass.getDeclaredMethods()) {
      if (testMethod.getAnnotation(Test.class) != null) {
        invokeMethods(beforeEachMethods, testObj); // BeforeEach methods
        try {
          testMethod.invoke(testObj);
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new RuntimeException(e);
        }
        invokeMethods(afterEachMethods, testObj); // AfterEach methods
      }
    }

    invokeMethods(afterAllMethods, null); // AfterAll methods
  }

  private static Object initTestObj(Class<?> testClass) {
    try {
      Constructor<?> noArgsConstructor = testClass.getConstructor();
      return noArgsConstructor.newInstance();
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Нет конструктора по умолчанию");
    } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
      throw new RuntimeException("Не удалось создать объект тест класса");
    }
  }

  private static List<Method> getMethodsAnnotatedWith(Class<?> clazz, Class<? extends java.lang.annotation.Annotation> annotation) {
    List<Method> methods = new ArrayList<>();
    for (Method method : clazz.getDeclaredMethods()) {
      if (method.isAnnotationPresent(annotation)) {
        methods.add(method);
      }
    }
    return methods;
  }

  private static void invokeMethods(List<Method> methods, Object testObj) {
    for (Method method : methods) {
      try {
        method.invoke(testObj);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    }
  }

}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface BeforeAll {
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface BeforeEach {
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface AfterEach {
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface AfterAll {
}

