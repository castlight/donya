package org.donyasource.donya.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * Identifies an array or collection field that contains all the arguments for a
 * localized resource
 */
public @interface ResourceArgumentList {
   /**
    * The name of the localized resource.  This is the name of a field that has
    * an @LocalizedResource annotation.
    * 
    * This does not need to be specified if there is only one localized resource in this class.
    */
   public String value() default "";
}
