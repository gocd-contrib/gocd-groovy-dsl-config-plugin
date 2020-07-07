/*
 * Copyright 2020 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.contrib.plugins.configrepo.groovy.dsl.util;

import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UriUtils {

    public static boolean isHttp(String url) {
        try {
            URI uri = new URI(url);
            final String scheme = uri.getScheme();
            return StringUtils.isNotBlank(scheme) && scheme.startsWith("http");
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public static String maskAuth(String url) {
        try {
            URI uri = new URI(url);
            String userInfo = uri.getUserInfo();
            if (StringUtils.isNotBlank(userInfo)) {
                userInfo = userInfo.replaceFirst(":.*", "********");
                uri = new URI(
                        uri.getScheme(),
                        userInfo,
                        uri.getHost(),
                        uri.getPort(),
                        uri.getPath(),
                        uri.getQuery(),
                        uri.getFragment()
                );
            }
            return uri.toString();
        } catch (URISyntaxException e) {
            return url;
        }
    }

    public static String embedAuth(String url, String user, String pass) {
        try {
            URI uri = new URI(url);
            if (StringUtils.isNoneBlank(user, pass)) {
                uri = new URI(
                        uri.getScheme(),
                        asUserInfo(user, pass),
                        uri.getHost(),
                        uri.getPort(),
                        uri.getPath(),
                        uri.getQuery(),
                        uri.getFragment()
                );
            }
            return uri.toString();
        } catch (URISyntaxException e) {
            return url;
        }
    }

    public static String sanitizeUrl(String url) {
        try {
            URI uri = new URI(url);
            return uri.toString();
        } catch (URISyntaxException e) {
            return url;
        }
    }

    public static String stripAuth(String url) {
        try {
            URI uri = new URI(url);
            uri = new URI(
                    uri.getScheme(),
                    null,
                    uri.getHost(),
                    uri.getPort(),
                    uri.getPath(),
                    uri.getQuery(),
                    uri.getFragment()
            );
            return uri.toString();
        } catch (URISyntaxException e) {
            return url;
        }
    }

    public static String encode(String value) {
        return URLEncoder.encode(value, UTF_8);
    }

    private static String asUserInfo(String user, String pass) {
        return encode(user) + ":" + encode(pass);
    }
}
