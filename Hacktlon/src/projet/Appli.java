package projet;

import javax.sql.DataSource;

import org.mapstruct.factory.Mappers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import jfox.context.ContextGlobal;
import jfox.context.IContext;
import jfox.javafx.util.Logger;
import jfox.javafx.util.UtilFX;
import jfox.jdbc.DataSourceSingleConnection;
import projet.commun.IMapper;
import projet.view.ManagerGui;


public class Appli extends Application {
	private IContext context;
	
	@Override
	public final void start(Stage stagePrimary) {
		try {
			//JDBC - DataSource (initialize the connection to the data base)
			DataSource dataSource = new DataSourceSingleConnection( "jdbc.properties" );
			//get the Context
			context = new ContextGlobal();
			context.addBean( dataSource );
			context.addBean( Mappers.getMapper( IMapper.class ) );

			//initialize ManagerGui
	    	ManagerGui managerGui = context.getBean( ManagerGui.class );
	    	managerGui.setFactoryController( context::getBeanNew );
			managerGui.setStage( stagePrimary );
			managerGui.configureStage();
			
			//display the stage
			stagePrimary.show();
		} catch(Exception e) {
			UtilFX.unwrapException(e).printStackTrace();
	        Alert alert = new Alert(AlertType.ERROR);
	        alert.setHeaderText( "Impossible de démarrer l'application." );
	        alert.showAndWait();
	        Platform.exit();
		}
	}
	
	@Override
	public final void stop() throws Exception {
		if (context != null ) {
			context.close();
			Logger.log( ManagerGui.class );
		}
	}

	//class contain main method, to start the project
	public static class MainContacts {
		public static void main(String[] args) {
			Application.launch( Appli.class, args);
		}
	}

}
