package jfox.javafx.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;


public class ConverterLocalDateTime extends Converter<LocalDateTime> {

	
	// Champs
	
	protected DateTimeFormatter	formatterOut = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
	protected DateTimeFormatter	formatterIn;
	
	
	// Constructeur

	public ConverterLocalDateTime( String  patternOut, String patternIn, String message ) {
		super(message);
		if ( patternOut != null ) {
			formatterOut = DateTimeFormatter.ofPattern( patternOut );  
		}
		if ( patternIn == null ) {
			if ( patternOut == null ) {
				patternIn = formatterOut.format( LocalDateTime.of( 2025, 12, 27, 11, 22, 33 ) );
				patternIn = patternIn.replace("2025", "yyyy").replace("25", "yy").replace("12", "M").replace("27", "d");
				patternIn = patternIn.replace("11", "H").replace("22", "m").replace("33", "s");
			} else {
				patternIn = patternOut.replace( "MM", "M" ).replace( "dd", "d" );
				patternIn = patternIn.replace( "HH", "H" ).replace( "mm", "m" ).replace( "ss", "s" );
			}
		}
		formatterIn = DateTimeFormatter.ofPattern( patternIn );
	}

	public ConverterLocalDateTime( String  pattern, String message ) {
		this( pattern, null, message );
	}

	public ConverterLocalDateTime( String  pattern ) {
		this( pattern, null, null );
	}

	public ConverterLocalDateTime() {
		this( null, null, null );
	}
	
	
	// Actons
	
	@Override
	protected String format(LocalDateTime object) {
		return formatterOut.format( object );
	}
	
	@Override
	protected LocalDateTime parse(String string) {
		return  LocalDateTime.from( formatterIn.parse( string ) ) ;
	}

}

