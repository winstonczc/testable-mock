package com.alibaba.testable.core.annotation;

import javax.lang.model.type.NullType;
import java.lang.annotation.*;

/**
 * Mark specified class as mock container, and allow it to inherit mock methods from other classes
 *
 * @author flin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface MockContainer {

    /**
     * @return
     */
    String[] fors() default {};

    /**
     * specify the classes to inherit methods from
     *
     * @return list of class
     */
    Class<?>[] inherits() default NullType.class;

}
