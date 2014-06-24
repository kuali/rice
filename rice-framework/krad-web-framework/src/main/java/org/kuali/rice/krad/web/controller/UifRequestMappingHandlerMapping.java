/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.web.controller;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Overriding of provided spring handler mapping to allow controllers with the same mapping to exist, and override
 * if necessary.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private final Map<RequestMappingInfo, HandlerMethod> handlerMethods =
            new LinkedHashMap<RequestMappingInfo, HandlerMethod>();

    private final MultiValueMap<String, RequestMappingInfo> urlMap =
            new LinkedMultiValueMap<String, RequestMappingInfo>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<RequestMappingInfo, HandlerMethod> getHandlerMethods() {
        return Collections.unmodifiableMap(this.handlerMethods);
    }

    /**
     * Override to only populate the first handler given for a mapping.
     *
     * {@inheritDoc}
     */
    @Override
    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        HandlerMethod newHandlerMethod = super.createHandlerMethod(handler, method);

        this.handlerMethods.put(mapping, newHandlerMethod);
        if (logger.isInfoEnabled()) {
            logger.info("Mapped \"" + mapping + "\" onto " + newHandlerMethod);
        }

        if (!this.handlerMethods.containsKey(mapping)) {
            Set<String> patterns = super.getMappingPathPatterns(mapping);
            for (String pattern : patterns) {
                if (!super.getPathMatcher().isPattern(pattern)) {
                    this.urlMap.add(pattern, mapping);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
        List<Match> matches = new ArrayList<Match>();

        List<RequestMappingInfo> directPathMatches = this.urlMap.get(lookupPath);
        if (directPathMatches != null) {
            addMatchingMappings(directPathMatches, matches, request);
        }

        if (matches.isEmpty()) {
            // No choice but to go through all mappings
            addMatchingMappings(this.handlerMethods.keySet(), matches, request);
        }

        if (!matches.isEmpty()) {
            Comparator<Match> comparator = new MatchComparator(super.getMappingComparator(request));
            Collections.sort(matches, comparator);

            if (logger.isTraceEnabled()) {
                logger.trace("Found " + matches.size() + " matching mapping(s) for [" + lookupPath + "] : " + matches);
            }

            Match bestMatch = matches.get(0);
            if (matches.size() > 1) {
                Match secondBestMatch = matches.get(1);
                if (comparator.compare(bestMatch, secondBestMatch) == 0) {
                    Method m1 = bestMatch.handlerMethod.getMethod();
                    Method m2 = secondBestMatch.handlerMethod.getMethod();
                    throw new IllegalStateException(
                            "Ambiguous handler methods mapped for HTTP path '" + request.getRequestURL() + "': {" +
                                    m1 + ", " + m2 + "}");
                }
            }

            handleMatch(bestMatch.mapping, lookupPath, request);
            return bestMatch.handlerMethod;
        } else {
            return handleNoMatch(handlerMethods.keySet(), lookupPath, request);
        }
    }


    private void addMatchingMappings(Collection<RequestMappingInfo> mappings, List<Match> matches,
            HttpServletRequest request) {
        for (RequestMappingInfo mapping : mappings) {
            RequestMappingInfo match = getMatchingMapping(mapping, request);
            if (match != null) {
                matches.add(new Match(match, handlerMethods.get(mapping)));
            }
        }
    }

    private static class Match {
        private final RequestMappingInfo mapping;
        private final HandlerMethod handlerMethod;

        private Match(RequestMappingInfo mapping, HandlerMethod handlerMethod) {
            this.mapping = mapping;
            this.handlerMethod = handlerMethod;
        }

        @Override
        public String toString() {
            return this.mapping.toString();
        }
    }

    private static class MatchComparator implements Comparator<Match> {
        private final Comparator<RequestMappingInfo> comparator;

        public MatchComparator(Comparator<RequestMappingInfo> comparator) {
            this.comparator = comparator;
        }

        public int compare(Match match1, Match match2) {
            return this.comparator.compare(match1.mapping, match2.mapping);
        }
    }
}
