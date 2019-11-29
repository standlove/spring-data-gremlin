/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.repository.config;

import com.microsoft.spring.data.gremlin.repository.support.GremlinAuditingBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.data.auditing.IsNewAwareAuditingHandler;
import org.springframework.data.auditing.config.AuditingBeanDefinitionRegistrarSupport;
import org.springframework.data.auditing.config.AuditingConfiguration;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

import static org.springframework.data.config.ParsingUtils.getObjectFactoryBeanDefinition;

/**
 * @author xxcxy
 */
public class GremlinAuditingRegistrar extends AuditingBeanDefinitionRegistrarSupport {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableGremlinAuditing.class;
    }

    @Override
    protected String getAuditingHandlerBeanName() {
        return "gremlinAuditingHandler";
    }

    @Override
    protected BeanDefinitionBuilder getAuditHandlerBeanDefinitionBuilder(AuditingConfiguration configuration) {

        Assert.notNull(configuration, "AuditingConfiguration must not be null!");

        final BeanDefinitionBuilder handler = BeanDefinitionBuilder.rootBeanDefinition(IsNewAwareAuditingHandler.class);

        handler.addConstructorArgReference("gremlinMappingContext");
        return configureDefaultAuditHandlerAttributes(configuration, handler);
    }

    @Override
    protected void registerAuditListenerBeanDefinition(BeanDefinition auditingHandlerDefinition,
                                                       BeanDefinitionRegistry registry) {

        Assert.notNull(auditingHandlerDefinition, "BeanDefinition must not be null!");
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");

        final BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .rootBeanDefinition(GremlinAuditingBeanPostProcessor.class)
                .addConstructorArgValue(getObjectFactoryBeanDefinition(getAuditingHandlerBeanName(), registry));

        registerInfrastructureBeanWithId(beanDefinitionBuilder.getBeanDefinition(),
                GremlinAuditingBeanPostProcessor.class.getName(), registry);
    }

}
