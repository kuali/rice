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
/**
 * Copyright 2006 Tim Down.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
/**
 * log4javascript
 *
 * log4javascript is a logging framework for JavaScript based on log4j
 * for Java. This file contains all core log4javascript code and is the only
 * file required to use log4javascript.
 *
 * Author: Tim Down <tim@timdown.co.uk>
 * Version: 1.2
 * Last modified: 21/6/2006
 * Website: http://www.timdown.co.uk/log4javascript
 */

/* ------------------------------------------------------------------------- */

// Array-related stuff

// Next two methods are solely for IE5, which is missing them
if (!Array.prototype.push) {
	Array.prototype.push = function() {
        for (var i = 0; i < arguments.length; i++){
            this[this.length] = arguments[i];
        }
        return this.length;
	};
}

if (!Array.prototype.shift) {
	Array.prototype.shift = function() {
		if (this.length > 0) {
			var firstItem = this[0];
			for (var i = 0; i < this.length - 1; i++) {
				this[i] = this[i + 1];
			}
			this.length = this.length - 1;
			return firstItem;
		}
	};
}

function Array_clone(arr) {
	var clonedArray = [];
	for (var i = 0; i < arr.length; i++) {
		clonedArray[i] = arr[i];
	}
	return clonedArray;
}

/* ------------------------------------------------------------------------- */

// Date-related stuff
Date.prototype.getDifference = function(date) {
	return (this.getTime() - date.getTime());
};

var SimpleDateFormat = (function() {
	var regex = /('[^']*')|(G+|y+|M+|w+|W+|D+|d+|F+|E+|a+|H+|k+|K+|h+|m+|s+|S+|Z+)|([a-zA-Z]+)|([^a-zA-Z']+)/;
	var monthNames = ["January", "February", "March", "April", "May", "June",
		"July", "August", "September", "October", "November", "December"];
	var dayNames = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
	var TEXT2 = 0, TEXT3 = 1, NUMBER = 2, YEAR = 3, MONTH = 4, TIMEZONE = 5;
	var types = {
		G : TEXT2,
		y : YEAR,
		Y : YEAR,
		M : MONTH,
		w : NUMBER,
		W : NUMBER,
		D : NUMBER,
		d : NUMBER,
		F : NUMBER,
		E : TEXT3,
		a : TEXT2,
		H : NUMBER,
		k : NUMBER,
		K : NUMBER,
		h : NUMBER,
		m : NUMBER,
		s : NUMBER,
		S : NUMBER,
		Z : TIMEZONE
	};
	var ONE_DAY = 24 * 60 * 60 * 1000;
	var ONE_WEEK = 7 * ONE_DAY;
	var DEFAULT_MINIMAL_DAYS_IN_FIRST_WEEK = 1;

	Date.prototype.isBefore = function(d) {
		return this.getTime() < d.getTime();
	};

	Date.prototype.getWeekInYear = function(minimalDaysInFirstWeek) {
		var previousSunday = new Date(this.getTime() - this.getDay() * ONE_DAY);
		var startOfYear = new Date(this.getFullYear(), 0, 1);
		var numberOfSundays = previousSunday.isBefore(startOfYear) ? 
			0 : 1 + Math.floor((previousSunday.getTime() - startOfYear.getTime()) / ONE_WEEK);
		var numberOfDaysInFirstWeek =  7 - startOfYear.getDay();
		var weekInYear = numberOfSundays;
		if (numberOfDaysInFirstWeek >= minimalDaysInFirstWeek) {
			weekInYear++;
		}
		return weekInYear;
	};

	Date.prototype.getWeekInMonth = function(minimalDaysInFirstWeek) {
		var previousSunday = new Date(this.getTime() - this.getDay() * ONE_DAY);
		var startOfMonth = new Date(this.getFullYear(), this.getMonth(), 1);
		var numberOfSundays = previousSunday.isBefore(startOfMonth) ? 
			0 : 1 + Math.floor((previousSunday.getTime() - startOfMonth.getTime()) / ONE_WEEK);
		var numberOfDaysInFirstWeek =  7 - startOfMonth.getDay();
		var weekInMonth = numberOfSundays;
		if (numberOfDaysInFirstWeek >= minimalDaysInFirstWeek) {
			weekInMonth++;
		}
		return weekInMonth;
	};

	Date.prototype.getDayInYear = function() {
		var startOfYear = new Date(this.getFullYear(), 0, 1);
		return 1 + Math.floor((this.getTime() - startOfYear.getTime()) / ONE_DAY);
	};

	/* --------------------------------------------------------------------- */

	SimpleDateFormat = function(formatString) {
		this.formatString = formatString;
	};

	/**
	 * Sets the minimum number of days in a week in order for that week to
	 * be considered as belonging to a particular month or year
	 */
	SimpleDateFormat.prototype.setMinimalDaysInFirstWeek = function(days) {
		this.minimalDaysInFirstWeek = days;
	};

	SimpleDateFormat.prototype.getMinimalDaysInFirstWeek = function(days) {
		return (typeof this.minimalDaysInFirstWeek == "undefined")
				? DEFAULT_MINIMAL_DAYS_IN_FIRST_WEEK : this.minimalDaysInFirstWeek;
	};

	SimpleDateFormat.prototype.format = function(date) {
		var formattedString = "";
		var result;

		var padWithZeroes = function(str, len) {
			while (str.length < len) {
				str = "0" + str;
			}
			return str;
		};

		var formatText = function(data, numberOfLetters, minLength) {
			return (numberOfLetters >= 4) ? data : data.substr(0, Math.max(minLength, numberOfLetters));
		};

		var formatNumber = function(data, numberOfLetters) {
			var dataString = "" + data;
			// Pad with 0s as necessary
			return padWithZeroes(dataString, numberOfLetters);
		};

		var searchString = this.formatString;
		while ((result = regex.exec(searchString))) {
			var matchedString = result[0];
			var quotedString = result[1];
			var patternLetters = result[2];
			var otherLetters = result[3];
			var otherCharacters = result[4];

			// If the pattern matched is quoted string, output the text between the quotes
			if (quotedString) {
				if (quotedString == "''") {
					formattedString += "'";
				} else {
					formattedString += quotedString.substring(1, quotedString.length - 1);
				}
			} else if (otherLetters) {
				// Swallow non-pattern letters by doing nothing here
			} else if (otherCharacters) {
				// Simply output other characters
				formattedString += otherCharacters;
			} else if (patternLetters) {
				// Replace pattern letters
				var patternLetter = patternLetters.charAt(0);
				var numberOfLetters = patternLetters.length;
				var rawData = "";
				switch (patternLetter) {
					case "G":
						rawData = "AD";
						break;
					case "y":
						rawData = date.getFullYear();
						break;
					case "M":
						rawData = date.getMonth();
						break;
					case "w":
						rawData = date.getWeekInYear(this.minimalDaysInFirstWeek);
						break;
					case "W":
						rawData = date.getWeekInMonth(this.minimalDaysInFirstWeek);
						break;
					case "D":
						rawData = date.getDayInYear();
						break;
					case "d":
						rawData = date.getDate();
						break;
					case "F":
						rawData = 1 + Math.floor((date.getDate() - 1) / 7);
						break;
					case "E":
						rawData = dayNames[date.getDay()];
						break;
					case "a":
						rawData = (date.getHours() >= 12) ? "PM" : "AM";
						break;
					case "H":
						rawData = date.getHours();
						break;
					case "k":
						rawData = 1 + date.getHours();
						break;
					case "K":
						rawData = date.getHours() % 12;
						break;
					case "h":
						rawData = 1 + (date.getHours() % 12);
						break;
					case "m":
						rawData = date.getMinutes();
						break;
					case "s":
						rawData = date.getSeconds();
						break;
					case "S":
						rawData = date.getMilliseconds();
						break;
					case "Z":
						rawData = date.getTimezoneOffset(); // This is returns the number of minutes since GMT was this time.
						break;
				}
				// Format the raw data depending on the type
				switch (types[patternLetter]) {
					case TEXT2:
						formattedString += formatText(rawData, numberOfLetters, 2);
						break;
					case TEXT3:
						formattedString += formatText(rawData, numberOfLetters, 3);
						break;
					case NUMBER:
						formattedString += formatNumber(rawData, numberOfLetters);
						break;
					case YEAR:
						if (numberOfLetters <= 2) {
							// Output a 2-digit year
							var dataString = "" + rawData;
							formattedString += dataString.substr(2, 2);
						} else {
							formattedString += formatNumber(rawData, numberOfLetters);
						}
						break;
					case MONTH:
						if (numberOfLetters >= 3) {
							formattedString += formatText(monthNames[rawData], numberOfLetters, numberOfLetters);
						} else {
							// NB. Months returned by getMonth are zero-based
							formattedString += formatNumber(rawData + 1, numberOfLetters);
						}
						break;
					case TIMEZONE:
						var isPositive = (rawData > 0);
						// The following line looks like a mistake but isn't
						// because of the way getTimezoneOffset measures.
						var prefix = isPositive ? "-" : "+";
						var absData = Math.abs(rawData);

						// Hours
						var hours = "" + Math.floor(absData / 60);
						hours = padWithZeroes(hours, 2);
						// Minutes
						var minutes = "" + (absData % 60);
						minutes = padWithZeroes(minutes, 2);

						formattedString += prefix + hours + minutes;
						break;
				}
			}
			searchString = searchString.substr(result.index + result[0].length);
		}
		return formattedString;
	};
	return SimpleDateFormat;
})();

