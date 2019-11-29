/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query.query;

import com.microsoft.spring.data.gremlin.query.GremlinOperations;
import com.microsoft.spring.data.gremlin.query.paramerter.GremlinParameterAccessor;
import com.microsoft.spring.data.gremlin.query.paramerter.GremlinParametersParameterAccessor;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.ResultProcessor;
import org.springframework.lang.NonNull;

import java.util.List;

public abstract class AbstractGremlinQuery implements RepositoryQuery {

    private final GremlinQueryMethod method;
    private final GremlinOperations operations;

    public AbstractGremlinQuery(@NonNull GremlinQueryMethod method, @NonNull GremlinOperations operations) {
        this.method = method;
        this.operations = operations;
    }

    protected abstract GremlinQuery createQuery(GremlinParameterAccessor accessor);

    protected boolean isDeleteQuery() {
        // panli: always return false as only take care find in one PR.
        return false;
    }

    @Override
    public Object execute(@NonNull Object[] parameters) {
        final GremlinParameterAccessor accessor = new GremlinParametersParameterAccessor(this.method, parameters);

        final GremlinQuery query = this.createQuery(accessor);
        final ResultProcessor processor = method.getResultProcessor().withDynamicProjection(accessor);
        final GremlinQueryExecution execution = this.getExecution();
        final Object result = execution.execute(query, processor.getReturnedType().getDomainType());
        return processResult(result, method, parameters);
    }

    protected Object processResult(@NonNull Object result, @NonNull QueryMethod queryMethod,
                                   @NonNull Object[] parameters) {
        final ParameterAccessor accessor = new ParametersParameterAccessor(queryMethod.getParameters(), parameters);
        final ResultProcessor processor = queryMethod.getResultProcessor().withDynamicProjection(accessor);
        if (queryMethod.isCollectionQuery() || queryMethod.isStreamQuery()) {
            return processor.processResult(result);
        } else if (isCountQuery()) {
            if (result == null) {
                return 0;
            }
            return ((List) result).size();
        } else if (result == null || ((List) result).isEmpty()) {
            return null;
        } else {
            return processor.processResult(((List) result).get(0));
        }
    }

    protected abstract boolean isCountQuery();

    @Override
    @NonNull
    public GremlinQueryMethod getQueryMethod() {
        return this.method;
    }

    @NonNull
    private GremlinQueryExecution getExecution() {
        if (this.isDeleteQuery()) {
            throw new UnsupportedOperationException("Not implemented yet");
        } else {
            return new GremlinQueryExecution.FindExecution(this.operations);
        }
    }
}
