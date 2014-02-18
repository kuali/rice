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
package org.kuali.rice.krad.uif.util;


/**
 * Provides modular support parsing path expressions using Spring's BeanWrapper expression Syntax.
 * (see <a href=
 * "http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/validation.html"
 * >The Spring Manual</a>)
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class ObjectPathExpressionParser {

    /**
     * Used by {@link #parsePathExpression(Object, String, PathEntry)} to track parse state without
     * the need to construct a new parser stack for each expression.
     */
    private static final ThreadLocal<ParseState> TL_EL_PARSE_STATE = new ThreadLocal<ParseState>();

    /**
     * Path entry interface for use with
     * {@link ObjectPathExpressionParser#parsePathExpression(Object, String, PathEntry)}.
     */
    public static interface PathEntry {

        /**
         * Parse one node.
         * 
         * @param parentPath The path expression parsed so far.
         * @param node The current parse node.
         * @param next The next path token.
         * @return A reference to the next parse node.
         */
        Object parse(String parentPath, Object node, String next);

    }

    /**
     * Tracks parser state for
     * {@link ObjectPathExpressionParser#parsePathExpression(Object, String, PathEntry)}.
     */
    private static final class ParseState {

        /**
         * The lexical index at which to begin the next lexical scan.
         */
        private int nextScanIndex;

        /**
         * The lexical index of the next path separator token.
         */
        private int nextTokenIndex;

        /**
         * The full original parse string.
         */
        private String originalPath;

        /**
         * The current lexical index in the original path.
         */
        private int originalPathIndex;

        /**
         * The portion of the path parsed so far.
         */
        private String parentPath;

        /**
         * The continuation point of the parse expression currently being evaluation.
         */
        private Object currentContinuation;

        /**
         * Determine if this parse state is active.
         */
        private boolean isActive() {
            return currentContinuation != null;
        }

        /**
         * Reset parse state, allowing this state marker to be reused on the next expression.
         */
        private void reset() {
            currentContinuation = null;
            originalPath = null;
            originalPathIndex = 0;
            parentPath = null;
        }

        /**
         * Prepare for the next lexical scan.
         * 
         * <p>
         * When a parenthetical expression occurs on the left hand side of the path, remove the
         * parentheses.
         * </p>
         * 
         * <p>
         * When using Spring syntax, treat brackets the same as parentheses.
         * </p>
         * 
         * <p>
         * Upon returning from this method, the value of {@link #nextScanIndex} will point at the
         * position of the character formerly to the right of the removed parenthetical group, if
         * applicable. If no parenthetical group was removed, {@link #nextScanIndex} will be reset
         * to 0.
         * </p>
         * 
         * @param path The path expression from the current continuation point.
         * @return The path expression, with brackets and quotes related to a collection reference removed.
         */
        public String prepareNextScan(String path) {
            nextScanIndex = 0;

            if (path.length() == 0) {
                throw new IllegalArgumentException("Unexpected end of input " + parentPath);
            }

            int endOfCollectionReference = indexOfCloseBracket(path, 0);
            
            if (endOfCollectionReference == -1) {
                return path;
            }

            // Strip brackets from parse path.
            StringBuilder pathBuilder = new StringBuilder(path);
            pathBuilder.deleteCharAt(endOfCollectionReference);
            pathBuilder.deleteCharAt(0);

            // Also strip quotes from the front/back of the collection reference.
            char firstChar = pathBuilder.charAt(0);
            if ((firstChar == '\'' || firstChar == '\"') &&
                    path.charAt(endOfCollectionReference - 1) == firstChar) {
                
                pathBuilder.deleteCharAt(endOfCollectionReference - 2);
                pathBuilder.deleteCharAt(0);
            }
            
            int diff = path.length() - pathBuilder.length();

            // Step scan index past collection reference, accounting for stripped characters.
            nextScanIndex += endOfCollectionReference + 1 - diff;

            // Move original path index forward to correct for stripped characters.
            originalPathIndex += diff;

            return pathBuilder.toString();
        }

        /**
         * Update current parse state with the lexical indexes of the next token break.
         * 
         * @param path The path being parsed, starting from the current continuation point.
         */
        public void scan(String path) {
            nextTokenIndex = -1;

            // Scan the character sequence, starting with the character following the open marker.
            for (int currentIndex = nextScanIndex; currentIndex < path.length(); currentIndex++) {
                switch (path.charAt(currentIndex)) {
                    case ']':
                        // should have been removed by prepareNextScan
                        throw new IllegalArgumentException("Unmatched ']': " + path);
                        // else fall through
                    case '[':
                    case '.':
                        if (nextTokenIndex == -1) {
                            nextTokenIndex = currentIndex;
                        }

                        // Move original path index forward
                        originalPathIndex += nextTokenIndex;
                        return;
                }
            }
        }

        /**
         * Step to the next continuation point in the parse path.
         * 
         * <p>
         * Upon returning from this method, the value of {@link #currentContinuation} will reflect
         * the resolved state of parsing the path. When null is returned, then
         * {@link #currentContinuation} will be the reflect the result of parsing the expression.
         * </p>
         * 
         * @param path The path expression from the current continuation point.
         * @return The path expression for the next continuation point, null if the path has been
         *         completely parsed.
         */
        private String step(String path, PathEntry pathEntry) {

            if (nextTokenIndex == -1) {
                // Only a symbolic reference, resolve it and return.
                currentContinuation = pathEntry.parse(parentPath, currentContinuation, path);
                parentPath = originalPath.substring(0, originalPathIndex);
                return null;
            }

            char nextToken = path.charAt(nextTokenIndex);

            switch (nextToken) {

                case '[':
                    // Approaching a collection reference.
                    currentContinuation = pathEntry.parse(parentPath, currentContinuation,
                            path.substring(0, nextTokenIndex));
                    parentPath = originalPath.substring(0, originalPathIndex);
                    return path.substring(nextTokenIndex); // Keep the left parenthesis

                case '.':
                    // Crossing a period, not preceded by a collection reference.
                    currentContinuation = pathEntry.parse(parentPath, currentContinuation,
                            path.substring(0, nextTokenIndex));

                    // Step past the period
                    parentPath = originalPath.substring(0, originalPathIndex++);

                    return path.substring(nextTokenIndex + 1);

                default:
                    throw new IllegalArgumentException("Unexpected '" + nextToken + "' :" + path);
            }
        }

    }

    /**
     * Return the index of the close bracket that matches the bracket at the start of the path.
     * 
     * @param path The string to scan.
     * @param leftBracketIndex The index of the left bracket.
     * @return The index of the right bracket that matches the left bracket at index given. If the
     *         path does not begin with an open bracket, then -1 is returned.
     * @throw IllegalArgumentException If the left bracket is unmatched by the right bracket in the
     *        parse string.
     */
    public static int indexOfCloseBracket(String path, int leftBracketIndex) {
        if (path == null || path.length() <= leftBracketIndex || path.charAt(leftBracketIndex) != '[') {
            return -1;
        }

        char inQuote = '\0';
        int pathLen = path.length() - 1;
        int bracketCount = 1;
        int currentPos = leftBracketIndex;

        do {
            char currentChar = path.charAt(++currentPos);

            // Toggle quoted state as applicable.
            if (inQuote == '\0' && (currentChar == '\'' || currentChar == '\"')) {
                inQuote = currentChar;
            } else if (inQuote == currentChar) {
                inQuote = '\0';
            }

            // Ignore quoted characters.
            if (inQuote != '\0') continue;

            // Adjust bracket count as applicable.
            if (currentChar == '[') bracketCount++;
            if (currentChar == ']') bracketCount--;
        } while (currentPos < pathLen && bracketCount > 0);

        if (bracketCount > 0) {
            throw new IllegalArgumentException("Unmatched '[': " + path);
        }
        
        return currentPos;
    }

    /**
     * Determine if a property name is a path or a plain property reference.
     *
     * <p>
     * This method is used to eliminate parsing and object creation overhead when resolving an
     * object property reference with a non-complex property path.
     * </p>
     * @param propertyName property name
     *
     * @return true if the name is a path, false if a plain reference
     */
    public static boolean isPath(String propertyName) {
        if (propertyName == null) {
            return false;
        }

        int length = propertyName.length();
        for (int i = 0; i < length; i++) {
            char c = propertyName.charAt(i);
            if (c != '_' && c != '$' && !Character.isLetterOrDigit(c)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Parse a path expression.
     * 
     * @param root The root object.
     * @param path The path expression.
     * @param pathEntry The path entry adaptor to use for processing parse node transition.
     * 
     * @return The valid of the bean property indicated by the given path expression, null if the
     *         path expression doesn't resolve to a valid property.
     * @see ObjectPropertyUtils#getPropertyValue(Object, String)
     */
    @SuppressWarnings("unchecked")
    public static <T> T parsePathExpression(Object root, String path, final PathEntry pathEntry) {

        // NOTE: This iterative parser allows support for subexpressions
        // without recursion. When a subexpression start token '[' is
        // encountered the current continuation is pushed onto a stack. When
        // the subexpression is resolved, the continuation is popped back
        // off the stack and resolved using the subexpression result as the
        // arg. All subexpressions start with the same root passed in as an
        // argument for this method. - MWF

        ParseState parseState = (ParseState) TL_EL_PARSE_STATE.get();
        boolean recycle;

        if (parseState == null) {
            TL_EL_PARSE_STATE.set(new ParseState());
            parseState = TL_EL_PARSE_STATE.get();
            recycle = true;
        } else if (parseState.isActive()) {
            ProcessLogger.ntrace("el-parse:", ":nested", 100);
            parseState = new ParseState();
            recycle = false;
        } else {
            recycle = true;
        }

        try {
            parseState.originalPath = path;
            parseState.originalPathIndex = 0;
            parseState.parentPath = null;
            parseState.currentContinuation = pathEntry.parse(null, root, null);
            while (path != null) {
                path = parseState.prepareNextScan(path);
                parseState.scan(path);
                path = parseState.step(path, pathEntry);
            }
            return (T) parseState.currentContinuation;
        } finally {
            assert !recycle || parseState == TL_EL_PARSE_STATE.get();
            parseState.reset();
        }
    }

    /**
     * Private constructor - utility class only.
     */
    private ObjectPathExpressionParser() {}

}
