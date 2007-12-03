package edu.iu.uis.eden.routetemplate.xmlrouting;

import java.util.List;

import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

public class UpperCaseFunction implements XPathFunction {

	public Object evaluate(List parameters) throws XPathFunctionException {
		String parameter = parameters.get(0).toString();
		return parameter.toUpperCase();
	}

}
