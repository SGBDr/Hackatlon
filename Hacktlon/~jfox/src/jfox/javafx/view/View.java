package jfox.javafx.view;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Parent;
import javafx.scene.Scene;


public class View {
	
	// Champs
	
	private String		path;
	private boolean		flagReuse;
	private Parent		root;
	private Controller	controller;
	private Scene		scene;
	private final List<Object>	objectsToClose = new ArrayList<>();

	
	// Constructeurs
	
	public View( String path, boolean flagReuse ) {
		this.path = path;
		this.flagReuse = flagReuse;
	}
	
	public View( String path ) {
		this( path, true );
	}

	
	// Getters & setters

	public String getPath() {
		return path;
	}

	public boolean isFlagReuse() {
		return flagReuse;
	}
	
	public Parent getRoot() {
		return root;
	}
	
	public void setRoot(Parent root) {
		this.root = root;
	}
	
	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		objectsToClose.remove(this.controller);
		this.controller = controller;
		addObjectToClose(controller);
	}
	
	public Scene getScene() {
		return scene;
	}
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public void addObjectToClose( Object obj ) {
		if ( obj != null ) {
			objectsToClose.add( obj );
		}
	}

	public List<Object> getObjectsToClose() {
		return objectsToClose;
	}

}
