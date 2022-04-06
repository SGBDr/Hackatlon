package projet.view.systeme;

import javax.inject.Inject;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import jfox.javafx.view.Controller;
import jfox.javafx.view.IManagerGui;
import projet.data.Compte;
import projet.view.EnumView;


public class ControllerConnexion extends Controller {
	
	@FXML
	private TextField txfPseudo;
	@FXML
	private PasswordField pwfMotDePasse;
	
	@Inject
	private IManagerGui managerGui;
	@Inject
	private ModelConnexion modelConnexion;
	@Inject
	private ModelInfo modelInfo;
	
	@FXML
	private void initialize() {
		Compte courant = modelConnexion.getCourant();
		// Data binding
		bindBidirectional(txfPseudo, courant.pseudoProperty());
		bindBidirectional(pwfMotDePasse, courant.motDePasseProperty());

	}
	
	
	@Override
	public void refresh() {
		// Ferem la session si elle est ouverte
		if ( modelConnexion.getCompteActif() != null ) {
			modelConnexion.fermerSessionUtilisateur();
		}
	}
	
	@FXML
	private void doConnexion() {
		managerGui.execTask(() -> {
			modelConnexion.ouvrirSessionUtilisateur();
			Platform.runLater( () -> {
         			modelInfo.titreProperty().setValue( "Bienvenue" );
        			modelInfo.messageProperty().setValue( "Connexion réussie" );
        			managerGui.showView(EnumView.Info);
            });
		} );
	}
	

}
