/**
 * Copyright 2005-2013 The Kuali Foundation
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

import java.util.Deque;

/**
 * Provides modular support for the partial JSP EL path syntax using an arbitrary root bean as the
 * initial name space.
 * 
 * <p>
 * NOTE: This is not full JSP EL, only the path reference portion without support for floating point
 * literals. See this <a href= "http://jsp.java.net/spec/jsp-2_1-fr-spec-el.pdf"> JSP Reference</a>
 * for the full BNF.
 * </p>
 * 
 * <pre>
 * Value ::= ValuePrefix (ValueSuffix)*
 * ValuePrefix ::= Literal
 *     | NonLiteralValuePrefix
 * NonLiteralValuePrefix ::= '(' Expression ')'
 *     | Identifier
 * ValueSuffix ::= '.' Identifier
 *     | '[' Expression ']'
 * Identifier ::= Java language identifier
 * Literal ::= BooleanLiteral
 *     | IntegerLiteral
 *     | FloatingPointLiteral
 *     | StringLiteral
 *     | NullLiteral
 * BooleanLiteral ::= 'true'
 *     | 'false'
 * StringLiteral ::= '([^'\]|\'|\\)*'
 *     | "([^"\]|\"|\\)*"
 *   i.e., a string of any characters enclosed by
 *   single or double quotes, where \ is used to
 *   escape ', ",and \. It is possible to use single
 *   quotes within double quotes, and vice versa,
 *   without escaping.
 * IntegerLiteral ::= ['0'-'9']+
 * NullLiteral ::= 'null'
 * </pre>
 * 
 * <p>
 * Support for treating bracketed expresssions as string literals is also provided, for equivlence
 * with Spring's BeanWrapper expression Syntax. (see <a href=
 * "http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/validation.html"
 * >The Spring Manual</a>)
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ObjectPathExpressionParser {

    /**
     * Used by {@link #parsePathExpression(Object, String, PathEntry)} to track parse state without
     * the need to construct a new parser stack for each expression.
     */
    private static final ThreadLocal<ParseState> TL_EL_PARSE_STATE = new ThreadLocal<ParseState>();

    /**
     * Tracks parser state for
     * {@link ObjectPathExpressionParser#parsePathExpression(Object, String, PathEntry)}.
     */
    private static final class ParseState {

        /**
         * The continuation stack.
         * <p>
         * When evaluating subexpressions, the outer expression is pushed onto this stack.
         * </p>
         */
        private final Deque<Object> stack = new java.util.LinkedList<Object>();

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
         * The root continuation.
         */
        private Object root;

        /**
         * The continuation point of the parse expression currently being evaluation.
         */
        private Object currentContinuation;

        /**
         * True to use Spring syntax (bracketed literals), false for standard EL syntax.
         */
        private boolean useSpringSyntax;

        /**
         * Determine if this parse state is active.
         */
        private boolean isActive() {
            return (stack != null && !stack.isEmpty()) || currentContinuation != null;
        }

        /**
         * Reset parse state, allowing this state marker to be reused on the next expression.
         */
        private void reset() {
            stack.clear();
            currentContinuation = null;
            useSpringSyntax = false;
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
         * @return The path expression, with parentheses related to a grouping on the left removed.
         */
        public String prepareNextScan(String path) {
            nextScanIndex = 0;

            char firstChar = path.charAt(0);

            boolean quote = firstChar == '\'' || firstChar == '\"';
            boolean paren = firstChar == '(';
            boolean bracket = firstChar == '[';

            if (!quote && !paren && (!useSpringSyntax || !bracket)) {
                return path;
            }

            // Track paren, bracket, and quote state.
            int parenCount = firstChar == '(' ? 1 : 0;
            int bracketCount = firstChar == '[' ? 1 : 0;
            char inQuote = quote ? firstChar : '\0';

            // Look back during lexical scanning to detect quote and escape characters.
            char lastChar = firstChar;
            char currentChar;

            // Position of the last character in the path
            int pathLen = path.length() - 1;

            while (nextScanIndex < pathLen
                    && ((paren && parenCount > 0) || (bracket && bracketCount > 0) || (quote && inQuote != '\0'))) {
                nextScanIndex++;
                currentChar = path.charAt(nextScanIndex);

                // Ignore escaped characters.
                if (lastChar == '\\') {
                    continue;
                }

                // Toggle quote state when a quote is encountered.
                if (inQuote == '\0') {
                    if (currentChar == '\'' || currentChar == '\"') {
                        inQuote = currentChar;
                    }
                } else if (currentChar == inQuote) {
                    inQuote = '\0';
                }

                // Ignore quoted characters.
                if (inQuote != '\0') {
                    continue;
                }

                switch (currentChar) {
                    case '(':
                        if (paren) {
                            parenCount++;
                        }
                        break;
                    case ')':
                        if (paren) {
                            parenCount--;
                        }
                        break;
                    case '[':
                        if (bracket) {
                            bracketCount++;
                        }
                        break;
                    case ']':
                        if (bracket) {
                            bracketCount--;
                        }
                        break;
                }
            }

            if (parenCount > 0) {
                throw new IllegalArgumentException("Unmatched '(': " + path);
            }

            if (bracketCount > 0) {
                throw new IllegalArgumentException("Unmatched '[': " + path);
            }

            if (paren || bracket) {

                assert (paren && path.charAt(nextScanIndex) == ')') || (bracket && path.charAt(nextScanIndex) == ']');
                if (nextScanIndex < pathLen) {
                    path = path.substring(1, nextScanIndex) + path.substring(nextScanIndex + 1);
                } else {
                    path = path.substring(1, nextScanIndex);
                }
                nextScanIndex--;

                // Move original path index forward to correct for stripped brackets
                originalPathIndex += 2;

            } else {
                nextScanIndex++;
            }

            // Also strip quotes from the front/back of a bracket subexpression when using Spring syntax
            if (bracket && nextScanIndex <= path.length()) {
                firstChar = path.charAt(0);
                if ((firstChar == '\'' || firstChar == '\"') && path.charAt(nextScanIndex - 1) == firstChar) {
                    path = path.substring(1, nextScanIndex - 1) + path.substring(nextScanIndex);
                    nextScanIndex -= 2;

                    // Move original path index forward to correct for stripped quotes
                    originalPathIndex += 2;
                }
            }

            return path;
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
                    case ')':
                        // should have been removed by prepareNextScan
                        throw new IllegalArgumentException("Unmatched ')': " + path);
                    case ']':
                        if (useSpringSyntax) {
                            // should have been removed by prepareNextScan
                            throw new IllegalArgumentException("Unmatched ']': " + path);
                        }
                        // else fall through
                    case '\'':
                    case '\"':
                    case '(':
                    case '[':
                    case '.':
                        if (nextTokenIndex == -1) {
                            nextTokenIndex = currentIndex;
                        }

                        // Move original path index forward
                        originalPathIndex += nextTokenIndex;
                        parentPath = originalPath.substring(0, originalPathIndex);

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
                currentContinuation = pathEntry.parse(parentPath, pathEntry.prepare(currentContinuation), path, false);
                return null;
            }

            char nextToken = path.charAt(nextTokenIndex);

            switch (nextToken) {

                case '[':
                    if (!useSpringSyntax) {
                        // Entering bracketed subexpression

                        // Resolve non-empty key reference as the current continuation
                        if (nextTokenIndex != 0) {
                            currentContinuation = pathEntry.parse(parentPath,
                                    pathEntry.prepare(currentContinuation),
                                    path.substring(0, nextTokenIndex), false);
                        }

                        // Push current continuation down in the stack.
                        stack.push(currentContinuation);

                        // Reset the current continuation for evaluating the
                        // subexpression
                        currentContinuation = pathEntry.parse(parentPath, root, null, false);

                        // Step past the left bracket
                        originalPathIndex++;

                        return path.substring(nextTokenIndex + 1);
                    }
                    // else fall through

                case '(':
                    // Approaching a parenthetical expression, not preceded by a subexpression,
                    // resolve the key reference as the current continuation
                    currentContinuation = pathEntry.parse(parentPath,
                            pathEntry.prepare(currentContinuation),
                            path.substring(0, nextTokenIndex), false);
                    return path.substring(nextTokenIndex); // Keep the left parenthesis

                case '.':
                    // Crossing a period, not preceded by a subexpression,
                    // resolve the key reference as the current continuation
                    currentContinuation = pathEntry.parse(parentPath,
                            pathEntry.prepare(currentContinuation),
                            path.substring(0, nextTokenIndex), false);

                    // Step past the period
                    originalPathIndex++;

                    return path.substring(nextTokenIndex + 1);

                case ']':
                    if (!useSpringSyntax) {

                        if (nextTokenIndex > 0) {
                            // Approaching a right bracket, resolve the key reference as the current continuation
                            currentContinuation = pathEntry.parse(parentPath,
                                    pathEntry.prepare(currentContinuation),
                                    path.substring(0, nextTokenIndex), false);
                            return path.substring(nextTokenIndex); // Keep the right bracket
                        }

                        // Crossing a right bracket.

                        // Use the current continuation as the parameter for resolving
                        // the top continuation on the stack, then make the result the
                        // current continuation.
                        currentContinuation = pathEntry.parse(parentPath,
                                pathEntry.prepare(stack.pop()),
                                pathEntry.dereference(currentContinuation), true);
                        if (nextTokenIndex + 1 >= path.length()) {
                            return null;
                        }

                        // short-circuit the next step, as an optimization for
                        // handling dot resolution without permitting double-dots
                        switch (path.charAt(nextTokenIndex + 1)) {
                            case '.':
                                // crossing a dot, skip it
                                originalPathIndex += 2;

                                return path.substring(nextTokenIndex + 2);
                            case '[':
                            case ']':
                                // crossing to another subexpression, don't skip it.
                                originalPathIndex++;

                                return path.substring(nextTokenIndex + 1);
                            default:
                                throw new IllegalArgumentException(
                                        "Expected '.', '[', or ']': " + path);
                        }
                    }
                    // else fall through

                default:
                    throw new IllegalArgumentException("Unexpected '" + nextToken + "' :" + path);
            }
        }

    }

    /**
     * Path entry interface for use with
     * {@link ObjectPathExpressionParser#parsePathExpression(Object, String, PathEntry)}.
     */
    public static interface PathEntry {

        /**
         * Parse one node.
         * 
         * @param node The current parse node.
         * @param next The next path token.
         * @param inherit True indicates that the current node is the result of a subexpression,
         *        false indicates the next node in the same expression.
         * @return A reference to the next parse node.
         */
        Object parse(String parentPath, Object node, String next, boolean inherit);

        /**
         * Prepare the next parse node based on a reference returned from the prior node.
         * 
         * @param prev The reference data from the previous node.
         * @return The next parse node.
         */
        Object prepare(Object prev);

        /**
         * Resolve the next path element based on reference data from the previous node.
         * 
         * @param prev The reference data from the previous node.
         * @return the next path element based on the returned reference data.
         */
        String dereference(Object prev);
    }

    /**
     * Determine if a property name is a path or a plain property reference.
     *
     * <p>
     * This method is used to eliminate parsing and object creation overhead when resolving an
     * object property reference with a non-complex property path.
     * </p>
     *
     * @return True if the name is a path, false if a plain reference.
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
     * @param <T> Reference type representing the next parse node.
     * @param <S> The parse node type.
     * 
     * @return The valid of the bean property indicated by the given path expression, null if the
     *         path expression doesn't resolve to a valid property.
     * @see ObjectPathExpressionParser#getPropertyValue(Object, String)
     */
    public static Object parsePathExpression(Object root, String path, boolean useSpringSyntax,
            final PathEntry pathEntry) {

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
            parseState.root = root;
            parseState.currentContinuation = pathEntry.parse(null, root, null, false);
            parseState.useSpringSyntax = useSpringSyntax;
            while (path != null) {
                path = parseState.prepareNextScan(path);
                parseState.scan(path);
                path = parseState.step(path, pathEntry);
            }
            return parseState.currentContinuation;
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
