/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.repository.support;

import com.microsoft.spring.data.gremlin.query.GremlinTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.auditing.IsNewAwareAuditingHandler;
import org.springframework.util.Assert;

/**
 * Sets {@link IsNewAwareAuditingHandler} to {@link GremlinTemplate}
 *
 * @author xxcxy
 */
public class GremlinAuditingBeanPostProcessor implements BeanPostProcessor {

    private final ObjectFactory<IsNewAwareAuditingHandler> isNewAwareHandler;

    public GremlinAuditingBeanPostProcessor(ObjectFactory<IsNewAwareAuditingHandler> isNewAwareHandler) {

        Assert.notNull(isNewAwareHandler, "IsNewAwareHandler must not be null!");

        this.isNewAwareHandler = isNewAwareHandler;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object,
     * java.lang.String)
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (GremlinTemplate.class.isInstance(bean)) {
            ((GremlinTemplate) bean).setAuditingHandlerFactory(isNewAwareHandler);
        }
        return bean;
    }
}
