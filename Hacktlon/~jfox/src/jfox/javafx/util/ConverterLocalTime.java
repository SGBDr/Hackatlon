package jfox.javafx.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;


public class ConverterLocalTime extends Converter<LocalTime> {

	
	// Champs
	
	protected DateTimeFormatter	formatterOut = DateTimeFormatter.ofLocalizedTime( FormatStyle.SHORT );
	protected DateTimeFormatter	formatterIn;

	
	
	// Constructeur

	public ConverterLocalTime( String  patternOut, String patternIn, String message ) {
		super(message);
		if ( patternOut != null ) {
			formatterOut = DateTimeFormatter.ofPattern( patternOut );
		}
		if ( patternIn == null ) {
			if ( patternOut == null ) {
				patternIn = formatterOut.format( LocalTime.of( 11,22, 33 ));
				patternIn = patternIn.replace("11", "H").replace("22", "m").replace("33", "s");
			} else {
				patternIn = patternOut.replace( "HH", "H" ).replace( "mm", "m" ).replace( "ss", "s" );
			}
		}
		formatterIn = DateTimeFormatter.ofPattern( patternIn );
	}
	

	public ConverterLocalTime( String  pattern, String message ) {
		this( pattern, null, message );
	}

	public ConverterLocalTime( String  pattern ) {
		this( pattern, null, null );
	}

	public ConverterLocalTime() {
		this( null, null, null );
	}
	
	
	// Actons
	
	@Override
	protected String format(LocalTime object) {
		return formatterOut.format( object );
	}
	
	@Override
	protected LocalTime parse(String string) {
		return  LocalTime.from( formatterIn.parse( string ) ) ;
	}

}

