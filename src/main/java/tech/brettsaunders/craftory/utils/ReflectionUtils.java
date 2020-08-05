/*******************************************************************************
 * Copyright (c) 2020. BrettSaunders & Craftory Team - All Rights Reserved
 *
 * This file is part of Craftory.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 *
 * File Author: Brett Saunders & Matty Jones
 ******************************************************************************/

package tech.brettsaunders.craftory.utils;

import com.google.common.collect.Lists;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReflectionUtils {

  public static <T extends Annotation> T getClassAnnotation(@NonNull Object object,
      @NonNull Class<T> annotation) {
    Class<?> clazz = object.getClass();
    T result = clazz.getAnnotation(annotation);
    if (result == null) {
      throw new IllegalStateException("The class '" + clazz.getName() + "' does not have the @"
          + annotation.getSimpleName() + " Annotation!");
    }
    return result;
  }

  public static Collection<Field> getFieldsRecursively(@NonNull Class<?> startClass,
      @NonNull Class<?> exclusiveParent) {
    Collection<Field> fields = Lists.newArrayList(startClass.getDeclaredFields());
    Class<?> parentClass = startClass.getSuperclass();

    if (parentClass != null && !(parentClass.equals(exclusiveParent))) {
      fields.addAll(getFieldsRecursively(parentClass, exclusiveParent));
    }

    return fields;
  }

  public static Collection<Method> getMethodsRecursively(@NonNull Class<?> startClass,
      @NonNull Class<?> exclusiveParent) {
    Collection<Method> methods = Lists.newArrayList(startClass.getDeclaredMethods());
    Class<?> parentClass = startClass.getSuperclass();

    if (parentClass != null && !(parentClass.equals(exclusiveParent))) {
      methods.addAll(getMethodsRecursively(parentClass, exclusiveParent));
    }

    return methods;
  }
}
