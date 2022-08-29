package com.sendroids.usersync.client.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({BatchSyncUserConfigSelector.class})
public @interface EnableBatchSyncUser {
}
