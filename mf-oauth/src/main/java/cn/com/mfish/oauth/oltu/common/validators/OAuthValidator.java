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

package cn.com.mfish.oauth.oltu.common.validators;


import cn.com.mfish.oauth.oltu.exception.OAuthProblemException;
import javax.servlet.http.HttpServletRequest;

public interface OAuthValidator<T extends HttpServletRequest> {

    void validateMethod(T request) throws OAuthProblemException;

    void validateContentType(T request) throws OAuthProblemException;

    void validateRequiredParameters(T request) throws OAuthProblemException;

    void validateOptionalParameters(T request) throws OAuthProblemException;

    void validateNotAllowedParameters(T request) throws OAuthProblemException;

    void validateClientAuthenticationCredentials(T request) throws OAuthProblemException;
}
