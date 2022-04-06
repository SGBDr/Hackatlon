package jfox.javafx.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;


public class ConverterLocalDate extends Converter<LocalDate> {

	
	// Champs
	
	protected DateTimeFormatter	formatterOut = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
	protected DateTimeFormatter	formatterIn;
	
	
	// Constructeur

	public ConverterLocalDate( String  patternOut, String patternIn, String message ) {
		super(message);
		if ( patternOut != null ) {
			formatterOut = DateTimeFormatter.ofPattern( patternOut );  
		}
		if ( patternIn == null ) {
			if ( patternOut == null ) {
				patternIn = formatterOut.format( LocalDate.of( 2025, 11, 22 ) );
				patternIn = patternIn.replace("2025", "yyyy").replace("25", "yy").replace("11", "M").replace("22", "d");
			} else {
				patternIn = patternOut.replace( "MM", "M" ).replace( "dd", "d" );
			}
		}
		formatterIn = DateTimeFormatter.ofPattern( patternIn );
	}

	public ConverterLocalDate( String  pattern, String message ) {
		this( pattern, null, message );
	}

	public ConverterLocalDate( String  pattern ) {
		this( pattern, null, null );
	}

	public ConverterLocalDate() {
		this( null, null, null );
	}
	
	
	// Actons
	
	@Override
	protected String format(LocalDate object) {
		return formatterOut.format( object );
	}
	
	@Override
	protected LocalDate parse(String string) {
		return  LocalDate.from( formatterIn.parse( string ) ) ;
	}

}

