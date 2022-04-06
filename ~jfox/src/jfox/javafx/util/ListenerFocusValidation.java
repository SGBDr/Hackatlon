package jfox.javafx.util;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextInputControl;


public class ListenerFocusValidation implements ChangeListener<Boolean> {

	
	// Champs
	
	private Converter<?>	converter;
	private StringProperty	textProperty;

	
	// Constructeur
	
	public ListenerFocusValidation( Converter<?> converter ) {
		this.converter = converter;
	}
	
	public ListenerFocusValidation() { 
		this( null );
	}
	
	
	// Acton
	
	@SuppressWarnings("unchecked")
	@Override
	public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		
		if ( newValue ) { // Si obtention du focus
			
			if ( textProperty == null && observable instanceof ReadOnlyProperty ) {
				Object bean = ( (ReadOnlyProperty<Boolean>) observable ).getBean();
				if ( bean instanceof Node ) {
					converter.setNode( (Node) bean );
					if ( bean instanceof TextInputControl ) {
						textProperty = ( (TextInputControl) bean ).textProperty();
					} else if ( bean instanceof ComboBox<?>) {
						textProperty = ( (ComboBox<?>) bean ).getEditor().textProperty();
					} else if ( bean instanceof DatePicker ) {
						textProperty = ( (DatePicker) bean ).getEditor().textProperty();
					}
				}
			}
			
		} else { // Si perte du focus

			Platform.runLater( () -> {
				
				// Teste la validité de la saisie
				converter.checkParseError();

				// Mise à jour forée pour faire un affichage formaté
				if ( textProperty != null ) {
					textProperty.setValue( converter.getOutputText() );
				}
			});
		
		}
	}
}

