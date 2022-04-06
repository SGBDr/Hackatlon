package jfox.javafx.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.LoadException;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;


public class UtilFX {
	

	// Constructeur privé pour empêcher d'd'instancier la classe
	private UtilFX() {
	}
	
	
	// Méthodes utilitaires
	
	
	// selectRow()
	
	public static <T> void selectRow( ListView<T> listView, T item  ) {
		int index = listView.getItems().indexOf( item );
		listView.getSelectionModel().clearSelection();
		if ( index >= 0 ) {
			listView.getSelectionModel().select( index );
			listView.getFocusModel().focus( index );
			listView.scrollTo( index );
		}
		listView.requestFocus();
	}
	
	public static <T> void selectRow( TableView<T> tableView, T item  ) {
		int index = tableView.getItems().indexOf( item );
		tableView.getSelectionModel().clearSelection();
		if ( index >= 0 ) {
			tableView.getSelectionModel().select( index );
			tableView.getFocusModel().focus( index );
			tableView.scrollTo( index );
		}
		tableView.requestFocus();
	}
	
	
	// findNext()
	
	public static <T> T findNext( ObservableList<T> liste, T courant ) {
		int index = liste.indexOf(courant) + 1;
		if ( liste.size() == 1 ) {
			return null;
		}
		if ( index == liste.size() ) {
			return liste.get( index - 2);
		}
		return liste.get(index);
	}
	
	
	// setCellFactory()

	public static <T> void setCellFactory( ListView<T> listView, Callback< T, String > extractor ) {
		listView.setCellFactory( param -> new ListCell<T>() {
		    @Override
		    protected void updateItem(T item, boolean empty) {
		        super.updateItem(item, empty);
		        if (empty || item == null ) {
		            setText(null);
		        } else {
		            setText( extractor.call( item ) );
		        }
		    }
		});	
	}

	public static <T> void setCellFactory( ComboBox<T> comboBox, Callback< T, String > extractor ) {
		comboBox.setCellFactory( param -> new ListCell<T>() {
		    @Override
		    protected void updateItem(T item, boolean empty) {
		        super.updateItem(item, empty);
		        if (empty || item == null ) {
		            setText(null);
		        } else {
		            setText( extractor.call( item ) );
		        }
		    }
		});	
		comboBox.setButtonCell( comboBox.getCellFactory().call(null) );
	}

	public static <S, T> void setCellFactory( TableColumn<S, T> column, Callback<T, String > extractor ) {
		column.setCellFactory( param -> new TableCell<S, T>() {
		    @Override
		    protected void updateItem(T item, boolean empty) {
		        super.updateItem(item, empty);
		        if (empty || item == null ) {
		            setText(null);
		        } else {
		            setText( extractor.call( item ) );
		        }
		    }
		});	
	}
	
	public static <S, T> void setCellFactory( ListView<T> listView, String propertyName ) {
		setCellFactory( listView, new Extractor<>(propertyName)  );
	}
	
	public static <S, T> void setCellFactory( ComboBox<T> comboBox, String propertyName ) {
		setCellFactory( comboBox, new Extractor<>(propertyName)  );
	}
	
	public static <S, T> void setCellFactory( TableColumn<S, T> column, String propertyName ) {
		setCellFactory( column, new Extractor<>(propertyName)  );
	}
	
	
	
	// setCellValueFactory()

	public static <S, T> void setValueFactory( TableColumn<S, T> column, Callback<TableColumn.CellDataFeatures<S,T>, ObservableValue<T>>  extractor ) {
		column.setCellValueFactory( extractor );
	}

	public static <S, T> void setValueFactory( TableColumn<S, T> column, String propertyName ) {
		column.setCellValueFactory( new PropertyValueFactory<S, T>( propertyName ));
	}
	
	
	// objectToString()
	
	public static String objectToString( Object object ) {

		StringBuilder sbResult = new StringBuilder();
		boolean flagFirst = true;
		
		for ( Field field : object.getClass().getDeclaredFields() ) {
			if ( flagFirst ) {
				flagFirst = false;
			} else {
				sbResult.append( ", " );
			}
			try {
				field.setAccessible(true);
				Object bean = field.get( object );
				if ( bean instanceof Property<?> ) {
					bean = ((Property<?>) bean).getValue();
				}
				if ( bean == null ) {
					sbResult.append( "null" );
				} else {
					sbResult.append( bean.toString() );
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return sbResult.toString();
	}
	
	
	// Gestion des exceptions
	
	public static RuntimeException runtimeException( Throwable throwable ) {

		throwable = unwrapException( throwable );
		
		if ( throwable instanceof RuntimeException ) {
			return (RuntimeException) throwable;
		} else {
			return new RuntimeException(throwable);
		}
	}
	
	
	public static Throwable unwrapException( Throwable throwable ) {
		
		// Suprimer les exceptions enveloppes non significatives
		if ( throwable.getCause() instanceof InvocationTargetException) {
			throwable = throwable.getCause().getCause();
		} else 	if ( throwable instanceof InvocationTargetException) {
			throwable = throwable.getCause();
		} 
		
		if ( throwable.getClass() == RuntimeException.class && throwable.getCause() != null ) {
			throwable = throwable.getCause();
		}
		if ( throwable.getClass() == LoadException.class && throwable.getCause() != null ) {
			throwable = throwable.getCause();
		}
		
		return throwable;
	}
	
	
	public static Throwable getFirstCause( Throwable throwable ) {
		while ( true ) {
			if ( throwable.getCause() == null ) {
				return throwable;
			} else {
				throwable = throwable.getCause();
			}
		}
	}
	
    
    // Sélectionne l'onglet qui contien un composant donné
    public static void selectTab( Node node ) {
    	Parent parent = node.getParent();
    	if ( parent == null ) {
    		return;
    	}
		parent = parent.getParent();
    	Node content = node;
    	while( parent != null ) {
    		if ( parent instanceof TabPane ) {
    			TabPane tabPane = (TabPane) parent;
    			for ( Tab tab : tabPane.getTabs()  ) {
    				if ( tab.getContent() == content ) {
    					tabPane.getSelectionModel().select(tab);
    				}
    			}
    			return;
    		}
    		content = content.getParent();
    		parent = parent.getParent();
    	} 
    }
	
}
