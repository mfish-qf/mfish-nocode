/**
 * Copyright 2010 Newcastle University
 * <p>
 * http://research.ncl.ac.uk/smart/
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.com.mfish.oauth.oltu.exception;


import cn.com.mfish.oauth.oltu.common.utils.OAuthUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class OAuthProblemException extends Exception {

    @Getter
    private final String error;
    @Getter
    private String description;
    @Getter
    private String uri;
    @Getter
    private String state;
    @Getter
    private String scope;
    @Setter
    @Getter
    private String redirectUri;

    private int responseStatus;

    @Getter
    private final Map<String, String> parameters = new HashMap<>();

    protected OAuthProblemException(String error) {
        this(error, "");
    }

    protected OAuthProblemException(String error, String description) {
        super(error + " " + description);
        this.description = description;
        this.error = error;
    }


    public static OAuthProblemException error(String error) {
        return new OAuthProblemException(error);
    }

    public static OAuthProblemException error(String error, String description) {
        return new OAuthProblemException(error, description);
    }

    public OAuthProblemException description(String description) {
        this.description = description;
        return this;
    }

    public OAuthProblemException uri(String uri) {
        this.uri = uri;
        return this;
    }

    public OAuthProblemException state(String state) {
        this.state = state;
        return this;
    }

    public OAuthProblemException scope(String scope) {
        this.scope = scope;
        return this;
    }

    public OAuthProblemException responseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
        return this;
    }

    public OAuthProblemException setParameter(String name, String value) {
        parameters.put(name, value);
        return this;
    }

    public int getResponseStatus() {
        return responseStatus == 0 ? 400 : responseStatus;
    }

    public String get(String name) {
        return parameters.get(name);
    }

    @Override
    public String getMessage() {
        StringBuilder b = new StringBuilder();
        if (!OAuthUtils.isEmpty(error)) {
            b.append(error);
        }

        if (!OAuthUtils.isEmpty(description)) {
            b.append(", ").append(description);
        }


        if (!OAuthUtils.isEmpty(uri)) {
            b.append(", ").append(uri);
        }


        if (!OAuthUtils.isEmpty(state)) {
            b.append(", ").append(state);
        }

        if (!OAuthUtils.isEmpty(scope)) {
            b.append(", ").append(scope);
        }

        return b.toString();
    }

    @Override
    public String toString() {
        return "OAuthProblemException{" +
                "error='" + error + '\'' +
                ", description='" + description + '\'' +
                ", uri='" + uri + '\'' +
                ", state='" + state + '\'' +
                ", scope='" + scope + '\'' +
                ", redirectUri='" + redirectUri + '\'' +
                ", responseStatus=" + responseStatus +
                ", parameters=" + parameters +
                '}';
    }
}
