/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.util;

/**
 * This class contains utility methods for making assertions in production code (not test code).
 * Kuali is not using the {@code assert} keyword because those assertions
 * are not enabled by default. We can use the methods of this class instead, or throw {@link AssertionError} directly. This makes
 * assertions effective in production and development, and avoids any risk of changes to functionality because any side-effects that
 * might be involved in the assertion are done consistently. Although {@code AssertionError} can be thrown directly, and sometimes
 * the compiler requires this, in many cases these method invocations will be easier to read than the extra {@code if} block with
 * its negated conditional.
 * 
 * <p/> These assertions are for use in production code. They should not be confused with
 * {@link junit.framework.Assert}, {@link junit.framework.AssertionFailedError}, nor {@link org.kuali.test.util.KualiTestAssertionUtils},
 * which are for test code.
 * 
 * <p/> These methods
 * should be used such that when they fail and throw {@code AssertionError}, it indicates that there is a bug in our software.
 * Drawing attention to the bug this way, as soon as it's discovered, reduces the amount of work required to fix it. So, we should
 * not catch {@code AssertionError} (or {@link Throwable}) and try to handle or work around it; it indicates a need to change some
 * source code so that the assertion is never false. For more about why, when, and how to use assertions, see <a
 * href="https://test.kuali.org/confluence/display/KULDEV/Assert+Keyword+And+AssertionError">Kuali's guide to assertions</a> and <a
 * href="http://java.sun.com/j2se/1.4.2/docs/guide/lang/assert.html">Sun's guide to assertions</a>.
 * 
 * @see org.kuali.test.util.KualiTestAssertionUtils
 */
public class AssertionUtils {

    /**
     * Asserts that the {@code isTrue} parameter is true. If this assertion fails, the {@code AssertionError} it throws will have no
     * detail message, only a stacktrace indicating the source file and line number containing the assertion that failed. <p/> This
     * method's name is intended to avoid confusion with JUnit asserts (which are for test code, not production code), and its
     * signature is intended to resemble that of the {@code assert} keyword.
     * 
     * @param isTrue whether this assertion succeeds. (Boolean objects are auto-unboxed by JDK 1.5.)
     * 
     * @throws AssertionError if {@code isTrue} is false
     */
    public static void assertThat(boolean isTrue) {
        if (!isTrue) {
            throw new AssertionError();
        }
    }

    /**
     * Asserts that the {@code isTrue} parameter is true, with a detail message. The purpose of the detail message is to capture and
     * communicate details about the assertion failure that will help a developer diagnose and fix the bug that led this assertion
     * to fail. It's meant to be interpreted in the context of a full stack trace and with the source code containing the failed
     * assertion. It is <em>not</em> a user-level error message. Details like {@code "assertion failed"} are redundant, not
     * useful. <p/> This method's name is intended to avoid confusion with JUnit asserts (which are for test code, not production
     * code), and its signature is intended to resemble that of the {@code assert} keyword.
     * 
     * @param isTrue whether this assertion succeeds. (Boolean objects are auto-unboxed by JDK 1.5.)
     * 
     * @param detailMessage value to use for the {@code AssertionError}'s detail message. If this is an instance of
     *        {@link Throwable}, then it also becomes the {@code AssertionError}'s cause. (Primitives are auto-boxed by JDK 1.5.)
     *        Objects are converted {@link Object#toString toString}, but only if this assertion fails, so it's better not to
     *        convert this detail in advance. The code will be a little easier to read, and there will be some performance
     *        improvement (altho it may be insignificant). For example, passing just {@code accountingLine} is better than passing
     *        {@code accountingLine.toString()} or {@code "accounting line:"+accountingLine}. Since the assertion has failed, any
     *        inconsistent side-effects from the conversion are not an issue. A {@code null} reference is treated as the String
     *        {@code "null"}.
     * 
     * @throws AssertionError if {@code isTrue} is false
     */
    public static void assertThat(boolean isTrue, Object detailMessage) {
        if (!isTrue) {
            throw new AssertionError(detailMessage);
        }
    }

    /**
     * Asserts that the {@code isTrue} parameter is true. This method is convenient for formatting the detail message, and as an
     * optimization for detail arguments that are expensive to convert to String. For example, suppose {@code foo} and {@code bar}
     * are two objects with expensive {@link Object#toString} methods. If you use
     * 
     * <pre>
     * AssertionUtils.assertThat(foo.equals(bar), &quot;foo: &quot; + foo + &quot; bar: &quot; + bar);
     * </pre>
     * 
     * then both object's {@code toString} methods will be invoked every time this assertion is done, even though those details are
     * not normally needed because this assertion normally succeeds. You can use this method instead to only do those
     * {@code toString} invocations if this assertion fails:
     * 
     * <pre>
     * AssertionUtils.assertThat(foo.equals(bar), &quot;foo: %s bar: %s&quot;, foo, bar);
     * </pre>
     * 
     * <p/> This method's name is intended to avoid confusion with JUnit asserts (which are for test code, not production code).
     * 
     * @param isTrue whether this assertion succeeds. (Boolean objects are auto-unboxed by JDK 1.5.)
     * 
     * @param detailMessageFormat a {@linkplain String#format format string} to be used in constructing the {@code AssertionError}'s
     *        detail message. The purpose of this message is to capture and communicate details about the assertion failure that
     *        will help a developer diagnose and fix the bug that led the assertion to fail. It's meant to be interpreted in the
     *        context of a full stack trace and with the source code containing the failed assertion. It is <em>not</em> a
     *        user-level error message. Details like {@code "assertion failed"} are redundant, not useful. This detail message
     *        cannot be {@code null}.
     * 
     * @param detailMessageArgs one or more arguments to the format string. Nulls are allowed by some format conversions, such as
     *        {@code "%s"}. (Primitives are auto-boxed by JDK 1.5.) Zero arguments will invoke {@link #assertThat(boolean, Object)}
     *        instead, so the detail message will not be treated as a format string.
     * 
     * @throws AssertionError if {@code isTrue} is false
     * 
     * @throws java.util.IllegalFormatException if {@code detailMessageFormat} contains an illegal syntax, a format specifier that
     *         is incompatible with the given arguments, insufficient arguments given the format string, or other illegal
     *         conditions. For specification of all possible formatting errors, see the Details section of
     *         {@link java.util.Formatter}.
     * 
     * @throws NullPointerException if the {@code detailMessageFormat} is {@code null}
     * 
     * @see String#format
     */
    public static void assertThat(boolean isTrue, String detailMessageFormat, Object... detailMessageArgs) {
        if (!isTrue) {
            throw new AssertionError(String.format(detailMessageFormat, detailMessageArgs));
        }
    }
}
