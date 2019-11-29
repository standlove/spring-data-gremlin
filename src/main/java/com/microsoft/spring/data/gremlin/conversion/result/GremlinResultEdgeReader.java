/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.result;

import com.microsoft.spring.data.gremlin.common.GremlinUtils;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceEdge;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.structure.util.detached.DetachedEdge;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

import static com.microsoft.spring.data.gremlin.common.Constants.GREMLIN_PROPERTY_CLASSNAME;
import static com.microsoft.spring.data.gremlin.common.Constants.PROPERTY_ID;
import static com.microsoft.spring.data.gremlin.common.Constants.PROPERTY_INV;
import static com.microsoft.spring.data.gremlin.common.Constants.PROPERTY_LABEL;
import static com.microsoft.spring.data.gremlin.common.Constants.PROPERTY_OUTV;
import static com.microsoft.spring.data.gremlin.common.Constants.PROPERTY_PROPERTIES;
import static com.microsoft.spring.data.gremlin.common.Constants.PROPERTY_TYPE;
import static com.microsoft.spring.data.gremlin.common.Constants.RESULT_TYPE_EDGE;

@NoArgsConstructor
public class GremlinResultEdgeReader extends AbstractGremlinResultReader implements GremlinResultsReader {

    private void readProperties(@NonNull GremlinSource source, @Nullable Map map) {
        if (map != null) {
            @SuppressWarnings("unchecked") final Map<String, Object> properties = (Map<String, Object>) map;

            properties.forEach(source::setProperty);
        }
    }

    private void validate(List<Result> results, GremlinSource source) {
        Assert.notNull(results, "Results should not be null.");
        Assert.notNull(source, "GremlinSource should not be null.");
        Assert.isTrue(results.size() == 1, "Edge should contain only one result.");

        final Result result = results.get(0);

        Assert.isInstanceOf(Map.class, result.getObject(), "should be one instance of Map");

        @SuppressWarnings("unchecked") final Map<String, Object> map = (Map<String, Object>) result.getObject();

        Assert.isTrue(map.containsKey(PROPERTY_ID), "should contain id property");
        Assert.isTrue(map.containsKey(PROPERTY_LABEL), "should contain label property");
        Assert.isTrue(map.containsKey(PROPERTY_TYPE), "should contain type property");
        Assert.isTrue(map.containsKey(PROPERTY_INV), "should contain inV property");
        Assert.isTrue(map.containsKey(PROPERTY_OUTV), "should contain outV property");
        Assert.isTrue(map.get(PROPERTY_TYPE).equals(RESULT_TYPE_EDGE), "must be vertex type");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void read(@NonNull List<Result> results, @NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("Should be instance of GremlinSourceEdge");
        }

        final GremlinSourceEdge sourceEdge = (GremlinSourceEdge) source;
        final String className = source.getProperties().get(GREMLIN_PROPERTY_CLASSNAME).toString();

        final Object resultObject = results.get(0).getObject();
        if (resultObject instanceof DetachedEdge) {
            final DetachedEdge result = (DetachedEdge) resultObject;
            result.keys().forEach(key -> source.setProperty(key, result.value(key)));
            sourceEdge.setIdField(GremlinUtils.getIdField(GremlinUtils.toEntityClass(className)));
            sourceEdge.setId(result.id());
            sourceEdge.setLabel(result.label());
            sourceEdge.setVertexIdFrom(result.outVertex().id().toString());
            sourceEdge.setVertexIdTo(result.inVertex().id().toString());
        } else {
            validate(results, source);
            final Map<String, Object> map = (Map<String, Object>) resultObject;
            this.readProperties(source, (Map) map.get(PROPERTY_PROPERTIES));
            sourceEdge.setIdField(GremlinUtils.getIdField(GremlinUtils.toEntityClass(className)));
            sourceEdge.setId(map.get(PROPERTY_ID));
            sourceEdge.setLabel(map.get(PROPERTY_LABEL).toString());
            sourceEdge.setVertexIdFrom(map.get(PROPERTY_OUTV));
            sourceEdge.setVertexIdTo(map.get(PROPERTY_INV));
        }
    }
}
