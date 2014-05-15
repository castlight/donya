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
 * Identifies a field that will contain a list of localized resources strings.
 * 
 * This field will be replaced with a list of formatted localized text when the JSON for this object
 * is generated. 
 */
public @interface LocalizedResources {

}
