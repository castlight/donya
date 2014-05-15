package org.donyasource.donya;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;



/**
 * Compile-time validation of localized resource annotations.  
 * 
 * TODO -- plug in to compilation using META-INF/services/javax.annotation.processing.Processor,
 * see http://deors.wordpress.com/2011/10/08/annotation-processors/
 * 
 */
@SupportedAnnotationTypes({
   "com.castlight.i18n.annotations.LocalizedResource"})
public class LocalizedResourceCompileTimeProcessor extends AbstractProcessor {

   @Override
   public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
      for (TypeElement element : annotations) {
         System.out.println("Got a LocalizedResource " + element);
      }
      
      return true;
   }

}
