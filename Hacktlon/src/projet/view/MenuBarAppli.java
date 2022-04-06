package projet.view;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import jfox.javafx.view.IManagerGui;
import projet.commun.Roles;
import projet.data.Compte;
import projet.view.systeme.ModelConnexion;


public class MenuBarAppli extends MenuBar {
	
	private Menu	menuDonnees;
	
	private MenuItem itemDeconnecter;

	/*private MenuItem itemCategories;
	private MenuItem itemComptes;*/
	
	@Inject
	private IManagerGui 	managerGui;
	@Inject
	private ModelConnexion	modelConnexion;	
	
	
	@PostConstruct
	public void init() {

		Menu menu;
		MenuItem item;
		
		
		//Menu System
		menu =  new Menu("Système");
		this.getMenus().add(menu);
		
		item = new MenuItem( "Se déconnecter" );
		item.setOnAction(e -> managerGui.showView(EnumView.Connexion));
		menu.getItems().add( item );
		itemDeconnecter = item;
		
		item = new MenuItem( "Quitter" );
		item.setOnAction(  e -> managerGui.exit()  );
		menu.getItems().add( item );

		
		// Manu Données
		
		menu =  new Menu( "Données" );;
		this.getMenus().add(menu);
		menuDonnees = menu;
		
		/*item = new MenuItem( "Personnes" );
		item.setOnAction(  e -> managerGui.showView( EnumView.PersonneListe )  );
		menu.getItems().add( item );
		
		item = new MenuItem( "Memo" );
		item.setOnAction(  e -> managerGui.showView( EnumView.MemoListe )  );
		menu.getItems().add( item );
		
		item = new MenuItem( "Service" );
		item.setOnAction(  e -> managerGui.showView( EnumView.ServiceListe )  );
		menu.getItems().add( item );
		
		item = new MenuItem( "Catégories" );
		item.setOnAction(  e -> managerGui.showView( EnumView.CategorieListe )  );
		menu.getItems().add( item );
		itemCategories = item;
		
		item = new MenuItem( "Comptes" );
		item.setOnAction(  e -> managerGui.showView( EnumView.CompteListe )  );
		menu.getItems().add( item );
		itemComptes = item;

		
		// Manu Tests
		
		menu =  new Menu( "Tests" );;
		this.getMenus().add(menu);
		menuTests = menu;
		
		item = new MenuItem( "DaoCategorie" );
		item.setOnAction(  e -> managerGui.showView( EnumView.TestDaoCategorie )  );
		menu.getItems().add( item );
		item = new MenuItem( "DaoMemo" );
		item.setOnAction(  e -> managerGui.showView( EnumView.TestDaoMemo )  );
		menu.getItems().add( item );
		item = new MenuItem( "DaoService" );
		item.setOnAction(  e -> managerGui.showView( EnumView.TestDaoService )  );
		menu.getItems().add( item );*/


		// Configuration initiale du menu
		configurerMenu( modelConnexion.getCompteActif() );

		// Le changement du compte connecté modifie automatiquement le menu
		modelConnexion.compteActifProperty().addListener( (obs) -> {
					Platform.runLater( () -> configurerMenu( modelConnexion.getCompteActif() ) );
				}
			); 
		
	}

	
	//configure the menu bar according to the roles of the connection account
	private void configurerMenu( Compte compteActif  ) {
		
		/*itemDeconnecter.setDisable(true);
		
		menuDonnees.setVisible(false);
		itemCategories.setVisible(false);
		itemComptes.setVisible(false);
		menuTests.setVisible(false);*/
		
		//have to be update after the spring daily meeting
		if( compteActif != null ) {
			/*itemDeconnecter.setDisable(false);
			if( compteActif.isInRole( Roles.UTILISATEUR) ) {
				menuDonnees.setVisible(true);
			}
			if( compteActif.isInRole( Roles.ADMINISTRATEUR ) ) {
				menuDonnees.setVisible(true);
				itemCategories.setVisible(true);
				itemComptes.setVisible(true);
				menuTests.setVisible(true);
			}*/
		}
	}
	
}
