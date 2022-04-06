package jfox.javafx.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;


public class ConverterBigDecimal extends Converter<BigDecimal> {

	
	// Champs
	
	private NumberFormat	format = NumberFormat.getInstance();
	
	
	// Constructeur
	
	public ConverterBigDecimal( Locale locale, String pattern, String message ) {
		super(message);
		if ( locale != null ) {
			format = NumberFormat.getInstance( locale );
		}
		if ( format instanceof DecimalFormat && pattern != null ) {
			( (DecimalFormat) format ).applyPattern(pattern);
		}
	}

	public ConverterBigDecimal( Locale locale, String pattern ) {
		this( locale, pattern, null );
	}

	public ConverterBigDecimal( Locale locale ) {
		this( locale, null, null );
	}

	public ConverterBigDecimal( String pattern, String message ) {
		this( null, pattern, message );
	}

	public ConverterBigDecimal( String pattern ) {
		this( null, pattern, null );
	}

	public ConverterBigDecimal() {
		this( null, null, null );
	}
	
	
	// Actons
	
	@Override
	protected String format(BigDecimal object) {
		return format.format( object );
	}
	
	@Override
	protected BigDecimal parse(String string) throws ParseException {
		return new BigDecimal( format.parse( string ).toString() ).setScale( format.getMaximumFractionDigits(), RoundingMode.HALF_UP );
	}
}
