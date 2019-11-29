/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.annotation;

import org.springframework.data.annotation.Persistent;
import org.springframework.data.annotation.Reference;

import java.lang.annotation.*;

/**
 * Specifies the field as target of one edge.
 */
@Persistent
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Reference
public @interface EdgeTo {
}
