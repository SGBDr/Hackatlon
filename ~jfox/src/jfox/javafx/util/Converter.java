package jfox.javafx.util;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextInputControl;
import javafx.util.StringConverter;
import jfox.exception.ExceptionValidation;
import jfox.localization.BundleMessages;


public abstract class Converter<T> extends StringConverter<T> {

	public final static String MSG_DEFAULT = BundleMessages.getString( "check.error.value" );
	
	// Champs
	private String 	message;
	private boolean	flagParseError;
	private String	inputText;
	private String	outputText;
	private Node	node;
	
	
	// Construteurs
	
	public Converter( String message ) {
		if ( message != null ) {
			this.message = message;
		} else {
			this.message = MSG_DEFAULT;
		}
	}
	
	
	// Getters & Setters
	
	public String getOutputText() {
		return outputText;
	}

	public void setNode(Node node) {
		this.node = node;
	}
	
	public boolean hasParseError() {
		return flagParseError;
	}
	
	
	// Actions
	
	@Override
	public String toString( T object) {
		if ( object == null ) {
			outputText = null;
		} else {
			outputText = format( object ); 
		}
		return outputText;
	}

	
	@Override
	public T fromString( String string ) {

		flagParseError = false;
		
		if ( string == null || string.trim().isEmpty() ) {
			inputText = null;
			outputText = null;
			return null;
		}

		inputText = new String( string );
		string = string.replace( " ",  "" );

		try {
			T value = parse( string );
			toString( value );
			return value;
		} catch ( Exception e ) {
			flagParseError = true;
			return null;
		}
	}
	

	public void checkParseError() {
		
		if ( flagParseError ) {
			if ( node != null ) {
				UtilFX.selectTab( node );
				node.requestFocus();
			}
			
			throw new ExceptionValidation( String.format( message, inputText) );
		}
	}
	

	public void cancelParseError() {
		
		if ( flagParseError ) {
			if ( node instanceof TextInputControl ) {
				( (TextInputControl) node ).setText( null );;
			} else if ( node instanceof ComboBox ) {
				( (ComboBox<?>) node ).getEditor().setText( null );;
			} else if ( node instanceof DatePicker ) {
				( (DatePicker) node ).getEditor().setText( null );;
			}
			flagParseError = false;
		}
	}
	
	
	// Méthodes abstraites
	
	protected abstract String format( T object ); 
	
	protected abstract T parse( String string ) throws Exception; 
	
}
