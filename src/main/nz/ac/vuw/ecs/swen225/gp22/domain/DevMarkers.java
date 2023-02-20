package nz.ac.vuw.ecs.swen225.gp22.domain;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is used to aid my development of this module. I use custom annotations to mark
 * certain parts of code that are still being worked on, or need to be changed to a notable
 * degree. These annotations will be printed out when running the main method in this file,
 * to make tracking these markers easier.
 * <p>
 * This file also acts as a testing ground for features that have not yet been
 * fully implemented and thus do not benefit from a unit test.
 *
 * @author Abdul
 * @version 1.2
 */
public class DevMarkers {
    /**
     * Determines whether or not custom annotations will be printed at runtime.
     */
    public static boolean markersEnabled = true;

    /**
     * List of custom annotations to check for.
     */
    private static final List<Class<? extends Annotation>> annotations = Arrays.asList(
            DevMarkers.NeedsPrecons.class,
            DevMarkers.NeedsPostcons.class,
            DevMarkers.WIPList.class
    );

    /**
     * Descriptions of non-WIP annotations. Follows the order of annotations.
     */
    private static final List<String> annotationNotes = Arrays.asList(
            "This element needs to enforce preconditions.",
            "This element needs to enforce postconditions."
    );

    /**
     * Used when an element needs to enforce preconditions.
     */
    @Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface NeedsPrecons {
    }

    /**
     * Used when an element needs to enforce postconditions.
     */
    @Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface NeedsPostcons {
    }

    /**
     * A custom repeatable annotation where extra info can be provided
     * about the work that needs to be done.
     */
    @Repeatable(DevMarkers.WIPList.class)
    @Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface WIP {
        /**
         * @return The description for the WIP element.
         */
        String value() default "This element is in development.";
    }

    /**
     * Allows WIP to be repeatable.
     */
    @Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface WIPList {
        /**
         * @return List of WIP elements.
         */
        DevMarkers.WIP[] value();
    }

    /**
     * Used to test interactions between objects whilst creating
     * the implementation for certain classes. Also prints out any
     * custom annotations if markersEnabled is true.
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        System.out.println("----------------------------------------------------------------------------------------------");
        System.out.println("Domain Development Branch Testing | Not for marking");
        System.out.println("----------------------------------------------------------------------------------------------");

        // Prints custom annotations.
        if (markersEnabled) findPackageClasses("nz.ac.vuw.ecs.swen225.gp22.domain").forEach(clazz -> {
            printAnnotationMessage(clazz, "Class - " + clazz.getSimpleName() + ": ");

            for (Constructor<?> constructor : clazz.getConstructors()) {
                String params = "";
                for (Class<?> param : constructor.getParameterTypes()) params += param.getSimpleName() + ", ";
                if (params.length() > 1) params = params.substring(0, params.length() - 2);
                printAnnotationMessage(constructor, "Constructor - " + clazz.getSimpleName() + "(" + params + "): ");
            }

            for (Method method : clazz.getDeclaredMethods()) {
                String params = "";
                for (Class<?> param : method.getParameterTypes()) params += param.getSimpleName() + ", ";
                if (params.length() > 1) params = params.substring(0, params.length() - 2);
                printAnnotationMessage(method, "Method - " + clazz.getSimpleName() + "." + method.getName() + "(" + params + "): ");
            }

            for (Field field : clazz.getDeclaredFields()) {
                printAnnotationMessage(field, "Field - " + clazz.getSimpleName() + "." + field.getName() + ": ");
            }

        });

        System.out.println("");
    }

    /**
     * Checks if an element has any custom annotations and if so,
     * prints them out with the correct prefix.
     *
     * @param o      The element being queried.
     * @param prefix Element prefix to indicate what the element is.
     */
    private static void printAnnotationMessage(AnnotatedElement o, String prefix) {
        for (Class<? extends Annotation> annotationClass : annotations) {
            Annotation annotation;
            if ((annotation = o.getAnnotation(annotationClass)) != null) {
                if (annotationClass == WIPList.class) {
                    for (WIP item : ((WIPList) annotation).value()) System.out.println(prefix + item.value());
                } else System.out.println(prefix + annotationNotes.get(annotations.indexOf(annotationClass)));
            }
        }
    }

    /**
     * Finds all classes in a given package. This uses
     * the system class loader and turns it into a stream which
     * can then find all the classes.
     * <p>
     * Used: https://www.baeldung.com/java-find-all-classes-in-package
     *
     * @param packageName Name of package to find classes in.
     * @return Set of classes in package.
     */
    public static Set<Class<?>> findPackageClasses(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines().filter(line -> line.endsWith(".class")).map(line -> getClass(line, packageName)).collect(Collectors.toSet());
    }

    /**
     * Finds a class using it's name and package.
     * <p>
     * Used: https://www.baeldung.com/java-find-all-classes-in-package
     *
     * @param className   Name of class.
     * @param packageName Package class is in.
     * @return Class object.
     */
    private static Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
        }
        return null;
    }
}
