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

package cn.com.mfish.oauth.oltu.common.validators;

import cn.com.mfish.oauth.oltu.common.OAuth;
import cn.com.mfish.oauth.oltu.common.utils.OAuthUtils;
import cn.com.mfish.oauth.oltu.exception.OAuthProblemException;
import javax.servlet.http.HttpServletRequest;

import java.util.*;

public abstract class AbstractValidator<T extends HttpServletRequest> implements OAuthValidator<T> {

    protected List<String> requiredParams = new ArrayList<String>();
    protected Map<String, String[]> optionalParams = new HashMap<String, String[]>();
    protected List<String> notAllowedParams = new ArrayList<String>();
    protected boolean enforceClientAuthentication;

    @Override
    public void validateMethod(T request) throws OAuthProblemException {
        if (!request.getMethod().equals(OAuth.HttpMethod.POST)) {
            throw OAuthUtils.handleOAuthProblemException("Method not set to POST.");
        }
    }

    @Override
    public void validateContentType(T request) throws OAuthProblemException {
        String contentType = request.getContentType();
        final String expectedContentType = OAuth.ContentType.URL_ENCODED;
        if (!OAuthUtils.hasContentType(contentType, expectedContentType)) {
            throw OAuthUtils.handleBadContentTypeException(expectedContentType);
        }
    }

    @Override
    public void validateRequiredParameters(T request) throws OAuthProblemException {
        final Set<String> missingParameters = new HashSet<String>();
        for (String requiredParam : requiredParams) {
            String val = request.getParameter(requiredParam);
            if (OAuthUtils.isEmpty(val)) {
                missingParameters.add(requiredParam);
            }
        }
        if (!missingParameters.isEmpty()) {
            throw OAuthUtils.handleMissingParameters(missingParameters);
        }
    }

    @Override
    public void validateOptionalParameters(T request) throws OAuthProblemException {
        final Set<String> missingParameters = new HashSet<String>();

        for (Map.Entry<String, String[]> requiredParam : optionalParams.entrySet()) {
            final String paramName = requiredParam.getKey();
            String val = request.getParameter(paramName);
            if (!OAuthUtils.isEmpty(val)) {
                String[] dependentParams = requiredParam.getValue();
                if (!OAuthUtils.hasEmptyValues(dependentParams)) {
                    for (String dependentParam : dependentParams) {
                        val = request.getParameter(dependentParam);
                        if (OAuthUtils.isEmpty(val)) {
                            missingParameters.add(dependentParam);
                        }
                    }
                }
            }
        }

        if (!missingParameters.isEmpty()) {
            throw OAuthUtils.handleMissingParameters(missingParameters);
        }
    }

    @Override
    public void validateNotAllowedParameters(T request) throws OAuthProblemException {
        List<String> notAllowedParameters = new ArrayList<String>();
        for (String requiredParam : notAllowedParams) {
            String val = request.getParameter(requiredParam);
            if (!OAuthUtils.isEmpty(val)) {
                notAllowedParameters.add(requiredParam);
            }
        }
        if (!notAllowedParameters.isEmpty()) {
            throw OAuthUtils.handleNotAllowedParametersOAuthException(notAllowedParameters);
        }
    }

    @Override
    public void validateClientAuthenticationCredentials(T request) throws OAuthProblemException {
        if (enforceClientAuthentication) {
            Set<String> missingParameters = new HashSet<String>();
            String clientAuthHeader = request.getHeader(OAuth.HeaderType.AUTHORIZATION);
            String[] clientCreds = OAuthUtils.decodeClientAuthenticationHeader(clientAuthHeader);

            // Only fallback to params if the auth header is not correct. Don't allow a mix of auth header vs params
            if (clientCreds == null || OAuthUtils.isEmpty(clientCreds[0]) || OAuthUtils.isEmpty(clientCreds[1])) {

                if (OAuthUtils.isEmpty(request.getParameter(OAuth.OAUTH_CLIENT_ID))) {
                    missingParameters.add(OAuth.OAUTH_CLIENT_ID);
                }
                if (OAuthUtils.isEmpty(request.getParameter(OAuth.OAUTH_CLIENT_SECRET))) {
                    missingParameters.add(OAuth.OAUTH_CLIENT_SECRET);
                }
            }

            if (!missingParameters.isEmpty()) {
                throw OAuthUtils.handleMissingParameters(missingParameters);
            }
        }
    }
}
