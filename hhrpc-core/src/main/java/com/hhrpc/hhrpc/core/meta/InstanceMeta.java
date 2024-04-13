package com.hhrpc.hhrpc.core.meta;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import java.util.Map;

/**
 * @Date 2024/3/24
 * @Author lifei
 */
public class InstanceMeta {
    private String schema;
    private String host;
    private Integer port;
    private String context;
    private Boolean status;
    private Map<String, String> parameters = Maps.newHashMap();

    public InstanceMeta(){}

    public static Builder builder() {
        return new Builder();
    }
    private InstanceMeta(Builder builder){
        this.schema = builder.schema;
        this.host = builder.host;
        this.port = builder.port;
        this.context = builder.context;
        this.status = builder.status;
        this.parameters = builder.parameters;
    }

    public String toPath() {
        return Strings.lenientFormat("%s_%s", host, port);
    }

    public String toUrl() {
        return Strings.lenientFormat("%s://%s:%s/", schema, host, port);
    }

    public static class Builder{
        private String schema;
        private String host;
        private Integer port;
        private String context;
        private Boolean status;
        private Map<String, String> parameters = Maps.newHashMap();

        public Builder schema(String schema) {this.schema=schema; return this;}
        public Builder host(String host) {this.host=host; return this;}
        public Builder port(Integer port) {this.port=port; return this;}
        public Builder context(String context) {this.context=context; return this;}
        public Builder status(Boolean status) {this.status=status; return this;}
        public Builder parameters(Map<String, String> parameters) {this.parameters=parameters; return this;}

        public InstanceMeta build() {
            return new InstanceMeta(this);
        }
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String toMetas() {
        return new Gson().toJson(parameters);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(InstanceMeta.class)
                .add("schema", schema)
                .add("host", host)
                .add("port", port)
                .add("context", context)
                .add("status", status)
                .add("parameters", parameters)
                .toString();
    }
}
