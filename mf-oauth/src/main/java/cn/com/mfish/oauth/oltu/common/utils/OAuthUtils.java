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

package cn.com.mfish.oauth.oltu.common.utils;

import cn.com.mfish.oauth.oltu.common.error.OAuthError;
import cn.com.mfish.oauth.oltu.exception.OAuthProblemException;
import cn.com.mfish.oauth.oltu.exception.OAuthSystemException;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.*;

public final class OAuthUtils {

    private static final String ENCODING = "UTF-8";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String NAME_VALUE_SEPARATOR = "=";
    private static final String DEFAULT_CONTENT_CHARSET = ENCODING;

    /**
     * Translates parameters into <code>application/x-www-form-urlencoded</code> String
     *
     * @param parameters parameters to encode
     * @param encoding   The name of a supported
     *                   <a href="../lang/package-summary.html#charenc">character
     *                   encoding</a>.
     * @return Translated string
     */
    public static String format(
            final Collection<? extends Map.Entry<String, Object>> parameters,
            final String encoding) {
        final StringBuilder result = new StringBuilder();
        for (final Map.Entry<String, Object> parameter : parameters) {
            String value = parameter.getValue() == null ? null : String.valueOf(parameter.getValue());
            if (!OAuthUtils.isEmpty(parameter.getKey())
                    && !OAuthUtils.isEmpty(value)) {
                final String encodedName = encode(parameter.getKey(), encoding);
                final String encodedValue = encode(value, encoding);
                if (!result.isEmpty()) {
                    result.append(PARAMETER_SEPARATOR);
                }
                result.append(encodedName);
                result.append(NAME_VALUE_SEPARATOR);
                result.append(encodedValue);
            }
        }
        return result.toString();
    }

    private static String encode(final String content, final String encoding) {
        try {
            return URLEncoder.encode(content,
                    encoding != null ? encoding : "UTF-8");
        } catch (UnsupportedEncodingException problem) {
            throw new IllegalArgumentException(problem);
        }
    }

    /**
     * Get the entity content as a String, using the provided default character set
     * if none is found in the entity.
     * If defaultCharset is null, the default "UTF-8" is used.
     *
     * @param is             input stream to be saved as string
     * @param defaultCharset character set to be applied if none found in the entity
     * @return the entity content as a String
     * @throws IllegalArgumentException if entity is null or if content length > Integer.MAX_VALUE
     * @throws IOException              if an error occurs reading the input stream
     */
    public static String toString(
            final InputStream is, final String defaultCharset) throws IOException {
        if (is == null) {
            throw new IllegalArgumentException("InputStream may not be null");
        }

        String charset = defaultCharset;
        if (charset == null) {
            charset = DEFAULT_CONTENT_CHARSET;
        }
        Reader reader = new InputStreamReader(is, charset);
        StringBuilder sb = new StringBuilder();
        int l;
        try {
            char[] tmp = new char[4096];
            while ((l = reader.read(tmp)) != -1) {
                sb.append(tmp, 0, l);
            }
        } finally {
            reader.close();
        }
        return sb.toString();
    }

    /**
     * Creates invalid_request exception with given message
     *
     * @param message error message
     * @return OAuthException
     */
    public static OAuthProblemException handleOAuthProblemException(String message) {
        return OAuthProblemException.error(OAuthError.TokenResponse.INVALID_REQUEST)
                .description(message);
    }

    /**
     * Creates OAuthProblemException that contains set of missing oauth parameters
     *
     * @param missingParams missing oauth parameters
     * @return OAuthProblemException with user friendly message about missing oauth parameters
     */

    public static OAuthProblemException handleMissingParameters(Set<String> missingParams) {
        StringBuilder sb = new StringBuilder("Missing parameters: ");
        if (!OAuthUtils.isEmpty(missingParams)) {
            for (String missingParam : missingParams) {
                sb.append(missingParam).append(" ");
            }
        }
        return handleOAuthProblemException(sb.toString().trim());
    }