/* ------------------------------------------------------------------------- */

var log4javascript = (function() {
	var applicationStartDate = new Date();
	var emptyFunction = function() {};
	var newLine = "\n";

	// Create logging object; this will be assigned properties and returned
	var log4javascript = {};
	log4javascript.version = "1.2";

	// Returns a nicely formatted representation of an error
	function getExceptionStringRep(ex) {
		if (ex) {
			var exStr = "";
			if (ex.message) {
				exStr += ex.message;
			} else if (ex.description) {
				exStr += ex.description;
			}
			if (ex.lineNumber) {
				exStr += " on line number " + ex.lineNumber;
			}
			if (ex.fileName) {
				exStr += " in file " + ex.fileName;
			}
			return exStr;
		}
		return null;
	}
	
	function extractBooleanFromParam(param, defaultValue) {
		if (typeof param == "undefined") {
			return defaultValue;
		} else {
			return Boolean(param);
		}
	}

	function extractStringFromParam(param, defaultValue) {
		if (typeof param == "undefined") {
			return defaultValue;
		} else {
			return String(param);
		}
	}

	function extractIntFromParam(param, defaultValue) {
		if (typeof param == "undefined") {
			return defaultValue;
		} else {
			try {
				return parseInt(param, 10);
			} catch (ex) {
				logLog.warn("Invalid int param " + param, ex);
				return defaultValue;
			}
		}
	}

	function extractFunctionFromParam(param, defaultValue) {
		if (typeof param == "function") {
			return param;
		} else {
			return defaultValue;
		}
	}

	/* --------------------------------------------------------------------- */

	// Simple logging for log4javascript itself

	var logLog = {
		quietMode: false,

		setQuietMode: function(quietMode) {
			this.quietMode = Boolean(quietMode);
		},

		numberOfErrors: 0,

		alertAllErrors: false,

		setAlertAllErrors: function(alertAllErrors) {
			this.alertAllErrors = alertAllErrors;
		},

		debug: function(message, exception) {
		},

		warn: function(message, exception) {
		},

		error: function(message, exception) {
			if (++this.numberOfErrors == 1 || this.alertAllErrors) {
				if (!this.quietMode) {
					var alertMessage = "log4javascript error: " + message;
					if (exception) {
						alertMessage += "\n\nOriginal error: " + getExceptionStringRep(exception);
					}
					alert(alertMessage);
				}
			}
		}
	};
	log4javascript.logLog = logLog;
	
	/* --------------------------------------------------------------------- */

	var errorListeners = [];

	log4javascript.addErrorListener = function(listener) {
		if (typeof listener == "function") {
			errorListeners.push(listener);
		} else {
			handleError("addErrorListener: listener supplied was not a function");
		}
	};

	log4javascript.removeErrorListener = function(listener) {
		var newErrorListeners = [];
		for (var i = 0; i < errorListeners.length; i++) {
			if (errorListeners[i] != listener) {
				newErrorListeners.push(errorListeners[i]);
			}
		}
		errorListeners = newErrorListeners;
	};

	function handleError(message, exception) {
		logLog.error(message, exception);
		for (var i = 0; i < errorListeners.length; i++) {
			errorListeners[i](message, exception);
		}
	}

	/* --------------------------------------------------------------------- */

	var enabled = (typeof log4javascript_disabled != "undefined")
		&& log4javascript_disabled ? false : true;

	log4javascript.setEnabled = function(enable) {
		enabled = Boolean(enable);
	};

	log4javascript.isEnabled = function() {
		return enabled;
	};

	/* --------------------------------------------------------------------- */

	function Logger(name) {
		this.name = name;
		var appenders = [];
		var loggerLevel = log4javascript.Level.DEBUG;

		// Create methods that use the appenders variable in this scope
		this.addAppender = function(appender) {
			if (appender instanceof log4javascript.Appender) {
				appenders.push(appender);
			} else {
				handleError("Logger.addAppender: appender supplied is not a subclass of Appender");
			}
		};

		this.log = function(level, message, exception) {
			if (level.isGreaterOrEqual(loggerLevel)) {
				var loggingEvent = new log4javascript.LoggingEvent(
					this, new Date(), level, message, exception);
				for (var i = 0; i < appenders.length; i++) {
					appenders[i].doAppend(loggingEvent);
				}
			}
		};

		this.setLevel = function(level) {
			loggerLevel = level;
		};

		this.getLevel = function() {
			return loggerLevel;
		};
	}

	Logger.prototype = {
		trace: function(message, exception) {
			this.log(log4javascript.Level.TRACE, message, exception);
		},

		debug: function(message, exception) {
			this.log(log4javascript.Level.DEBUG, message, exception);
		},

		info: function(message, exception) {
			this.log(log4javascript.Level.INFO, message, exception);
		},

		warn: function(message, exception) {
			this.log(log4javascript.Level.WARN, message, exception);
		},

		error: function(message, exception) {
			this.log(log4javascript.Level.ERROR, message, exception);
		},

		fatal: function(message, exception) {
			this.log(log4javascript.Level.FATAL, message, exception);
		}
	};

	/* --------------------------------------------------------------------- */

	// Hashtable of loggers keyed by logger name
	var loggers = {};

	log4javascript.getLogger = function(loggerName) {
		// Use default logger if loggerName is not specified or invalid
		if (!(typeof loggerName == "string")) {
			loggerName = "[root]";
		}

		// Create the logger for this name if it doesn't already exist
		if (!loggers[loggerName]) {
			loggers[loggerName] = new Logger(loggerName);
		}
		return loggers[loggerName];
	};

	var defaultLogger = null;
	log4javascript.getDefaultLogger = function() {
		if (!defaultLogger) {
			defaultLogger = log4javascript.getLogger("[default]");
			var a = new log4javascript.PopUpAppender();
			defaultLogger.addAppender(a);
		}
		return defaultLogger;
	};

	var nullLogger = null;
	log4javascript.getNullLogger = function() {
		if (!nullLogger) {
			nullLogger = log4javascript.getLogger("[null]");
		}
		return nullLogger;
	};

	/* --------------------------------------------------------------------- */

	log4javascript.Level = function(level, name) {
		this.level = level;
		this.name = name;
	};

	log4javascript.Level.prototype = {
		toString: function() {
			return this.name;
		},
		isGreaterOrEqual: function(level) {
			return this.level >= level.level;
		}
	};

	log4javascript.Level.ALL = new log4javascript.Level(Number.MIN_VALUE, "ALL");
	log4javascript.Level.TRACE = new log4javascript.Level(10000, "TRACE");
	log4javascript.Level.DEBUG = new log4javascript.Level(20000, "DEBUG");
	log4javascript.Level.INFO = new log4javascript.Level(30000, "INFO");
	log4javascript.Level.WARN = new log4javascript.Level(40000, "WARN");
	log4javascript.Level.ERROR = new log4javascript.Level(50000, "ERROR");
	log4javascript.Level.FATAL = new log4javascript.Level(60000, "FATAL");
	log4javascript.Level.OFF = new log4javascript.Level(Number.MAX_VALUE, "OFF");

	/* --------------------------------------------------------------------- */

	log4javascript.LoggingEvent = function(logger, timeStamp, level, message,
			exception) {
		this.logger = logger;
		this.timeStamp = timeStamp;
		this.timeStampInSeconds = Math.floor(timeStamp.getTime() / 1000);
		this.level = level;
		this.message = message;
		this.exception = exception;
	};

	/* --------------------------------------------------------------------- */

	// Layout "abstract class"
	log4javascript.Layout = function() {
	};

	log4javascript.Layout.prototype = {
		defaults: {
			loggerKey: "logger",
			timeStampKey: "timestamp",
			levelKey: "level",
			messageKey: "message",
			exceptionKey: "exception",
			urlKey: "url"
		},
		loggerKey: "logger",
		timeStampKey: "timestamp",
		levelKey: "level",
		messageKey: "message",
		exceptionKey: "exception",
		urlKey: "url",
		batchHeader: "",
		batchFooter: "",
		batchSeparator: "",

		format: function(loggingEvent) {
			handleError("Layout.format: layout supplied has no format() method");
		},

		getContentType: function() {
			return "text/plain";
		},

		allowBatching: function() {
			return true;
		},

		getDataValues: function(loggingEvent) {
			var dataValues = [
				[this.loggerKey, loggingEvent.logger.name],
				[this.timeStampKey, loggingEvent.timeStampInSeconds],
				[this.levelKey, loggingEvent.level.name],
				[this.urlKey, window.location.href],
				[this.messageKey, loggingEvent.message]
			];
			if (loggingEvent.exception) {
				dataValues.push([this.exceptionKey, getExceptionStringRep(loggingEvent.exception)]);
			}
			return dataValues;
		},

		setKeys: function(loggerKey, timeStampKey, levelKey, messageKey,
				exceptionKey, urlKey) {
			this.loggerKey = extractStringFromParam(loggerKey, this.defaults.loggerKey);
			this.timeStampKey = extractStringFromParam(timeStampKey, this.defaults.timeStampKey);
			this.levelKey = extractStringFromParam(levelKey, this.defaults.levelKey);
			this.messageKey = extractStringFromParam(messageKey, this.defaults.messageKey);
			this.exceptionKey = extractStringFromParam(exceptionKey, this.defaults.exceptionKey);
			this.urlKey = extractStringFromParam(urlKey, this.defaults.urlKey);
		}
	};

	/* --------------------------------------------------------------------- */

	// SimpleLayout 
	log4javascript.SimpleLayout = function() {
	};

	log4javascript.SimpleLayout.prototype = new log4javascript.Layout();

	log4javascript.SimpleLayout.prototype.format = function(loggingEvent) {
		return loggingEvent.level.name + " - " + loggingEvent.message;
	};

	/* --------------------------------------------------------------------- */

	// XmlLayout 
	log4javascript.XmlLayout = function() {
	};

	log4javascript.XmlLayout.prototype = new log4javascript.Layout();

	log4javascript.XmlLayout.prototype.getContentType = function() {
		return "text/xml";
	};

	log4javascript.XmlLayout.prototype.escapeCdata = function(str) {
		return str.replace(/\]\]>/, "]]>]]&gt;<![CDATA[");
	};

	log4javascript.XmlLayout.prototype.format = function(loggingEvent) {
		var str = "<log4javascript:event logger=\"" + loggingEvent.logger.name
			+ "\" timestamp=\"" + loggingEvent.timeStampInSeconds
			+ "\" level=\"" + loggingEvent.level.name
			+ "\">" + newLine + "<log4javascript:message><![CDATA["
			+ this.escapeCdata(loggingEvent.message)
			+ "]]></log4javascript:message>" + newLine;
		if (loggingEvent.exception) {
			str += "<log4javascript:exception><![CDATA["
				+ getExceptionStringRep(loggingEvent.exception)
				+ "]]></log4javascript:exception>" + newLine;
		}
		str += "</log4javascript:event>" + newLine + newLine;
		return str;
	};

	/* --------------------------------------------------------------------- */

	// JsonLayout 
	log4javascript.JsonLayout = function(readable, loggerKey, timeStampKey,
			levelKey, messageKey, exceptionKey, urlKey) {
		this.readable = Boolean(readable);
		this.batchHeader = this.readable ? "[" + newLine : "[";
		this.batchFooter = this.readable ? "]" + newLine : "]";
		this.batchSeparator = this.readable ? "," + newLine : ",";
		this.setKeys(loggerKey, timeStampKey, levelKey, messageKey,
			exceptionKey, urlKey);
		this.propertySeparator = this.readable ? ", " : ",";
		this.colon = this.readable ? ": " : ":";
	};

	log4javascript.JsonLayout.prototype = new log4javascript.Layout();

	log4javascript.JsonLayout.prototype.format = function(loggingEvent) {
		var dataValues = this.getDataValues(loggingEvent);
		var str = "{";
		if (this.readable) {
			str += newLine;
		}
		for (var i = 0; i < dataValues.length; i++) {
			if (this.readable) {
				str += "\t";
			}
			// All the data values are either strings or numbers, so
			// no need for anything complicated
			var val = (typeof dataValues[i][1] == "string")
				? "\"" + dataValues[i][1] + "\"" : dataValues[i][1];
			str += "\"" + dataValues[i][0] + "\"" + this.colon + val;
			if (i < dataValues.length - 1) {
				str += this.propertySeparator;
			}
			if (this.readable) {
				str += newLine;
			}
		}
		str += "}";
		if (this.readable) {
			str += newLine;
		}
		return str;
	};

	/* --------------------------------------------------------------------- */

	// HttpPostDataLayout 
	log4javascript.HttpPostDataLayout = function(loggerKey, timeStampKey,
			levelKey, messageKey, exceptionKey, urlKey) {
		this.setKeys(loggerKey, timeStampKey, levelKey, messageKey,
			exceptionKey, urlKey);
	};

	log4javascript.HttpPostDataLayout.prototype = new log4javascript.Layout();

	// Disable batching
	log4javascript.HttpPostDataLayout.prototype.allowBatching = function() {
		return false;
	};

	log4javascript.HttpPostDataLayout.prototype.format = function(loggingEvent) {
		var dataValues = this.getDataValues(loggingEvent);
		var queryBits = [];
		for (var i = 0; i < dataValues.length; i++) {
			queryBits.push(escape(dataValues[i][0]) + "=" + escape(dataValues[i][1]));
		}
		return queryBits.join("&");
	};

	/* --------------------------------------------------------------------- */

	// PatternLayout 
	log4javascript.PatternLayout = function(pattern) {
		if (pattern) {
			this.pattern = pattern;
		} else {
			this.pattern = log4javascript.PatternLayout.DEFAULT_CONVERSION_PATTERN;
		}
	};

	log4javascript.PatternLayout.TTCC_CONVERSION_PATTERN = "%r %p %c - %m%n";
	log4javascript.PatternLayout.DEFAULT_CONVERSION_PATTERN = "%m%n";
	log4javascript.PatternLayout.ISO8601_DATEFORMAT = "yyyy-MM-dd HH:mm:ss,SSS";
	log4javascript.PatternLayout.DATETIME_DATEFORMAT = "dd MMM YYYY HH:mm:ss,SSS";
	log4javascript.PatternLayout.ABSOLUTETIME_DATEFORMAT = "HH:mm:ss,SSS";

	log4javascript.PatternLayout.prototype = new log4javascript.Layout();

	log4javascript.PatternLayout.prototype.format = function(loggingEvent) {
		var regex = /%(-?[0-9]+)?(\.?[0-9]+)?([cdmnpr%])(\{([^\}]+)\})?|([^%]+)/;
		var formattedString = "";
		var result;
		var searchString = this.pattern;

		// Cannot use regex global flag since it doesn't work in IE5
		while ((result = regex.exec(searchString))) {
			var matchedString = result[0];
			var padding = result[1];
			var truncation = result[2];
			var conversionCharacter = result[3];
			var specifier = result[5];
			var text = result[6];

			// Check if the pattern matched was just normal text
			if (text) {
				formattedString += "" + text;
			} else {
				// Create a raw replacement string based on the conversion
				// character and specifier
				var replacement = "";
				switch(conversionCharacter) {
					case "c":
						var loggerName = loggingEvent.logger.name;
						if (specifier) {
							var precision = parseInt(specifier, 10);
							var loggerNameBits = loggingEvent.logger.name.split(".");
							if (precision >= loggerNameBits.length) {
								replacement = loggerName;
							} else {
								replacement = loggerNameBits.slice(loggerNameBits.length - precision).join(".");
							}
						} else {
							replacement = loggerName;
						}
						break;
					case "d":
						var dateFormat = log4javascript.PatternLayout.ISO8601_DATEFORMAT;
						if (specifier) {
							dateFormat = specifier;
							// Pick up special cases
							if (dateFormat == "ISO8601") {
								dateFormat = log4javascript.PatternLayout.ISO8601_DATEFORMAT;
							} else if (dateFormat == "ABSOLUTE") {
								dateFormat = log4javascript.PatternLayout.ABSOLUTETIME_DATEFORMAT;
							} else if (dateFormat == "DATE") {
								dateFormat = log4javascript.PatternLayout.DATETIME_DATEFORMAT;
							}
						}
						// Format the date
						replacement = (new SimpleDateFormat(dateFormat)).format(loggingEvent.timeStamp);
						break;
					case "m":
						replacement = loggingEvent.message;
						break;
					case "n":
						replacement = "\n";
						break;
					case "p":
						replacement = loggingEvent.level.name;
						break;
					case "r":
						replacement = "" + loggingEvent.timeStamp.getDifference(applicationStartDate);
						break;
					case "%":
						replacement = "%";
						break;
					default:
						replacement = matchedString;
						break;
				}
				// Format the replacement according to any padding or
				// truncation specified

				var len;

				// First, truncation
				if (truncation) {
					len = parseInt(truncation.substr(1), 10);
					replacement = replacement.substring(0, len);
				}
				// Next, padding
				if (padding) {
					if (padding.charAt(0) == "-") {
						len = parseInt(padding.substr(1), 10);
						// Right pad with spaces
						while (replacement.length < len) {
							replacement += " ";
						}
					} else {
						len = parseInt(padding, 10);
						// Left pad with spaces
						while (replacement.length < len) {
							replacement = " " + replacement;
						}
					}
				}
				formattedString += replacement;
			}
			searchString = searchString.substr(result.index + result[0].length);
		}
		return formattedString;
	};

	/* --------------------------------------------------------------------- */

	// Appender "abstract class"
	log4javascript.Appender = function() {};

	// Performs threshold checks before delegating actual logging to the
	// subclass's specific append method.
	log4javascript.Appender.prototype = {
		layout: new log4javascript.PatternLayout(),
		threshold: log4javascript.Level.ALL,

		doAppend: function(loggingEvent) {
			if (enabled && loggingEvent.level.level >= this.threshold.level) {
				this.append(loggingEvent);
			}
		},

		append: function(loggingEvent) {},

		setLayout: function(layout) {
			if (layout instanceof log4javascript.Layout) {
				this.layout = layout;
			} else {
				log4javascript.handleError("Appender.setLayout: layout supplied to " + this.toString()
					+ " is not a subclass of Layout");
			}
		},

		getLayout: function() {
			return this.layout;
		},
	
		setThreshold: function(threshold) {
			if (threshold instanceof log4javascript.Level) {
				this.threshold = threshold;
			} else {
				log4javascript.handleError("Appender.setThreshold: threshold supplied to " + this.toString()
					+ " is not a subclass of Level");
			}
		},
	
		getThreshold: function() {
			return this.threshold;
		},
		
		toString: function() {
			return "[Base Appender]";
		}
	};
	var Appender = log4javascript.Appender;

	/* --------------------------------------------------------------------- */

	// AlertAppender
	log4javascript.AlertAppender = function(layout) {
		if (layout) {
			this.setLayout(layout);
		}
	};

	log4javascript.AlertAppender.prototype = new Appender();

	log4javascript.AlertAppender.prototype.layout = new log4javascript.SimpleLayout();

	log4javascript.AlertAppender.prototype.init = function() {};

	log4javascript.AlertAppender.prototype.append = function(loggingEvent) {
		var formattedMessage = this.getLayout().format(loggingEvent);
		alert(formattedMessage);
	};

	log4javascript.AlertAppender.prototype.toString = function() {
		return "[AlertAppender]";
	};

	/* --------------------------------------------------------------------- */

	// AjaxAppender
	log4javascript.AjaxAppender = function(url, layout, timed, waitForResponse,
			batchSize, timerInterval, requestSuccessCallback, failCallback) {
		var appender = this;
		var isSupported = true;
		if (!url) {
			handleError("AjaxAppender: URL must be specified in constructor");
			isSupported = false;
		}

		timed = extractBooleanFromParam(timed, this.defaults.timed);
		waitForResponse = extractBooleanFromParam(waitForResponse, this.defaults.waitForResponse);
		batchSize = extractIntFromParam(batchSize, this.defaults.batchSize);
		timerInterval = extractIntFromParam(timerInterval, this.defaults.timerInterval);
		requestSuccessCallback = extractFunctionFromParam(requestSuccessCallback, this.defaults.requestSuccessCallback);
		failCallback = extractFunctionFromParam(failCallback, this.defaults.failCallback);

		var queuedLoggingEvents = [];
		var queuedRequests = [];
		var sending = false;
		var appender = this;
		var initialized = false;

		// Configuration methods. The function scope is used to prevent
		// direct alteration to the appender configuration properties.
		function checkCanConfigure(configOptionName) {
			if (initialized) {
				handleError("AjaxAppender: configuration option '" + configOptionName + "' may not be set after the appender has been initialized");
				return false;
			}
			return true;
		}

		this.setLayout = function(layout) {
			if (checkCanConfigure("layout")) {
				this.layout = layout;
			}
		};

		if (layout) {
			this.setLayout(layout);
		}

		this.isTimed = function() { return timed; };
		this.setTimed = function(_timed) {
			if (checkCanConfigure("timed")) {
				timed = Boolean(_timed);
			}
		};

		this.getTimerInterval = function() { return timerInterval; };
		this.setTimerInterval = function(_timerInterval) {
			if (checkCanConfigure("timerInterval")) {
				timerInterval = extractIntFromParam(_timerInterval, timerInterval);
			}
		};

		this.isWaitForResponse = function() { return waitForResponse; };
		this.setWaitForResponse = function(_waitForResponse) {
			if (checkCanConfigure("waitForResponse")) {
				waitForResponse = Boolean(_waitForResponse);
			}
		};

		this.getBatchSize = function() { return batchSize; };
		this.setBatchSize = function(_batchSize) {
			if (checkCanConfigure("batchSize")) {
				batchSize = extractIntFromParam(_batchSize, batchSize);
			}
		};

		this.setRequestSuccessCallback = function(_requestSuccessCallback) {
			requestSuccessCallback = extractFunctionFromParam(_requestSuccessCallback, requestSuccessCallback);
		};

		this.setFailCallback = function(_failCallback) {
			failCallback = extractFunctionFromParam(_failCallback, failCallback);
		};

		// Internal functions
		function sendAll() {
			if (isSupported && enabled) {
				sending = true;
				var currentRequest;
				if (waitForResponse) {
					// Send the first request then use this function as the callback once
					// the response comes back
					if (queuedRequests.length > 0) {
						currentRequest = queuedRequests.shift();
						sendRequest(currentRequest, sendAll);
					} else {
						sending = false;
						if (timed) {
							scheduleSending();
						}
					}
				} else {
					// Rattle off all the requests without waiting to see the response
					while ((currentRequest = queuedRequests.shift())) {
						sendRequest(currentRequest);
					}
					sending = false;
					if (timed) {
						scheduleSending();
					}
				}
			}
		}

		this.sendAll = sendAll;

		function scheduleSending() {
			setTimeout(sendAll, timerInterval);
		}

		function getXmlHttp() {
			var xmlHttp = null;
			if (typeof XMLHttpRequest == "object" || typeof XMLHttpRequest == "function") {
				xmlHttp = new XMLHttpRequest();
			} else {
				try {
					xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
				} catch (e2){
					try {
						xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
					} catch (e3) {
						var msg = "AjaxAppender: could not create XMLHttpRequest object. AjaxAppender disabled";
						handleError(msg);
						isSupported = false;
						if (failCallback) {
							failCallback(msg);
						}
					}
				}
			}
			return xmlHttp;
		}

		function sendRequest(postData, successCallback) {
			try {
				var xmlHttp = getXmlHttp();
				if (isSupported) {
					if (xmlHttp.overrideMimeType) {
						xmlHttp.overrideMimeType(appender.getLayout().getContentType());
					}
					xmlHttp.onreadystatechange = function() {
						if (xmlHttp.readyState == 4) {
							var success = ((typeof xmlHttp.status == "undefined") || xmlHttp.status === 0
								|| (xmlHttp.status >= 200 && xmlHttp.status < 300));
							if (success) {
								if (successCallback) {
									successCallback(xmlHttp);
								}
								if (requestSuccessCallback) {
									requestSuccessCallback(xmlHttp);
								}
							} else {
								var msg = "AjaxAppender.append: XMLHttpRequest request to URL "
									+ url + " returned status code " + xmlHttp.status;
								handleError(msg);
								if (failCallback) {
									failCallback(msg);
								}
							}
							xmlHttp.onreadystatechange = emptyFunction;
							xmlHttp = null;
						}
					};
					xmlHttp.open("POST", url, true);
					try {
						xmlHttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
					} catch (headerEx) {
						var msg = "AjaxAppender.append: your browser's XMLHttpRequest implementation"
							+ " does not support setRequestHeader, therefore cannot post data. AjaxAppender disabled";
						handleError(msg);
						isSupported = false;
						if (failCallback) {
							failCallback(msg);
						}
						return;
					}
					xmlHttp.send(postData);
				}
			} catch (ex) {
				var msg = "AjaxAppender.append: error sending log message to " + url;
				handleError(msg, ex);
				if (failCallback) {
					failCallback(msg + ". Details: " + getExceptionStringRep(ex));
				}
			}
		}

		this.append = function(loggingEvent) {
			if (isSupported) {
				if (!initialized) {
					init();
				}
				queuedLoggingEvents.push(loggingEvent);
				var actualBatchSize = this.getLayout().allowBatching() ? batchSize : 1;

				if (queuedLoggingEvents.length >= actualBatchSize) {
					var currentLoggingEvent;
					var postData = "";
					var formattedMessages = [];
					while ((currentLoggingEvent = queuedLoggingEvents.shift())) {
						formattedMessages.push(this.getLayout().format(currentLoggingEvent));
					}
					// Create the post data string
					if (actualBatchSize == 1) {
						postData = formattedMessages.join("");
					} else {
						postData = this.getLayout().batchHeader
							+ formattedMessages.join(this.getLayout().batchSeparator)
							+ this.getLayout().batchFooter;
					}

					// Queue a request for this batch of log entries
					queuedRequests.push(postData);

					// If using a timer, the queue of requests will be processed by the
					// timer function, so nothing needs to be done here.
					if (!timed) {
						if (!waitForResponse || (waitForResponse && !sending)) {
							sendAll();
						}
					}
				}
			}
		};

		function init() {
			initialized = true;
			// Start timer
			if (timed) {
				scheduleSending();
			}
		}
	};
	
	log4javascript.AjaxAppender.prototype = new Appender();

	log4javascript.AjaxAppender.prototype.defaults = {
		waitForResponse: false,
		timed: false,
		timerInterval: 1000,
		batchSize: 1,
		requestSuccessCallback: null,
		failCallback: null
	};

	log4javascript.AjaxAppender.prototype.layout = new log4javascript.HttpPostDataLayout();

	log4javascript.AjaxAppender.prototype.toString = function() {
		return "[AjaxAppender]";
	};

	/* --------------------------------------------------------------------- */

	// BaseConsoleAppender
	// Create an anonymous function to protect base console methods

	(function() {
		var getConsoleHtmlLines = function() {
			return [
'<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">',
'<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">',
'	<head>',
'		<title>Log</title>',
'		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />',
'		<script type="text/javascript">',
'			//<![CDATA[',
'			var loggingEnabled = true;',
'',
'			function toggleLoggingEnabled() {',
'				setLoggingEnabled($("enableLogging").checked);',
'			}',
'',
'			function setLoggingEnabled(enable) {',
'				loggingEnabled = enable;',
'			}',
'',
'			var newestAtTop = false;',
'',
'			function setNewestAtTop(isNewestAtTop) {',
'				var oldNewestAtTop = newestAtTop;',
'				newestAtTop = Boolean(isNewestAtTop);',
'				if (oldNewestAtTop != newestAtTop) {',
'					// Invert the order of the log entries',
'					var lc = getLogContainer();',
'					var numberOfEntries = lc.childNodes.length;',
'					var node = null;',
'',
'					// Remove all the log container nodes',
'					var logContainerChildNodes = [];',
'					while ((node = lc.firstChild)) {',
'						lc.removeChild(node);',
'						logContainerChildNodes.push(node);',
'					}',
'',
'					// Put them all back in reverse order',
'					while ((node = logContainerChildNodes.pop())) {',
'						lc.appendChild(node);',
'					}',
'					if (scrollToLatest) {',
'						doScrollToLatest();',
'					}',
'				}',
'				$("newestAtTop").checked = isNewestAtTop',
'			}',
'',
'			function toggleNewestAtTop() {',
'				var isNewestAtTop = $("newestAtTop").checked;',
'				setNewestAtTop(isNewestAtTop);',
'			}',
'',
'			var scrollToLatest = true;',
'',
'			function setScrollToLatest(isScrollToLatest) {',
'				scrollToLatest = isScrollToLatest;',
'				if (scrollToLatest) {',
'					doScrollToLatest();',
'				}',
'			}',
'',
'			function toggleScrollToLatest() {',
'				var isScrollToLatest = $("scrollToLatest").checked;',
'				setScrollToLatest(isScrollToLatest);',
'			}',
'',
'			function doScrollToLatest() {',
'				var l = getLogContainer();',
'				if (typeof l.scrollTop != "undefined") {',
'					if (newestAtTop) {',
'						l.scrollTop = 0;',
'					} else {',
'						var latestLogEntry = l.lastChild;',
'						if (latestLogEntry) {',
'							l.scrollTop = l.scrollHeight;',
'						}',
'					}',
'				}',
'			}',
'',
'			var messagesBeforeDocLoaded = [];',
'			',
'			function log(logLevel, formattedMessage) {',
'				if (loggingEnabled) {',
'					if (loaded) {',
'						doLog(logLevel, formattedMessage);',
'					} else {',
'						messagesBeforeDocLoaded.push([logLevel, formattedMessage]);',
'					}',
'				}',
'			}',
'',
'			var logEntries = [];',
'',
'			function LogEntry(level, formattedMessage) {',
'				this.level = level;',
'				this.formattedMessage = formattedMessage;',
'				this.mainDiv = document.createElement("div");',
'				this.mainDiv.className = "logentry " + level.name;',
'',
'				// Support for the CSS attribute white-space in IE for Windows is',
'				// non-existent pre version 6 and slightly odd in 6, so instead',
'				// use two different HTML elements',
'				if (getLogContainer().currentStyle) {',
'					this.unwrappedPre = this.mainDiv.appendChild(document.createElement("pre"));',
'					this.unwrappedPre.appendChild(document.createTextNode(formattedMessage));',
'					this.unwrappedPre.className = "unwrapped";',
'					this.wrappedSpan = this.mainDiv.appendChild(document.createElement("span"));',
'					this.wrappedSpan.appendChild(document.createTextNode(formattedMessage));',
'					this.wrappedSpan.className = "wrapped";',
'				} else {',
'					this.mainDiv.appendChild(document.createTextNode(formattedMessage));',
'				}',
'			}',
'',
'			LogEntry.prototype.appendToLog = function() {',
'				var lc = getLogContainer();',
'				if (newestAtTop && lc.hasChildNodes()) {',
'					lc.insertBefore(this.mainDiv, lc.firstChild);',
'				} else {',
'					getLogContainer().appendChild(this.mainDiv);',
'				}',
'			};',
'',
'			function doLog(logLevel, formattedMessage) {',
'				var logEntry = new LogEntry(logLevel, formattedMessage);',
'				// Apply search',
'				if (currentSearch) {',
'					currentSearch.applyTo(logEntry);',
'				}',
'				logEntry.appendToLog();',
'				logEntries.push(logEntry);',
'				if (scrollToLatest) {',
'					doScrollToLatest();',
'				}',
'			}',
'',
'			function mainPageReloaded() {',
'				var separator = document.createElement("div");',
'				separator.className = "separator";',
'				separator.innerHTML = "&nbsp;";',
'				getLogContainer().appendChild(separator);',
'			}',
'',
'			window.onload = function() {',
'				setLogContainerHeight();',
'				toggleLoggingEnabled();',
'				toggleSearchEnabled();',
'				applyFilters();',
'				toggleWrap();',
'				toggleNewestAtTop();',
'				toggleScrollToLatest();',
'				doSearch();',
'				while (messagesBeforeDocLoaded.length > 0) {',
'					var currentMessage = messagesBeforeDocLoaded.shift();',
'					doLog(currentMessage[0], currentMessage[1]);',
'				}',
'				loaded = true;',
'				// Workaround to make sure log div starts at the correct size',
'				setTimeout(setLogContainerHeight, 20);',
'',
'				// Remove "Close" button if not in pop-up mode',
'				if (window != top) {',
'					$("closeButton").style.display = "none";',
'				}',
'			};',
'',
'			var loaded = false;',
'',
'			var logLevels = ["TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL"];',
'',
'			function getCheckBox(logLevel) {',
'				return $("switch_" + logLevel);',
'			}',
'',
'			function getLogContainer() {',
'				return $("log");',
'			}',
'',
'			function applyFilters() {',
'				for (var i = 0; i < logLevels.length; i++) {',
'					if (getCheckBox(logLevels[i]).checked) {',
'						addClass(getLogContainer(), logLevels[i]);',
'					} else {',
'						removeClass(getLogContainer(), logLevels[i]);',
'					}',
'				}',
'			}',
'',
'			function toggleAllLevels() {',
'				var turnOn = $("switch_ALL").checked;',
'				for (var i = 0; i < logLevels.length; i++) {',
'					getCheckBox(logLevels[i]).checked = turnOn;',
'					if (turnOn) {',
'						addClass(getLogContainer(), logLevels[i]);',
'					} else {',
'						removeClass(getLogContainer(), logLevels[i]);',
'					}',
'				}',
'			}',
'',
'			function checkAllLevels() {',
'				for (var i = 0; i < logLevels.length; i++) {',
'					if (!getCheckBox(logLevels[i]).checked) {',
'						getCheckBox("ALL").checked = false;',
'						return;',
'					}',
'				}',
'				getCheckBox("ALL").checked = true;',
'			}',
'',
'			function clearLog() {',
'				getLogContainer().innerHTML = "";',
'			}',
'',
'			function toggleWrap() {',
'				var enable = $("wrap").checked;',
'				if (enable) {',
'					addClass(getLogContainer(), "wrap");',
'				} else {',
'					removeClass(getLogContainer(), "wrap");',
'				}',
'			}',
'',
'			/* ------------------------------------------------------------------- */',
'',
'			// Search',
'',
'			var searchTimer = null;',
'',
'			function scheduleSearch() {',
'				try {',
'					clearTimeout(searchTimer);',
'				} catch (ex) {',
'					// Do nothing',
'				}',
'				searchTimer = setTimeout(doSearch, 500);',
'			}',
'',
'			function Search(searchTerm, isRegex, searchRegex, isCaseSensitive) {',
'				this.searchTerm = searchTerm;',
'				this.isRegex = isRegex;',
'				this.searchRegex = searchRegex;',
'				this.isCaseSensitive = isCaseSensitive;',
'			}',
'',
'			Search.prototype.matches = function(logEntry) {',
'				var entryText = logEntry.formattedMessage;',
'				var matchesSearch = false;',
'				if (this.isRegex) {',
'					matchesSearch = this.searchRegex.test(entryText);',
'				} else if (this.isCaseSensitive) {',
'					matchesSearch = (entryText.indexOf(this.searchTerm) > -1);',
'				} else {',
'					matchesSearch = (entryText.toLowerCase().indexOf(this.searchTerm.toLowerCase()) > -1);',
'				}',
'				return matchesSearch;',
'			};',
'',
'			Search.prototype.applyTo = function(logEntry) {',
'				var doesMatch = this.matches(logEntry);',
'				if (doesMatch) {',
'					replaceClass(logEntry.mainDiv, "searchmatch", "searchnonmatch");',
'				} else {',
'					replaceClass(logEntry.mainDiv, "searchnonmatch", "searchmatch");',
'				}',
'				return doesMatch;',
'			};',
'',
'			var currentSearch = null;',
'',
'			function doSearch() {',
'				var searchBox = $("searchBox");',
'				var searchTerm = searchBox.value;',
'				var isRegex = $("searchRegex").checked;',
'				var isCaseSensitive = $("searchCaseSensitive").checked;',
'				',
'				if (searchTerm === "") {',
'					$("searchReset").disabled = true;',
'					removeClass(getLogContainer(), "searching");',
'					for (var logEntry = getLogContainer().firstChild; logEntry !== null; logEntry = logEntry.nextSibling) {',
'						removeClass(logEntry, "searchmatch");',
'						removeClass(logEntry, "searchnonmatch");',
'					}',
'				} else {',
'					$("searchReset").disabled = false;',
'					var searchRegex;',
'					var regexValid;',
'					if (isRegex) {',
'						try {',
'							searchRegex = isCaseSensitive ? new RegExp(searchTerm) : new RegExp(searchTerm, "i");',
'							regexValid = true;',
'							replaceClass(searchBox, "validregex", "invalidregex");',
'							searchBox.title = "Valid regex";',
'						} catch (ex) {',
'							regexValid = false;',
'							replaceClass(searchBox, "invalidregex", "validregex");',
'							searchBox.title = "Invalid regex: " + (ex.message ? ex.message : (ex.description ? ex.description : "unknown error"));',
'							return;',
'						}',
'					} else {',
'						searchBox.title = "";',
'						removeClass(searchBox, "validregex");',
'						removeClass(searchBox, "invalidregex");',
'					}',
'					addClass(getLogContainer(), "searching");',
'					currentSearch = new Search(searchTerm, isRegex, searchRegex, isCaseSensitive);',
'					for (var i = 0; i < logEntries.length; i++) {',
'						currentSearch.applyTo(logEntries[i]);',
'					}',
'					// The following line is a workaround for a bug in Konqueror that stops the search results displaying',
'					doScrollToLatest();',
'				}',
'			}',
'',
'			function toggleSearchEnabled(enable) {',
'				enable = (typeof enable == "undefined") ? !$("searchDisable").checked : enable;',
'				$("searchBox").disabled = !enable;',
'				$("searchReset").disabled = !enable;',
'				$("searchRegex").disabled = !enable;',
'				$("searchCaseSensitive").disabled = !enable;',
'				if (enable) {',
'					removeClass($("search"), "greyedout");',
'					addClass(getLogContainer(), "searching");',
'					$("searchDisable").checked = !enable;',
'				} else {',
'					addClass($("search"), "greyedout");',
'					removeClass(getLogContainer(), "searching");',
'				}',
'			}',
'',
'			function clearSearch() {',
'				$("searchBox").value = "";',
'				doSearch();',
'			}',
'',
'			/* ------------------------------------------------------------------------- */',
'			',
'			// CSS Utilities',
'			',
'			function addClass(el, cssClass) {',
'				if (!hasClass(el, cssClass)) {',
'					if (el.className) {',
'						el.className += " " + cssClass;',
'					} else {',
'						el.className = cssClass;',
'					}',
'				}',
'			}',
'',
'			function hasClass(el, cssClass) {',
'				if (el.className) {',
'					var classNames = el.className.split(" ");',
'					return array_contains(classNames, cssClass);',
'				}',
'				return false;',
'			}',
'',
'			function removeClass(el, cssClass) {',
'				if (hasClass(el, cssClass)) {',
'					// Rebuild the className property',
'					var existingClasses = el.className.split(" ");',
'					var newClasses = [];',
'					for (var i = 0; i < existingClasses.length; i++) {',
'						if (existingClasses[i] != cssClass) {',
'							newClasses[newClasses.length] = existingClasses[i];',
'						}',
'					}',
'					el.className = newClasses.join(" ");',
'				}',
'			}',
'',
'			function replaceClass(el, newCssClass, oldCssClass) {',
'				removeClass(el, oldCssClass);',
'				addClass(el, newCssClass);',
'			}',
'',
'			/* ------------------------------------------------------------------------- */',
'',
'			// Other utility functions',
'',
'			// Syntax borrowed from Prototype library',
'			function $(id) {',
'				return document.getElementById(id);',
'			}',
'',
'			function getWindowHeight() {',
'				if (window.innerHeight) {',
'					return window.innerHeight;',
'				} else if (document.documentElement && document.documentElement.clientHeight) {',
'					return document.documentElement.clientHeight;',
'				} else if (document.body) {',
'					return document.body.clientHeight;',
'				}',
'				return 0;',
'			}',
'',
'			function setLogContainerHeight() {',
'				var windowHeight = getWindowHeight();',
'				$("body").style.height = getWindowHeight() + "px";',
'				getLogContainer().style.height = ""',
'					+ (windowHeight - (11 + $("switches").offsetHeight)) + "px";',
'			}',
'			window.onresize = setLogContainerHeight;',
'',
'			if (!Array.prototype.push) {',
'				Array.prototype.push = function() {',
'			        for (var i = 0; i < arguments.length; i++){',
'			            this[this.length] = arguments[i];',
'			        }',
'			        return this.length;',
'				};',
'			}',
'',
'			if (!Array.prototype.pop) {',
'				Array.prototype.pop = function() {',
'					if (this.length > 0) {',
'						var val = this[this.length - 1];',
'						this.length = this.length - 1;',
'						return val;',
'					}',
'				};',
'			}',
'',
'			if (!Array.prototype.shift) {',
'				Array.prototype.shift = function() {',
'					if (this.length > 0) {',
'						var firstItem = this[0];',
'						for (var i = 0; i < this.length - 1; i++) {',
'							this[i] = this[i + 1];',
'						}',
'						this.length = this.length - 1;',
'						return firstItem;',
'					}',
'				};',
'			}',
'',
'			function array_contains(arr, val) {',
'				for (var i = 0; i < arr.length; i++) {',
'					if (arr[i] == val) {',
'						return true;',
'					}',
'				}',
'				return false;',
'			}',
'			//]]>',
'		</script>',
'		<style type="text/css">',
'			body {',
'				background-color: white;',
'				color: black;',
'				padding: 0px;',
'				margin: 0px;',
'				font-family: verdana, arial, helvetica, sans-serif;',
'				font-size: 75%;',
'				overflow: hidden;',
'			}',
'',
'			div#switchesContainer {',
'				background-color: #eeeeee;',
'				border-width: 0px 0px 1px 0px;',
'				border-color: gray;',
'				border-style: solid;',
'				padding: 5px;',
'				width: 100%;',
'			}',
'',
'			div#switchesContainer input {',
'				margin-bottom: 0px;',
'			}',
'',
'			div#switches input.button {',
'				padding: 2px;',
'				font-size: 92.66%;',
'			}',
'',
'			div#switches input#clearButton {',
'				margin-left: 20px;',
'			}',
'',
'			div#levels label {',
'				font-weight: bold;',
'			}',
'',
'			div#levels label, div#options label {',
'				margin-right: 5px;',
'			}',
'',
'			div#levels label#wrapLabel {',
'				font-weight: normal;',
'			}',
'',
'			div#search {',
'				padding: 5px 0px;',
'			}',
'',
'			div#search label {',
'				margin-right: 10px;',
'			}',
'',
'			div#search label.searchBoxLabel {',
'				margin-right: 0px;',
'			}',
'',
'			div#search input.validregex {',
'				color: green;',
'			}',
'',
'			div#search input.invalidregex {',
'				color: red;',
'			}',
'',
'			*.greyedout {',
'				color: gray;',
'			}',
'',
'			*.greyedout *.alwaysenabled {',
'				color: black;',
'			}',
'',
'			div#log {',
'				font-family: Courier New, Courier;',
'				font-size: 100%;',
'				width: 100%;',
'				overflow: auto;',
'			}',
'',
'			div#log *.logentry {',
'				overflow: visible;',
'				display: none;',
'				white-space: pre;',
'			}',
'',
'			div#log *.logentry pre.unwrapped {',
'				display: inline;',
'			}',
'',
'			div#log *.logentry span.wrapped {',
'				display: none;',
'			}',
'',
'			div.wrap#log *.logentry {',
'				white-space: normal !important;',
'				border-width: 0px 0px 1px 0px;',
'				border-color: #dddddd;',
'				border-style: dotted;',
'			}',
'',
'			div.wrap#log *.logentry pre.unwrapped {',
'				display: none;',
'			}',
'',
'			div.wrap#log *.logentry span.wrapped {',
'				display: inline;',
'			}',
'',
'			div.searching *.searchnonmatch {',
'				display: none !important;',
'			}',
'',
'			div#log *.TRACE, label#label_TRACE {',
'				color: #666666;',
'			}',
'',
'			div#log *.DEBUG, label#label_DEBUG {',
'				color: green;',
'			}',
'',
'			div#log *.INFO, label#label_INFO {',
'				color: #000099;',
'			}',
'',
'			div#log *.WARN, label#label_WARN {',
'				color: #999900;',
'			}',
'',
'			div#log *.ERROR, label#label_ERROR {',
'				color: red;',
'			}',
'',
'			div#log *.FATAL, label#label_FATAL {',
'				color: #660066;',
'			}',
'',
'			div.TRACE#log *.TRACE,',
'			div.DEBUG#log *.DEBUG,',
'			div.INFO#log *.INFO,',
'			div.WARN#log *.WARN,',
'			div.ERROR#log *.ERROR,',
'			div.FATAL#log *.FATAL {',
'				display: block;',
'			}',
'',
'			div#log div.separator {',
'				background-color: #cccccc;',
'				margin: 5px 0px;',
'				line-height: 1px;',
'			}',
'		</style>',
'	</head>',
'	<body id="body">',
'		<div id="switchesContainer">',
'			<div id="switches">',
'				<div id="levels">',
'					<input type="checkbox" id="switch_TRACE" onclick="applyFilters(); checkAllLevels()" checked="checked" title="Show/hide trace messages" /><label for="switch_TRACE" id="label_TRACE">trace</label>',
'					<input type="checkbox" id="switch_DEBUG" onclick="applyFilters(); checkAllLevels()" checked="checked" title="Show/hide debug messages" /><label for="switch_DEBUG" id="label_DEBUG">debug</label>',
'					<input type="checkbox" id="switch_INFO" onclick="applyFilters(); checkAllLevels()" checked="checked" title="Show/hide info messages" /><label for="switch_INFO" id="label_INFO">info</label>',
'					<input type="checkbox" id="switch_WARN" onclick="applyFilters(); checkAllLevels()" checked="checked" title="Show/hide warn messages" /><label for="switch_WARN" id="label_WARN">warn</label>',
'					<input type="checkbox" id="switch_ERROR" onclick="applyFilters(); checkAllLevels()" checked="checked" title="Show/hide error messages" /><label for="switch_ERROR" id="label_ERROR">error</label>',
'					<input type="checkbox" id="switch_FATAL" onclick="applyFilters(); checkAllLevels()" checked="checked" title="Show/hide fatal messages" /><label for="switch_FATAL" id="label_FATAL">fatal</label>',
'					<input type="checkbox" id="switch_ALL" onclick="toggleAllLevels(); applyFilters()" checked="checked" title="Show/hide all messages" /><label for="switch_ALL" id="label_ALL">all</label>',
'				</div>',
'				<div id="search">',
'					<label for="searchBox" class="searchBoxLabel">Search: </label><input type="text" id="searchBox" onclick="toggleSearchEnabled(true)" onkeyup="scheduleSearch()" size="20" />',
'					<input type="button" id="searchReset" disabled="disabled" value="Reset" onclick="clearSearch()" class="button" title="Reset the search" />',
'					<input type="checkbox" id="searchRegex" onclick="doSearch()" title="If checked, search is treated as a regular expression" /><label for="searchRegex">Regex</label>',
'					<input type="checkbox" id="searchCaseSensitive" onclick="doSearch()" title="If checked, search is case sensitive" /><label for="searchCaseSensitive">Match case</label>',
'					<input type="checkbox" id="searchDisable" onclick="toggleSearchEnabled()" title="Enable/disable search" /><label for="searchDisable" class="alwaysenabled">Disable</label>',
'				</div>',
'				<div id="options">',
'					<input type="checkbox" id="enableLogging" onclick="toggleLoggingEnabled()" checked="checked" title="Enable/disable logging" /><label for="enableLogging" id="wrapLabel">Log</label>',
'					<input type="checkbox" id="wrap" onclick="toggleWrap()" title="Enable/disable word wrap" /><label for="wrap" id="wrapLabel">Wrap</label>',
'					<input type="checkbox" id="newestAtTop" onclick="toggleNewestAtTop()" title="If checked, causes newest messages to appear at the top" /><label for="newestAtTop" id="newestAtTopLabel">Newest at the top</label>',
'					<input type="checkbox" id="scrollToLatest" onclick="toggleScrollToLatest()" checked="checked" title="If checked, window automatically scrolls to a new message when it is added" /><label for="scrollToLatest" id="scrollToLatestLabel">Scroll to latest</label>',
'					<input type="button" id="clearButton" value="Clear" onclick="clearLog()" class="button" title="Clear all log messages"  />',
'					<input type="button" id="closeButton" value="Close" onclick="window.close()" class="button" title="Close the window" />',
'				</div>',
'			</div>',
'		</div>',
'		<div id="log" class="DEBUG INFO WARN ERROR FATAL"></div>',
'	</body>',
'</html>'
];
		};

		function ConsoleAppender() {}

		var consoleAppenderIdCounter = 1;

		ConsoleAppender.prototype = new Appender();

		ConsoleAppender.prototype.create = function(inline, containerElement,
				layout, lazyInit, focusConsoleWindow, useOldPopUp,
				complainAboutPopUpBlocking, newestMessageAtTop,
				scrollToLatestMessage, initiallyMinimized, width, height) {
			var appender = this;

			// Common properties
			if (layout) {
				this.setLayout(layout);
			} else {
				this.setLayout(this.defaults.layout);
			}
			var initialized = false;
			var consoleWindowLoaded = false;
			var queuedLoggingEvents = [];
			var isSupported = true;
			var consoleAppenderId = consoleAppenderIdCounter++;

			// Params
			lazyInit = extractBooleanFromParam(lazyInit, true);
			newestMessageAtTop = extractBooleanFromParam(newestMessageAtTop, this.defaults.newestMessageAtTop);
			scrollToLatestMessage = extractBooleanFromParam(scrollToLatestMessage, this.defaults.scrollToLatestMessage);
			width = width ? width : this.defaults.width;
			height = height ? height : this.defaults.height;

			// Functions whose implementations vary between subclasses
			var init, safeToAppend, getConsoleWindow;

			// Configuration methods. The function scope is used to prevent
			// direct alteration to the appender configuration properties.
			var appenderName = inline ? "InlineAppender" : "PopUpAppender";
			var checkCanConfigure = function(configOptionName) {
				if (initialized) {
					handleError(appenderName + ": configuration option '" + configOptionName + "' may not be set after the appender has been initialized");
					return false;
				}
				return true;
			};

			this.isNewestMessageAtTop = function() { return newestMessageAtTop; };
			this.setNewestMessageAtTop = function(_newestMessageAtTop) {
				newestMessageAtTop = Boolean(_newestMessageAtTop);
				if (consoleWindowLoaded && isSupported) {
					getConsoleWindow().setNewestAtTop(newestMessageAtTop);
				}
			};

			this.isScrollToLatestMessage = function() { return scrollToLatestMessage; };
			this.setScrollToLatestMessage = function(_scrollToLatestMessage) {
				scrollToLatestMessage = Boolean(_scrollToLatestMessage);
				if (consoleWindowLoaded && isSupported) {
					getConsoleWindow().setScrollToLatest(scrollToLatestMessage);
				}
			};

			this.getWidth = function() { return width; };
			this.setWidth = function(_width) {
				if (checkCanConfigure("width")) {
					width = extractStringFromParam(_width, width);
				}
			};

			this.getHeight = function() { return height; };
			this.setHeight = function(_height) {
				if (checkCanConfigure("height")) {
					height = extractStringFromParam(_height, height);
				}
			};

			// Common methods
			this.append = function(loggingEvent) {
				if (isSupported) {
					if (!initialized) {
						init();
					}
					queuedLoggingEvents.push(loggingEvent);
					appendQueuedLoggingEvents();
				}
			};

			var appendQueuedLoggingEvents = function(loggingEvent) {
				if (safeToAppend()) {
					while (queuedLoggingEvents.length > 0) {
						var currentLoggingEvent = queuedLoggingEvents.shift();
						var formattedMessage = appender.getLayout().format(currentLoggingEvent);
						getConsoleWindow().log(currentLoggingEvent.level, formattedMessage);
					}
					if (focusConsoleWindow) {
						getConsoleWindow().focus();
					}
				}
			};

			var writeHtml = function(doc) {
				var lines = getConsoleHtmlLines();
				doc.open();
				for (var i = 0; i < lines.length; i++) {
					doc.writeln(lines[i]);
				}
				doc.close();
			};

			var pollConsoleWindow = function(windowTest, successCallback, errorMessage) {
				function pollConsoleWindowLoaded() {
					try {
						if (windowTest(getConsoleWindow())) {
							clearInterval(poll);
							successCallback();
						}
					} catch (ex) {
						clearInterval(poll);
						isSupported = false;
						handleError(errorMessage, ex);
					}
				}

				// Poll the pop-up since the onload event is not reliable
				var poll = setInterval(pollConsoleWindowLoaded, 100);
			}

			// Define methods and properties that vary between subclasses
			if (inline) {
				// InlineAppender

				// Extract params
				if (!containerElement || !containerElement.appendChild) {
					isSupported = false;
					handleError("InlineAppender.init: a container DOM element must be supplied for the console window");
					return;
				}
				initiallyMinimized = extractBooleanFromParam(initiallyMinimized, appender.defaults.initiallyMinimized);

				// Configuration methods. The function scope is used to prevent
				// direct alteration to the appender configuration properties.
				this.isInitiallyMinimized = function() { return initiallyMinimized; };
				this.setInitiallyMinimized = function(_initiallyMinimized) {
					if (checkCanConfigure("initiallyMinimized")) {
						initiallyMinimized = Boolean(_initiallyMinimized);
					}
				};

				// Define useful variables
				var minimized = false;
				var iframeContainerDiv;
				var iframeRemoved = false;
				var iframeId = "log4javascriptInlineAppender" + consoleAppenderId;

				this.hide = function() {
					iframeContainerDiv.style.display = "none";
					minimized = true;
				};

				this.show = function() {
					iframeContainerDiv.style.display = "block";
					minimized = false;
				};

				this.isVisible = function() {
					return !minimized;
				};
				
				this.close = function() {
					if (!iframeRemoved) {
						iframeContainerDiv.parentNode.removeChild(iframeContainerDiv);
						iframeRemoved = true;
					}
				};

				// Create init, getConsoleWindow and safeToAppend functions
				init = function() {
					var initErrorMessage = "InlineAppender.init: unable to create console iframe"; 
					function finalInit() {
						try {
							getConsoleWindow().setNewestAtTop(newestMessageAtTop);
							getConsoleWindow().setScrollToLatest(scrollToLatestMessage);
							consoleWindowLoaded = true;
							appendQueuedLoggingEvents();
							if (initiallyMinimized) {
								appender.hide();
							}
						} catch (ex) {
							isSupported = false;
							handleError(initErrorMessage, ex);
						}
					}

					function writeToDocument() {
						try {
							var windowTest = function(win) { return Boolean(win.loaded); };
							writeHtml(getConsoleWindow().document);
							if (windowTest(getConsoleWindow())) {
								finalInit();
							} else {
								pollConsoleWindow(windowTest, finalInit, initErrorMessage);
							}
						} catch (ex) {
							isSupported = false;
							handleError(initErrorMessage, ex);
						}
					}
					
					minimized = initiallyMinimized;
					iframeContainerDiv = containerElement.appendChild(document.createElement("div"));

					iframeContainerDiv.style.width = "" + width + "px";
					iframeContainerDiv.style.height = "" + height + "px";
					iframeContainerDiv.style.border = "solid gray 1px";
					
					// Adding an iframe using the DOM would be preferable, but it doesn't work
					// in IE5 on Windows, or in Konqueror prior to version 3.5 - in Konqueror
					// it creates the iframe fine but I haven't been able to find a way to obtain
					// the window object
					var iframeHtml = "<iframe id='" + iframeId + "' name='" + iframeId
						+ "' width='100%' height='100%' frameborder='0'"
						+ "scrolling='no'></iframe>";
					iframeContainerDiv.innerHTML = iframeHtml;
					
					// Write the console HTML to the iframe
					var iframeDocumentExistsTest = function(win) { return Boolean(win && win.document); };
					if (iframeDocumentExistsTest(getConsoleWindow())) {
						writeToDocument();
					} else {
						pollConsoleWindow(iframeDocumentExistsTest, writeToDocument, initErrorMessage);
					}
					
					initialized = true;
				};

				getConsoleWindow = function() {
					var iframe = window.frames[iframeId];
					if (iframe) {
						return iframe;
					}
				};

				safeToAppend = function() {
					if (isSupported && !iframeRemoved) {
						if (!consoleWindowLoaded && getConsoleWindow() && getConsoleWindow().loaded) {
							consoleWindowLoaded = true;
						}
						return consoleWindowLoaded;
					}
					return false;
				};
			} else {
				// PopUpAppender

				// Extract params
				useOldPopUp = extractBooleanFromParam(useOldPopUp, appender.defaults.useOldPopUp);
				complainAboutPopUpBlocking = extractBooleanFromParam(complainAboutPopUpBlocking, appender.defaults.complainAboutPopUpBlocking);

				// Configuration methods. The function scope is used to prevent
				// direct alteration to the appender configuration properties.
				this.isUseOldPopUp = function() { return useOldPopUp; };
				this.setUseOldPopUp = function(_useOldPopUp) {
					if (checkCanConfigure("useOldPopUp")) {
						useOldPopUp = Boolean(_useOldPopUp);
					}
				};

				this.isComplainAboutPopUpBlocking = function() { return complainAboutPopUpBlocking; };
				this.setComplainAboutPopUpBlocking = function(_complainAboutPopUpBlocking) {
					if (checkCanConfigure("complainAboutPopUpBlocking")) {
						complainAboutPopUpBlocking = Boolean(_complainAboutPopUpBlocking);
					}
				};

				this.isFocusPopUp = function() { return focusConsoleWindow; };
				this.setFocusPopUp = function(_focusPopUp) {
					// This property can be safely altered after logging has started
					focusConsoleWindow = Boolean(_focusPopUp);
				};
				
				this.close = function() {
					try {
						popUp.close();
					} catch (e) {
					}
					popUpClosed = true;
				}

				// Define useful variables
				var popUp;
				var popUpClosed = false;

				// Create init, getConsoleWindow and safeToAppend functions
				init = function() {
					var appender = this;
					var windowProperties = "width=" + width + ",height=" + height + ",status,resizable";
					var windowName = "log4javascriptPopUp" + consoleAppenderId;

					function finalInit() {
						consoleWindowLoaded = true;
						getConsoleWindow().setNewestAtTop(newestMessageAtTop);
						getConsoleWindow().setScrollToLatest(scrollToLatestMessage);
						appendQueuedLoggingEvents();
					}

					try {
						popUp = window.open("", windowName, windowProperties);
						if (popUp) {
							if (useOldPopUp && popUp.loaded) {
								popUp.mainPageReloaded();
								finalInit();
							} else {
								writeHtml(popUp.document);
								// Check if the pop-up window object is available
								var popUpLoadedTest = function(win) { return Boolean(win) && win.loaded; };
								if (popUp.loaded) {
									finalInit();
								} else {
									pollConsoleWindow(popUpLoadedTest, finalInit, "PopUpAppender.init: unable to create console window");
								}
							}
						} else {
							isSupported = false;
							logLog.warn("PopUpAppender.init: pop-ups blocked, please unblock to use PopUpAppender");
							if (complainAboutPopUpBlocking) {
								handleError("log4javascript: pop-up windows appear to be blocked. Please unblock them to use pop-up logging.");
							}
						}
					} catch (ex) {
						handleError("PopUpAppender.init: error creating pop-up", ex);
					}
					initialized = true;
				};

				getConsoleWindow = function() {
					return popUp;
				};

				safeToAppend = function() {
					if (isSupported && !popUpClosed) {
						if (popUp.closed || 
								(consoleWindowLoaded && (typeof popUp.closed == "undefined"))) { // Extra check for Opera
							popUpClosed = true;
							logLog.debug("PopUpAppender: pop-up closed");
							return false;
						}
						if (!consoleWindowLoaded && popUp.loaded) {
							consoleWindowLoaded = true;
						}
					}
					return isSupported && consoleWindowLoaded && !popUpClosed;
				};
			}

			if (enabled && !lazyInit) {
				init();
			}
		};

		/* ----------------------------------------------------------------- */

		log4javascript.PopUpAppender = function(layout, lazyInit, focusPopUp,
				useOldPopUp, complainAboutPopUpBlocking, newestMessageAtTop,
				scrollToLatestMessage, width, height) {

			var focusConsoleWindow = extractBooleanFromParam(focusPopUp, this.defaults.focusPopUp);

			this.create(false, null, layout, lazyInit, focusConsoleWindow,
				useOldPopUp, complainAboutPopUpBlocking,
				newestMessageAtTop, scrollToLatestMessage, null, width, height);
		};

		log4javascript.PopUpAppender.prototype = new ConsoleAppender();

		log4javascript.PopUpAppender.prototype.defaults = {
			layout: new log4javascript.PatternLayout("%d{HH:mm:ss} %-5p - %m"),
			focusPopUp: false,
			lazyInit: true,
			useOldPopUp: true,
			complainAboutPopUpBlocking: true,
			newestMessageAtTop: false,
			scrollToLatestMessage: true,
			width: "600",
			height: "400"
		};

		log4javascript.PopUpAppender.prototype.toString = function() {
			return "[PopUpAppender]";
		};

		/* ----------------------------------------------------------------- */

		log4javascript.InlineAppender = function(containerElement, lazyInit,
				layout, initiallyMinimized, newestMessageAtTop,
				scrollToLatestMessage, width, height) {

			this.create(true, containerElement, layout, lazyInit, false,
				null, null, newestMessageAtTop, scrollToLatestMessage,
				initiallyMinimized, width, height);
		};

		log4javascript.InlineAppender.prototype = new ConsoleAppender();

		log4javascript.InlineAppender.prototype.defaults = {
			layout: new log4javascript.PatternLayout("%d{HH:mm:ss} %-5p - %m"),
			initiallyMinimized: false,
			lazyInit: true,
			newestMessageAtTop: false,
			scrollToLatestMessage: true,
			width: "100%",
			height: "250px"
		};

		log4javascript.InlineAppender.prototype.toString = function() {
			return "[InlineAppender]";
		};
	})();

	/* --------------------------------------------------------------------- */

	// BrowserConsoleAppender (only works in Opera and Safari)
	log4javascript.BrowserConsoleAppender = function(layout) {
		if (layout) {
			this.setLayout(layout);
		}
	};

	log4javascript.BrowserConsoleAppender.prototype = new log4javascript.Appender();
	log4javascript.BrowserConsoleAppender.prototype.layout = new log4javascript.SimpleLayout();
	log4javascript.BrowserConsoleAppender.prototype.threshold = log4javascript.Level.WARN;

	log4javascript.BrowserConsoleAppender.prototype.append = function(loggingEvent) {
		if ((typeof opera != "undefined") && opera.postError) {
			opera.postError(this.getLayout().format(loggingEvent));
		} else if (window.console && window.console.log) {
			window.console.log(this.getLayout().format(loggingEvent));
		}
	};

	log4javascript.BrowserConsoleAppender.prototype.toString = function() {
		return "[BrowserConsoleAppender]";
	};

	return log4javascript;
})();
