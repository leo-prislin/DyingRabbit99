package de.wattestaebchen.dyingrabbit99.commands2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandAnnotation {
	
	String label();	
	String[] alias() default {};
	String[] subCommands() default {};
	boolean subCommandsRequired() default false;
	
}