    public static OAuthProblemException handleBadContentTypeException(String expectedContentType) {
        String errorMsg = "Bad request content type. Expecting: " +
                expectedContentType;
        return handleOAuthProblemException(errorMsg);
    }

    public static OAuthProblemException handleNotAllowedParametersOAuthException(
            List<String> notAllowedParams) {
        StringBuilder sb = new StringBuilder("Not allowed parameters: ");
        if (notAllowedParams != null) {
            for (String notAllowed : notAllowedParams) {
                sb.append(notAllowed).append(" ");
            }
        }
        return handleOAuthProblemException(sb.toString().trim());
    }

    private static boolean isEmpty(Set<String> missingParams) {
        return missingParams == null || missingParams.isEmpty();
    }

    public static <T> T instantiateClass(Class<T> clazz) throws OAuthSystemException {
        return instantiateClassWithParameters(clazz, null, null);
    }

    public static <T> T instantiateClassWithParameters(Class<T> clazz, Class<?>[] paramsTypes,
                                                       Object[] paramValues) throws OAuthSystemException {

        try {
            if (paramsTypes != null && paramValues != null) {
                if (!(paramsTypes.length == paramValues.length)) {
                    throw new IllegalArgumentException("Number of types and values must be equal");
                }

                if (paramsTypes.length == 0) {
                    return clazz.getDeclaredConstructor().newInstance();
                }
                Constructor<T> clazzConstructor = clazz.getConstructor(paramsTypes);
                return clazzConstructor.newInstance(paramValues);
            }
            return clazz.getDeclaredConstructor().newInstance();

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new OAuthSystemException(e);
        }

    }

    // todo: implement method to decode header form (with no challenge)

    /**
     * Decodes the Basic Authentication header into a username and password
     *
     * @param authenticationHeader {@link String} containing the encoded header value.
     *                             e.g. "Basic dXNlcm5hbWU6cGFzc3dvcmQ="
     * @return a {@link String[]} if the header could be decoded into a non null username and password or null.
     */
    public static String[] decodeClientAuthenticationHeader(String authenticationHeader) {
        if (isEmpty(authenticationHeader)) {
            return null;
        }
        String[] tokens = authenticationHeader.split(" ");
        if (tokens.length != 2) {
            return null;
        }
        String authType = tokens[0];
        if (!"basic".equalsIgnoreCase(authType)) {
            return null;
        }
        String encodedCreds = tokens[1];
        return decodeBase64EncodedCredentials(encodedCreds);
    }

    private static String[] decodeBase64EncodedCredentials(String encodedCreds) {
        String decodedCreds = new String(Base64.decodeBase64(encodedCreds));
        String[] creds = decodedCreds.split(":", 2);
        if (creds.length != 2) {
            return null;
        }
        if (!OAuthUtils.isEmpty(creds[0]) && !OAuthUtils.isEmpty(creds[1])) {
            return creds;
        }
        return null;
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean hasEmptyValues(String[] array) {
        if (array == null || array.length == 0) {
            return true;
        }
        for (String s : array) {
            if (isEmpty(s)) {
                return true;
            }
        }
        return false;
    }

    public static Set<String> decodeScopes(String s) {
        Set<String> scopes = new HashSet<>();
        if (!OAuthUtils.isEmpty(s)) {
            StringTokenizer tokenizer = new StringTokenizer(s, " ");

            while (tokenizer.hasMoreElements()) {
                scopes.add(tokenizer.nextToken());
            }
        }
        return scopes;
    }

    public static boolean hasContentType(String requestContentType, String requiredContentType) {
        if (OAuthUtils.isEmpty(requiredContentType) || OAuthUtils.isEmpty(requestContentType)) {
            return false;
        }
        StringTokenizer tokenizer = new StringTokenizer(requestContentType, ";");
        while (tokenizer.hasMoreTokens()) {
            if (requiredContentType.equals(tokenizer.nextToken())) {
                return true;
            }
        }

        return false;
    }
}


