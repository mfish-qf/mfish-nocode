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

package cn.com.mfish.oauth.oltu.as.response;


import cn.com.mfish.oauth.oltu.common.OAuth;
import cn.com.mfish.oauth.oltu.common.message.OAuthResponse;
import jakarta.servlet.http.HttpServletRequest;

public class OAuthASResponse extends OAuthResponse {

    protected OAuthASResponse(String uri, int responseStatus) {
        super(uri, responseStatus);
    }

    public static OAuthAuthorizationResponseBuilder authorizationResponse(HttpServletRequest request, int code) {
        return new OAuthAuthorizationResponseBuilder(request, code);
    }

    public static class OAuthAuthorizationResponseBuilder extends OAuthResponseBuilder {

        public OAuthAuthorizationResponseBuilder(HttpServletRequest request, int responseCode) {
            super(responseCode);
            //AMBER-45
            String state = request.getParameter(OAuth.OAUTH_STATE);
            if (state != null) {
                this.setState(state);
            }
        }

        void setState(String state) {
            this.parameters.put(OAuth.OAUTH_STATE, state);
        }

        public OAuthAuthorizationResponseBuilder setCode(String code) {
            this.parameters.put(OAuth.OAUTH_CODE, code);
            return this;
        }

        public OAuthAuthorizationResponseBuilder location(String location) {
            this.location = location;
            return this;
        }
    }
}
