package projet.view;


import javafx.scene.Scene;
import javafx.stage.Stage;
import jfox.javafx.view.IConfigDialog;
import jfox.javafx.view.View;


public class ConfigDialog implements IConfigDialog {
	@Override
	public void configureStage(Stage stage) {
		stage.sizeToScene();
		stage.setResizable( true );
		
	}


	@Override
	public Scene createScene( View view ) {
		Scene scene = new Scene(view.getRoot());
		scene.getStylesheets().add(this.getClass().getResource("application.css").toExternalForm());
		return scene;
	}

}
