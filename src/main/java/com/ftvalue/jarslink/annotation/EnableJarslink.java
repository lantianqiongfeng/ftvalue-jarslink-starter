package com.ftvalue.jarslink.annotation;

import com.ftvalue.jarslink.autoconfigure.JarslinkParseAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({JarslinkParseAutoConfiguration.class})
public @interface EnableJarslink {

}
