/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.repository.config;

import org.springframework.context.annotation.Import;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(GremlinAuditingRegistrar.class)
public @interface EnableGremlinAuditing {
    /**
     * Configures the {@link AuditorAware} bean to be used to lookup the current principal.
     *
     * @return
     */
    String auditorAwareRef() default "";

    /**
     * Configures whether the creation and modification dates are set. Defaults to {@literal true}.
     *
     * @return
     */
    boolean setDates() default true;

    /**
     * Configures whether the entity shall be marked as modified on creation. Defaults to {@literal true}.
     *
     * @return
     */
    boolean modifyOnCreate() default true;

    /**
     * Configures a {@link DateTimeProvider} bean name that allows customizing the dateTime to be
     * used for setting creation and modification dates.
     *
     * @return
     */
    String dateTimeProviderRef() default "";
}

