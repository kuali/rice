/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.bo;

import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Table;



/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class JpaToOjbMetadata {

	public static void main( String[] args ) {
		
		Class clazz = Parameter.class;

		
		StringBuffer sb = new StringBuffer( 1000 );
		Table tableAnnotation = (Table)clazz.getAnnotation( Table.class );
		
		sb.append( "	<class-descriptor class=\"" ).append( clazz.getName() ).append( "\" table=\"" );
		sb.append( tableAnnotation.name() ).append( "\">\r\n" );

		getClassFields( clazz, sb );
		getReferences( clazz, sb );
		sb.append( "	</class-descriptor>\r\n" );
		
		System.out.println( sb.toString() );
	}

	
	private static String javaToOjbDataType( Class dataType ) {
		if ( dataType.equals( String.class ) ) {
			return "VARCHAR";
		}
		return "VARCHAR";
	}
	
	private static void getClassFields( Class clazz, StringBuffer sb ) {
		for ( Field field : clazz.getDeclaredFields() ) {
			Id id = (Id)field.getAnnotation( Id.class );
			Column column = (Column)field.getAnnotation( Column.class );
			if ( column != null ) {
				sb.append( "		<field-descriptor name=\"" );
				sb.append( field.getName() );
				sb.append( "\" column=\"" );
				sb.append( column.name() );
				sb.append( "\" jdbc-type=\"" );
				sb.append( javaToOjbDataType( field.getType() ) );
				sb.append( "\" " );
				if ( id != null ) {
					sb.append( "primarykey=\"true\" " );
				}
				if ( field.getName().equals( "objectId" ) ) {
					sb.append( "index=\"true\" " );
				}
				if ( field.getName().equals( "versionNumber" ) ) {
					sb.append( "locking=\"true\" " );
				}
				sb.append( " />\r\n" );
			}
		}
		if ( !clazz.equals( PersistableBusinessObject.class ) && clazz.getSuperclass() != null ) {
			getClassFields( clazz.getSuperclass(), sb );
		}
	}

	private static void getReferences( Class clazz, StringBuffer sb ) {
		for ( Field field : clazz.getDeclaredFields() ) {
			JoinColumns multiKey = (JoinColumns)field.getAnnotation( JoinColumns.class );
			JoinColumn singleKey = (JoinColumn)field.getAnnotation( JoinColumn.class );
			if ( multiKey != null || singleKey != null ) {
				sb.append( "		<reference-descriptor name=\"" );
				sb.append( field.getName() );
				sb.append( "\" class-ref=\"" );
				sb.append( field.getType().getName() );
				sb.append( "\" auto-retrieve=\"true\" auto-update=\"none\" auto-delete=\"none\" proxy=\"true\">\r\n" );
				if ( multiKey != null ) {
					for ( JoinColumn col : multiKey.value() ) {
						sb.append( "			<foreignkey field-ref=\"" );
						sb.append( getPropertyFromField( clazz, col.name() ) );
						sb.append( " />\r\n" );
					}
				} else {
					sb.append( "			<foreignkey field-ref=\"" );
					sb.append( getPropertyFromField( clazz, singleKey.name() ) );
					sb.append( " />\r\n" );
				}
				sb.append( "		</reference-descriptor>\r\n" );
			}
		}
	}
	
	private static String getPropertyFromField( Class clazz, String colName ) {
		for ( Field field : clazz.getDeclaredFields() ) {
			Column column = (Column)field.getAnnotation( Column.class );
			if ( column != null ) {
				if ( column.name().equals( colName ) ) {
					return field.getName();
				}
			}
		}
		return "";
	}
}
