package jfox.javafx.view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jfox.javafx.util.Converter;
import jfox.javafx.util.ConverterLocalDate;
import jfox.javafx.util.ListenerFocusValidation;


public abstract class Controller {

	
	// Champs
	
	private List<Converter<?>> converters =  new ArrayList<>();
	
	
	// Getters
	
	public List<Converter<?>> getConverters() {
		return converters;
	}
	
	
	// Refresh
	
	public void refresh() {
	}
	
	
	// Bind bidirectionnel simple
	
	protected void bindBidirectional( TextInputControl control, Property<String> property ) {
		control.textProperty().bindBidirectional( property );
	}
	
	protected void bindBidirectional( Labeled control, Property<String> property ) {
		control.textProperty().bindBidirectional( property );
	}
	
	protected void bindBidirectional( CheckBox control, Property<Boolean> property ) {
		control.selectedProperty().bindBidirectional( property );
	}
	
	protected void bindBidirectional( Toggle control, Property<Boolean> property ) {
		control.selectedProperty().bindBidirectional( property );
	}
	
	protected <T> void bindBidirectional( ChoiceBox<T> control, Property<T> property ) {
		control.valueProperty().bindBidirectional( property );
	}
	
	protected <T> void bindBidirectional( ComboBox<T> control, Property<T> property ) {
		control.valueProperty().bindBidirectional( property );
	}
	
	protected void bindBidirectional( DatePicker datePicker, Property<LocalDate> property ) {
		bindBidirectional(datePicker, property, new ConverterLocalDate() );
	}
	
	protected void bindBidirectional( ImageView imageView, Property<Image> property ) {
		imageView.imageProperty().bindBidirectional( property );
	}
	
	
	// Bind bidirectionnel avec contrôle de la valeur saisie
	
	protected <T> void bindBidirectional( TextInputControl control, Property<T> property, Converter<T> converter ) {
		control.textProperty().bindBidirectional( property, converter );
		if ( converter != null ) {
			control.focusedProperty().addListener( new ListenerFocusValidation( converter ));
			converters.add(converter);
		}
	}
	
	protected <T> void bindBidirectional( Labeled control, Property<T> property, Converter<T> converter ) {
		control.textProperty().bindBidirectional( property, converter );
		if ( converter != null ) {
			control.focusedProperty().addListener( new ListenerFocusValidation( converter ));
			converters.add(converter);
		}
	}
	
	
	protected <T> void bindBidirectional( ChoiceBox<T> choiceBox, Property<T> property, Converter<T> converter ) {
		choiceBox.valueProperty().bindBidirectional( property );
		if ( converter != null ) {
			choiceBox.setConverter( converter );
			converters.add(converter);
		}
	}
	
	
	protected <T> void bindBidirectional( ComboBox<T> comboBox, Property<T> property, Converter<T> converter ) {
		comboBox.valueProperty().bindBidirectional( property );
		if ( converter != null ) {
			comboBox.setConverter( converter );
			comboBox.getEditor().textProperty().addListener( obs -> {
				converter.fromString(comboBox.getEditor().getText());
				if (  ! converter.hasParseError() ) {
					comboBox.commitValue();
				}
		    });
			comboBox.getEditor().focusedProperty().addListener( new ListenerFocusValidation( converter ));
			converters.add(converter);
		}
	}
	
	protected <T> void bindBidirectional( DatePicker datePicker, Property<LocalDate> property, Converter<LocalDate> converter ) {
		datePicker.valueProperty().bindBidirectional( property );
		if ( converter != null ) {
			datePicker.setConverter( converter );
			datePicker.getEditor().textProperty().addListener( obs -> {
				LocalDate date = converter.fromString(datePicker.getEditor().getText());
				if (  ! converter.hasParseError() ) {
					datePicker.setValue( date );
				}
		    });
			datePicker.focusedProperty().addListener( new ListenerFocusValidation( converter ));
			converters.add(converter);
		}
	}

	
	// Bind bidirectionnel pour ToggleGroup

	@SuppressWarnings("unchecked")
	protected <T> void bindBidirectional( final ToggleGroup tgg, final Property<T> property, T...userData ) {

		for ( int i=0; i < userData.length && i < tgg.getToggles().size(); ++i ) {
			tgg.getToggles().get(i).setUserData( userData[i] ) ;
		}
		
		final var busy = new SimpleBooleanProperty( false );
		
		tgg.selectedToggleProperty().addListener( obs -> {
			if ( ! busy.getValue() ) {
				busy.setValue(true);
				// Modifie le statut en fonction du bouton radio sélectionné
				property.setValue( (T) tgg.getSelectedToggle().getUserData() );
				busy.setValue(false);
			}
		});
		
		InvalidationListener listenerProperty = obs -> {
			if ( ! busy.getValue() ) {
				busy.setValue(true);
				// Déselectionne le bouton sélectionné s'il y en a un
				var selected = tgg.getSelectedToggle();
				if ( selected != null ) {
					selected.setSelected(false);
				}
				// Sélectionne le bouton radio correspondant au statut
				for ( Toggle t : tgg.getToggles() ) {
					if ( t.getUserData().equals( property.getValue()  )) {
						t.setSelected(true);
						break;
					}
				}
				busy.setValue(false);
			}
		};
		
		property.addListener( listenerProperty );
		listenerProperty.invalidated( property );
	}
	
	
	
	// Prise en compte des erreurs dans les convertisseurs
	
	public void cancelParseError() {
		for ( Converter<?> converter : converters ) {
			converter.cancelParseError();
		}
	}
	
	public void checkParseError() {
		for ( Converter<?> converter : converters ) {
			converter.checkParseError();
		}
	}
	
}
