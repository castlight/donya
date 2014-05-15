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
 * Identifies the resource family for a localized resource.  A resource family identifies a group
 * of resource bundles, where there is one bundle per locale.  For example, "AccountErrors"
 * might be the resource family for all error messages for the account service
 */
public @interface ResourceFamily {
   /**
    * The name of the localized resource.  This is the name of a field that has
    * an @LocalizedResource annotation.
    * 
    * This does not need to be specified if there is only one localized resource in this class.
    */
   public String value() default "";
}
