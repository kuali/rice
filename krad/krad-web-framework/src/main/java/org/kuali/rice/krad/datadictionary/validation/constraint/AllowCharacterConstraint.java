/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.krad.datadictionary.validation.constraint;

import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.uif.UifConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Parent abstract class that allows additional characters to be allowed in child constraint
 * character sets, see AlphaNumericPatternConstraint, among others for example.
 *
 * By setting an allow flag to true you are allowing that character as a valid character in the set.
 * AllowAll set to true will allow all characters which have a flag to be allowed in the set.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class AllowCharacterConstraint extends ValidCharactersPatternConstraint {
    protected boolean allowWhitespace = false;
    protected boolean omitNewline = false;
    protected boolean allowUnderscore = false;
    protected boolean allowPeriod = false;
    protected boolean allowParenthesis = false;
    protected boolean allowDollar = false;
    protected boolean allowForwardSlash = false;
    protected boolean allowDoubleQuote = false;
    protected boolean allowApostrophe = false;
    protected boolean allowComma = false;
    protected boolean allowColon = false;
    protected boolean allowSemiColon = false;
    protected boolean allowQuestionMark = false;
    protected boolean allowExclaimation = false;
    protected boolean allowDash = false;
    protected boolean allowPlus = false;
    protected boolean allowEquals = false;
    protected boolean allowAsterisk = false;
    protected boolean allowAtSign = false;
    protected boolean allowPercent = false;
    protected boolean allowPound = false;
    protected boolean allowAll = false;

    /**
     * @return the allowWhitespace
     */
    public boolean isAllowWhitespace() {
        return this.allowWhitespace;
    }

    /**
     * @param allowWhitespace the allowWhitespace to set
     */
    public void setAllowWhitespace(boolean allowWhitespace) {
        this.allowWhitespace = allowWhitespace;
    }

    /**
     * @return the allowUnderscore
     */
    public boolean isAllowUnderscore() {
        return this.allowUnderscore;
    }

    /**
     * @param allowUnderscore the allowUnderscore to set
     */
    public void setAllowUnderscore(boolean allowUnderscore) {
        this.allowUnderscore = allowUnderscore;
    }

    /**
     * @return the allowPeriod
     */
    public boolean isAllowPeriod() {
        return this.allowPeriod;
    }

    /**
     * @param allowPeriod the allowPeriod to set
     */
    public void setAllowPeriod(boolean allowPeriod) {
        this.allowPeriod = allowPeriod;
    }

    /**
     * @return the allowParenthesis
     */
    public boolean isAllowParenthesis() {
        return this.allowParenthesis;
    }

    /**
     * @param allowParenthesis the allowParenthesis to set
     */
    public void setAllowParenthesis(boolean allowParenthesis) {
        this.allowParenthesis = allowParenthesis;
    }

    /**
     * @return the allowDollar
     */
    public boolean isAllowDollar() {
        return this.allowDollar;
    }

    /**
     * @param allowDollar the allowDollar to set
     */
    public void setAllowDollar(boolean allowDollar) {
        this.allowDollar = allowDollar;
    }

    /**
     * @return the allowForwardSlash
     */
    public boolean isAllowForwardSlash() {
        return this.allowForwardSlash;
    }

    /**
     * @param allowForwardSlash the allowForwardSlash to set
     */
    public void setAllowForwardSlash(boolean allowForwardSlash) {
        this.allowForwardSlash = allowForwardSlash;
    }

    /**
     * @return the allowDoubleQuote
     */
    public boolean isAllowDoubleQuote() {
        return this.allowDoubleQuote;
    }

    /**
     * @param allowDoubleQuote the allowDoubleQuote to set
     */
    public void setAllowDoubleQuote(boolean allowDoubleQuote) {
        this.allowDoubleQuote = allowDoubleQuote;
    }

    /**
     * @return the allowApostrophe
     */
    public boolean isAllowApostrophe() {
        return this.allowApostrophe;
    }

    /**
     * @param allowApostrophe the allowApostrophe to set
     */
    public void setAllowApostrophe(boolean allowApostrophe) {
        this.allowApostrophe = allowApostrophe;
    }

    /**
     * @return the allowComma
     */
    public boolean isAllowComma() {
        return this.allowComma;
    }

    /**
     * @param allowComma the allowComma to set
     */
    public void setAllowComma(boolean allowComma) {
        this.allowComma = allowComma;
    }

    /**
     * @return the allowColon
     */
    public boolean isAllowColon() {
        return this.allowColon;
    }

    /**
     * @param allowColon the allowColon to set
     */
    public void setAllowColon(boolean allowColon) {
        this.allowColon = allowColon;
    }

    /**
     * @return the allowSemiColon
     */
    public boolean isAllowSemiColon() {
        return this.allowSemiColon;
    }

    /**
     * @param allowSemiColon the allowSemiColon to set
     */
    public void setAllowSemiColon(boolean allowSemiColon) {
        this.allowSemiColon = allowSemiColon;
    }

    /**
     * @return the allowQuestionMark
     */
    public boolean isAllowQuestionMark() {
        return this.allowQuestionMark;
    }

    /**
     * @param allowQuestionMark the allowQuestionMark to set
     */
    public void setAllowQuestionMark(boolean allowQuestionMark) {
        this.allowQuestionMark = allowQuestionMark;
    }

    /**
     * @return the allowExclaimation
     */
    public boolean isAllowExclaimation() {
        return this.allowExclaimation;
    }

    /**
     * @param allowExclaimation the allowExclaimation to set
     */
    public void setAllowExclaimation(boolean allowExclaimation) {
        this.allowExclaimation = allowExclaimation;
    }

    /**
     * @return the allowDash
     */
    public boolean isAllowDash() {
        return this.allowDash;
    }

    /**
     * @param allowDash the allowDash to set
     */
    public void setAllowDash(boolean allowDash) {
        this.allowDash = allowDash;
    }

    /**
     * @return the allowPlus
     */
    public boolean isAllowPlus() {
        return this.allowPlus;
    }

    /**
     * @param allowPlus the allowPlus to set
     */
    public void setAllowPlus(boolean allowPlus) {
        this.allowPlus = allowPlus;
    }

    /**
     * @return the allowEquals
     */
    public boolean isAllowEquals() {
        return this.allowEquals;
    }

    /**
     * @param allowEquals the allowEquals to set
     */
    public void setAllowEquals(boolean allowEquals) {
        this.allowEquals = allowEquals;
    }

    /**
     * @return the allowAsterisk
     */
    public boolean isAllowAsterisk() {
        return this.allowAsterisk;
    }

    /**
     * @param allowAsterisk the allowAsterisk to set
     */
    public void setAllowAsterisk(boolean allowAsterisk) {
        this.allowAsterisk = allowAsterisk;
    }

    /**
     * @return the allowAtSign
     */
    public boolean isAllowAtSign() {
        return this.allowAtSign;
    }

    /**
     * @param allowAtSign the allowAtSign to set
     */
    public void setAllowAtSign(boolean allowAtSign) {
        this.allowAtSign = allowAtSign;
    }

    /**
     * @return the allowPercent
     */
    public boolean isAllowPercent() {
        return this.allowPercent;
    }

    /**
     * @param allowPercent the allowPercent to set
     */
    public void setAllowPercent(boolean allowPercent) {
        this.allowPercent = allowPercent;
    }

    /**
     * @return the allowPound
     */
    public boolean isAllowPound() {
        return this.allowPound;
    }

    /**
     * @param allowPound the allowPound to set
     */
    public void setAllowPound(boolean allowPound) {
        this.allowPound = allowPound;
    }

    /**
     * If true, this constraint will allow all symbols that have flags for them. Equivalent to
     * setting each flag to true separately.
     *
     * @return the allowAll
     */
    public boolean isAllowAll() {
        return this.allowAll;
    }

    /**
     * @param allowAll the allowAll to set
     */
    public void setAllowAll(boolean allowAll) {
        this.allowAll = allowAll;
    }

    public boolean isOmitNewline() {
        return omitNewline;
    }

    /**
     * When set to true, omit new line characters from the set of valid characters.  This flag
     * will only have an effect if the allowWhitespace flag is true, otherwise all whitespace
     * including new lines characters are omitted.
     *
     * @param omitNewline
     */
    public void setOmitNewline(boolean omitNewline) {
        this.omitNewline = omitNewline;
    }

    /**
     * This method returns the allowed set of characters allowed by this constraint, based on the
     * flags set. This string is intended to be placed within the or set of a regex, ie between the
     * [ ] symbols
     *
     * @return
     */
    protected String getAllowedCharacterRegex() {
        StringBuilder regexString = new StringBuilder("");
        if (allowWhitespace || allowAll) {
            regexString.append("\\t\\v\\040");
            if (!omitNewline) {
                regexString.append("\\f\\r\\n");
            }
        }
        if (allowUnderscore || allowAll) {
            regexString.append("_");
        }
        if (allowPeriod || allowAll) {
            regexString.append(".");
        }
        if (allowParenthesis || allowAll) {
            regexString.append("(");
            regexString.append(")");
        }
        if (allowDollar || allowAll) {
            regexString.append("$");
        }
        if (allowForwardSlash || allowAll) {
            regexString.append("/");
        }
        if (allowDoubleQuote || allowAll) {
            regexString.append("\\\"");
        }
        if (allowApostrophe || allowAll) {
            regexString.append("'");
        }
        if (allowComma || allowAll) {
            regexString.append(",");
        }
        if (allowColon || allowAll) {
            regexString.append(":");
        }
        if (allowSemiColon || allowAll) {
            regexString.append(";");
        }
        if (allowQuestionMark || allowAll) {
            regexString.append("?");
        }
        if (allowExclaimation || allowAll) {
            regexString.append("!");
        }
        if (allowDash || allowAll) {
            regexString.append("\\-");
        }
        if (allowPlus || allowAll) {
            regexString.append("+");
        }
        if (allowEquals || allowAll) {
            regexString.append("=");
        }
        if (allowAsterisk || allowAll) {
            regexString.append("*");
        }
        if (allowAtSign || allowAll) {
            regexString.append("@");
        }
        if (allowPercent || allowAll) {
            regexString.append("%");
        }
        if (allowPound || allowAll) {
            regexString.append("#");
        }

        return regexString.toString();
    }

    /**
     * Generates a comma separated string of the allowed set of characters, for the {0} parameter to
     * be used within its validation message
     *
     * @return the validationMessageParams
     */
    public List<String> getValidationMessageParams() {
        if (validationMessageParams == null) {
            validationMessageParams = new ArrayList<String>();
            ConfigurationService configService = KRADServiceLocator.getKualiConfigurationService();
            StringBuilder paramString = new StringBuilder("");

            if (allowWhitespace || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "whitespace"));
                if (!omitNewline) {
                    paramString.append(", " + configService.getPropertyValueAsString(
                            UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "newline"));
                }
            }
            if (allowUnderscore || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "underscore"));
            }
            if (allowPeriod || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "period"));
            }
            if (allowParenthesis || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "parenthesis"));
            }
            if (allowDollar || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "dollar"));
            }
            if (allowForwardSlash || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "forwardSlash"));
            }
            if (allowDoubleQuote || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "doubleQuote"));
            }
            if (allowApostrophe || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "apostrophe"));
            }
            if (allowComma || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "comma"));
            }
            if (allowColon || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "colon"));
            }
            if (allowSemiColon || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "semiColon"));
            }
            if (allowQuestionMark || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "questionMark"));
            }
            if (allowExclaimation || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "exclaimation"));
            }
            if (allowDash || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "dash"));
            }
            if (allowPlus || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "plus"));
            }
            if (allowEquals || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "equals"));
            }
            if (allowAsterisk || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "asterisk"));
            }
            if (allowAtSign || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "atSign"));
            }
            if (allowPercent || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "percent"));
            }
            if (allowPound || allowAll) {
                paramString.append(", " + configService.getPropertyValueAsString(
                        UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "pound"));
            }

            validationMessageParams.add(paramString.toString());
        }
        return this.validationMessageParams;
    }

}
