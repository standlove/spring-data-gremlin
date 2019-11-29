/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query.query;

import com.microsoft.spring.data.gremlin.annotation.Query;
import com.microsoft.spring.data.gremlin.common.GremlinUtils;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.query.GremlinOperations;
import com.microsoft.spring.data.gremlin.query.paramerter.GremlinParameterAccessor;
import com.microsoft.spring.data.gremlin.query.paramerter.GremlinParametersParameterAccessor;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.ResultProcessor;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * A concrete {@link AbstractGremlinQuery} which handles String based gremlin queries
 * defined using the {@link Query} annotation.
 *
 * @author xxcxy
 */
public class StringGremlinQuery extends AbstractGremlinQuery {

    private String queryString;
    private final ResultProcessor processor;
    private final GremlinQueryMethod method;
    private final GremlinOperations operations;

    /**
     * Creates a new {@link StringGremlinQuery}.
     *
     * @param method     a {@link GremlinQueryMethod}
     * @param operations the {@link GremlinOperations}
     * @param query      a {@linke Query} annotation
     */
    public StringGremlinQuery(@NonNull GremlinQueryMethod method, @NonNull GremlinOperations operations,
                              @NonNull Query query) {
        super(method, operations);
        this.processor = method.getResultProcessor();
        this.operations = operations;
        this.method = method;
        this.queryString = query.value();
    }

    /**
     * Override the super's execute, so create query can be null.
     *
     * @param accessor
     * @return
     */
    @Override
    protected GremlinQuery createQuery(final GremlinParameterAccessor accessor) {
        return null;
    }

    /**
     * Override the super's execute method.
     *
     * @param parameters
     * @return
     */
    @Override
    public Object execute(final Object[] parameters) {
        final GremlinParametersParameterAccessor accessor = new GremlinParametersParameterAccessor(method, parameters);
        final GremlinSource<?> source = GremlinUtils.toGremlinSource(processor.withDynamicProjection(accessor)
                .getReturnedType().getDomainType());
        final Object result = operations.findByStringQuery(getStringQuery(accessor, parameters), source);
        return processResult(result, method, parameters);
    }

    /**
     * Parses the query string gremlin.
     *
     * @param accessor
     * @param values
     * @return
     */
    private List<String> getStringQuery(GremlinParametersParameterAccessor accessor, Object[] values) {
        String queryString = this.queryString;
        for (final Parameter param : accessor.getParameters().getBindableParameters()) {
            final Optional<String> paramName = param.getName();
            final Object val = values[param.getIndex()];
            if (paramName.isPresent()) {
                queryString = queryString.replaceAll("\\$" + paramName.get(), Objects.toString(val));
            } else {
                queryString = queryString.replaceFirst("\\?", Objects.toString(val));
            }
        }
        return Collections.singletonList(queryString);
    }

    @Override
    protected boolean isCountQuery() {
        return false;
    }
}
