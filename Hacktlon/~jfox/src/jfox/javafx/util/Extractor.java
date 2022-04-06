package jfox.javafx.util;

import java.lang.reflect.Method;

import javafx.util.Callback;


public class Extractor<S, T> implements Callback<T, String >  {
	
	
	// Fields
	
	private String propertyName;
	private String methodName;
	private Method method;
	private boolean flagError;
	
	
	// Contrructor
	
	public Extractor( String propertyName ) {
		this.propertyName = propertyName;
	}
	
	
	
	@Override
	public String call(T param) {
		
		if ( flagError ) {
			return null;
		}
		
		if ( method == null ) {
			methodName = "get" + propertyName.substring(0,1).toUpperCase() + propertyName.substring(1);
			try {
				method = param.getClass().getMethod( methodName );
			} catch (NoSuchMethodException | SecurityException e) {
			}
			
			if ( method == null ) {
				methodName = methodName.replace( "get", "is" );
				try {
					method = param.getClass().getMethod( methodName );
				} catch (NoSuchMethodException | SecurityException e) {
				}
			}
		}
		
		if ( method == null ) {
			flagError = true;
			Exception e = new java.lang.IllegalStateException ( "Cannot read from unreadable property " + propertyName );
			e.printStackTrace();
			return null;
		} else {
			try {
				return  String.valueOf( method.invoke( param ) );
			} catch (Exception e) {
				throw  UtilFX.runtimeException(e);
			}
		}
		
	}

}
