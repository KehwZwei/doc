  
package com.s3s3l.niflheim;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.s3s3l.doc.RequestType;

/**
 * <p>
 * </p> 
 * ClassName:Interface <br> 
 * Date:     Jun 25, 2018 8:22:35 PM <br>
 *  
 * @author   kehw_zwei 
 * @version  1.0.0
 * @since    JDK 1.8
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Interface {

    String value();
    
    /**
     * 
     * Alias for value
     * 
     * @return 
     * @since JDK 1.8
     */
    String name();
    
    RequestType requestType() default RequestType.HTTP_OR_HTTPS;
    
    String desc() default "";
}
  