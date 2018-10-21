package com.putable.videx.core.oio;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OIO {
    public static int FILENAME = 0; 
    public static int BASE_DIRECTORY = 1; 
    public boolean owned() default true;
    /**
     * Obsolete fields are accepted on load but ignored during save
     * @return true iff this field is obsolete
     */
    public boolean obsolete() default false; 
    public boolean inline() default true;
    public String extension() default "";
    public int value() default -1;
}
