
package com.s3s3l.niflheim;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * </p>
 * ClassName:Doc <br>
 * Date: Jun 25, 2018 8:20:11 PM <br>
 * 
 * @author kehw_zwei
 * @version 1.0.0
 * @since JDK 1.8
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NiflheimDoc {

    String value() default "Default";
    /**
     * 
     * Alias for value
     * 
     * @return 
     * @since JDK 1.8
     */
    String name() default "Default";
}
