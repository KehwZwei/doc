
package com.s3s3l.niflheim;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.s3s3l.doc.HttpMethod;

/**
 * <p>
 * </p>
 * ClassName:RPC <br>
 * Date: Jun 26, 2018 12:42:21 PM <br>
 * 
 * @author kehw_zwei
 * @version 1.0.0
 * @since JDK 1.8
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Http {

    String value();

    /**
     * 
     * Alias for value
     * 
     * @return
     * @since JDK 1.8
     */
    String path();

    HttpMethod method() default HttpMethod.POST;
}
