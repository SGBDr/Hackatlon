package projet.view.systeme;

import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import jfox.javafx.view.Controller;


public class ControllerInfo extends Controller {
	
	@FXML
	private Label		lblTitre;
	@FXML
	private Label		lblMessage;
	
	@Inject
	private ModelInfo	modelInfo;
	
	@FXML
	private void initialize() {
		// Data binding
		bindBidirectional( lblTitre, modelInfo.titreProperty() );
		bindBidirectional( lblMessage, modelInfo.messageProperty() );
		
	}
	

}
