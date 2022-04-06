package projet.view.systeme;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import jfox.exception.ExceptionValidation;
import projet.dao.DaoCompte;
import projet.data.Compte;


public class ModelConnexion {
	
	public static final Logger logger = Logger.getLogger(ModelConnexion.class.getName());
	

	private final Compte courant = new Compte();

	
	private final Property<Compte> compteActif = new SimpleObjectProperty<>();
	
	@Inject
	private DaoCompte	daoCompte;
	
	public Compte getCourant() {
		return courant;
	}
	
	public Property<Compte> compteActifProperty() {
		return compteActif;
	}
	
	public Compte getCompteActif() {
		return compteActif.getValue();
	}
	
	@PostConstruct
	public void init() {
		courant.setPseudo( "init" );
		courant.setMotDePasse( "init" );
	}

	public void ouvrirSessionUtilisateur() {
		Compte compte = daoCompte.validerAuthentification(courant.pseudoProperty().getValue(), courant.motDePasseProperty().getValue());
		if( compte == null ) {
			throw new ExceptionValidation( "Pseudo ou mot de passe invalide." );
		} else {
			Platform.runLater( () -> compteActif.setValue( compte ) );
		}
	}

	public void fermerSessionUtilisateur() {
		compteActif.setValue( null );
	}

}
