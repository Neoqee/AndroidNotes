package com.neoqee.lib_compiler;

import com.neoqee.lib_annotations.Anno;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes("com.neoqee.lib_annotations.Anno")
public class AnnoProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        for (Element element : roundEnv.getElementsAnnotatedWith(Anno.class)) {
//            if (element.getKind() != ElementKind.CLASS){
//
//                return false;
//            }
//
//            String packageName = processingEnv.getElementUtils().getPackageOf(element).getQualifiedName().toString();
//            String elementName = element.getSimpleName().toString();
////            Class.forName()
//
//        }
        return true;
    }
}
