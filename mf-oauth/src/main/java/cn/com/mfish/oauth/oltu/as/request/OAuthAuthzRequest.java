/**
 *       Copyright 2010 Newcastle University
 *
 *          http://research.ncl.ac.uk/smart/
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.com.mfish.oauth.oltu.as.request;


import cn.com.mfish.oauth.oltu.as.validator.CodeValidator;
import cn.com.mfish.oauth.oltu.as.validator.TokenValidator;
import cn.com.mfish.oauth.oltu.common.OAuth;
import cn.com.mfish.oauth.oltu.common.message.types.ResponseType;
import cn.com.mfish.oauth.oltu.common.utils.OAuthUtils;
import cn.com.mfish.oauth.oltu.common.validators.OAuthValidator;
import cn.com.mfish.oauth.oltu.exception.OAuthProblemException;
import cn.com.mfish.oauth.oltu.exception.OAuthSystemException;
import jakarta.servlet.http.HttpServletRequest;


public class OAuthAuthzRequest extends OAuthRequest {

    public OAuthAuthzRequest(HttpServletRequest request) throws OAuthSystemException, OAuthProblemException {
        super(request);
    }

    @Override
    protected OAuthValidator<HttpServletRequest> initValidator() throws OAuthProblemException, OAuthSystemException {
        //end user authorization validators
        validators.put(ResponseType.CODE.toString(), CodeValidator.class);
        validators.put(ResponseType.TOKEN.toString(), TokenValidator.class);
        final String requestTypeValue = getParam(OAuth.OAUTH_RESPONSE_TYPE);
        if (OAuthUtils.isEmpty(requestTypeValue)) {
            throw OAuthUtils.handleOAuthProblemException("Missing response_type parameter value");
        }
        final Class<? extends OAuthValidator<HttpServletRequest>> clazz = validators.get(requestTypeValue);
        if (clazz == null) {
            throw OAuthUtils.handleOAuthProblemException("Invalid response_type parameter value");
        }

        return OAuthUtils.instantiateClass(clazz);
    }

    public String getState() {
        return getParam(OAuth.OAUTH_STATE);
    }

    public String getResponseType() {
        return getParam(OAuth.OAUTH_RESPONSE_TYPE);
    }

}
