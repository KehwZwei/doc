
package com.s3s3l.niflheim;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * </p>
 * ClassName:Param <br>
 * Date: Jun 26, 2018 12:37:06 PM <br>
 * 
 * @author kehw_zwei
 * @version 1.0.0
 * @since JDK 1.8
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
public @interface Param {

    String value() default "";

    /**
     * 
     * Alias for value
     * 
     * @return
     * @since JDK 1.8
     */
    String name() default "";

    String desc() default "";

    String remark() default "";

    String[] scope() default {};
}
