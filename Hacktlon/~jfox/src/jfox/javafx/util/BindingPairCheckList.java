package jfox.javafx.util;

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;
import javafx.util.Pair;
import javafx.util.StringConverter;


public class BindingPairCheckList<T> {
	
	
	// Champs
	
	private final ObservableList<Pair<T, BooleanProperty>> list = FXCollections.observableArrayList();
	
	
	// Constructeur
	
	public BindingPairCheckList( List<T> keys, ObservableList<T> observableList ) {

    	for ( T key : keys  ) {
    		list.add( new Pair<T, BooleanProperty>(key, new SimpleBooleanProperty() ) );
    	}

    	// De la liste vers courant
		for ( var pair : list) {
			pair.getValue().addListener((obs) -> {
				if (pair.getValue().get()) {
					if (!observableList.contains(pair.getKey())) {
						observableList.add(pair.getKey());
					}
				} else {
					observableList.remove(pair.getKey());
				}
			});
		}    		
    	
    	// De courant vers la liste
    	observableList.addListener(
        	(ListChangeListener<T>)	c -> {
    			c.next();
    			for ( var pair : list ) {
    				if ( c.getAddedSubList().contains( pair.getKey() ) ) {
    					pair.getValue().set( true );
    				}
    				if ( c.getRemoved().contains( pair.getKey() ) ) {
    					pair.getValue().set( false );
    				}
    			}
        });
	}

	
	public void configureListView( 
			ListView<Pair<T, BooleanProperty>> listView,
			Callback<Pair<T, BooleanProperty>, String> callback
	) {
		
		StringConverter<Pair<T, BooleanProperty>> converter = null;
		if ( callback != null ) {
	    	converter = new StringConverter<>() {
				@Override
				public String toString(Pair<T, BooleanProperty> pair) {
						return callback.call(pair);
				}
				@Override
				public Pair<T, BooleanProperty> fromString(String string) {
					return null;
				}
			};
			
		}
    	
		listView.setItems( list );
		
		listView.setCellFactory( CheckBoxListCell.forListView(
        		item -> item.getValue(),
        		converter
   		) );
	}

	
	public void configureListView( 
			ListView<Pair<T, BooleanProperty>> listView
	) {
		configureListView( listView, null );
	}
}
