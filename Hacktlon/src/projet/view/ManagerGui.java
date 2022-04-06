package projet.view;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import jfox.javafx.view.ManagerGuiAbstract;
import jfox.javafx.view.View;


public class ManagerGui extends ManagerGuiAbstract {

	
	// Actions

	@Override
	public void configureStage(){
		
		//display the first scene after the load of the project
		showView( EnumView.Connexion);
		
		//configuration of the stage
		stage.setTitle("HackatlonApp");
		stage.setWidth(600);
		stage.setHeight(440);
		stage.setMinWidth(340);
		stage.setMinHeight(300);
		stage.getIcons().add(new Image("images/logo.png"));
		
		//default configuration of DialogScene
		typeConfigDialogDefault = ConfigDialog.class;
	}

	
	@Override
	public Scene createScene( View view ) {
		BorderPane paneMenu = new BorderPane(view.getRoot());
		paneMenu.setTop( (Node) factoryController.call( MenuBarAppli.class ) );
		Scene scene = new Scene( paneMenu );
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		return scene;
	}
	
}