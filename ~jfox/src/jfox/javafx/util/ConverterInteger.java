package jfox.javafx.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;


public class ConverterInteger extends Converter<Integer>  {

	
	// Champs
	
	private NumberFormat	format = NumberFormat.getInstance();
	
	
	// Constructeur
	
	public ConverterInteger( Locale locale, String pattern, String message  ) {
		super( message );
		if ( locale != null ) {
			format = NumberFormat.getInstance( locale );
		}
		if ( format instanceof DecimalFormat && pattern != null ) {
			( (DecimalFormat) format ).applyPattern(pattern);
		}
	}

	public ConverterInteger( String pattern, String message ) {
		this( null, pattern, message );
	}

	public ConverterInteger( String pattern ) {
		this( null, pattern, null );
	}

	public ConverterInteger() {
		this( null, null, null );
	}
	
	
	// Actons
	
	@Override
	protected String format(Integer object) {
		return format.format( object );
	}
	
	@Override
	protected Integer parse(String string) throws ParseException {
		return format.parse( string ).intValue();
	}
	
}
