package com.hhrpc.hhrpc.core.meta;

import com.google.common.base.Strings;

/**
 * @Date 2024/3/24
 * @Author lifei
 */
public class ServiceMeta {
    private String app;
    private String namespace;
    private String name;
    private String env;

    public ServiceMeta(){}
    private ServiceMeta(Builder builder){
        this.app = builder.app;
        this.namespace = builder.namespace;
        this.name = builder.name;
        this.env = builder.env;
    }


    public static Builder builder() {
        return new Builder();
    }

    public String toPath() {
        return Strings.lenientFormat("%s_%s_%s_%s", app, namespace, env, name);
    }

    public static class Builder {
        private String app;
        private String namespace;
        private String name;
        private String env;

        public Builder app(String app) {this.app = app; return this;}
        public Builder namespace(String namespace) {this.namespace = namespace; return this;}
        public Builder name(String name) {this.name = name; return this;}
        public Builder env(String env) {this.env = env; return this;}

        public ServiceMeta build() {
            return new ServiceMeta(this);
        }
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    @Override
    public String toString() {
        return "ServiceMeta{" +
                "app='" + app + '\'' +
                ", namespace='" + namespace + '\'' +
                ", name='" + name + '\'' +
                ", env='" + env + '\'' +
                '}';
    }
}
