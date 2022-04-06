package jfox.javafx.view;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.WritableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;
import jfox.exception.ExceptionAnomaly;
import jfox.exception.ExceptionPermission;
import jfox.exception.ExceptionValidation;
import jfox.javafx.util.UtilFX;
import jfox.localization.BundleMessages;


public abstract class ManagerGuiAbstract implements IManagerGui, IFactoryScene {

	// Logger
	private static final Logger logger = Logger.getLogger(ManagerGuiAbstract.class.getName());

	// Constants
	private static final KeyCodeCombination KCC_ENTER = new KeyCodeCombination(KeyCode.ENTER);
	private static final KeyCodeCombination KCC_ESCAPE = new KeyCodeCombination(KeyCode.ESCAPE);

	// Champs

	protected Stage stage;
	private IFactoryScene factoryScene = this;
	private View viewPrevious;
	private View viewActive;

	private boolean flagNewStage = true;
	private boolean flagShowErrorInProgress = false;;

	protected Callback<Class<?>, Object> factoryController;
	protected Class<? extends IConfigDialog> typeConfigDialogDefault = ConfigDialogDefault.class;

	protected List<Class<? extends Exception>> listExceptionValidation = new ArrayList<>();
	protected List<Class<? extends Exception>> listExceptionPermission = new ArrayList<>();
	protected List<Class<? extends Exception>> listExceptionAnomaly = new ArrayList<>();


	// Constructeur

	public ManagerGuiAbstract() {

		addExceptionValidation(ExceptionValidation.class);
		addExceptionPermission(ExceptionPermission.class);
		addExceptionAnomaly(ExceptionAnomaly.class);

		// Traitement par défaut des exceptions
		Thread.setDefaultUncaughtExceptionHandler(this::handleUncaughtException);
		Thread.currentThread().setUncaughtExceptionHandler(this::handleUncaughtException);
	}

	// Getters & setters

	@Override
	public final Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setFactoryController(Callback<Class<?>, Object> factoryController) {
		this.factoryController = factoryController;
	}

	// Actions

	// Affichage des vues

	@Override
	public void showView(IEnumView idView) {

		View view = idView.getView();
		Scene scene = view.getScene();
		Controller controller = view.getController();
		boolean flagNewScene = false;

		// Charge le panneau si nécessaire
		if ( scene == null ) {

			flagNewScene = true;

			try {
				// Charge le panneau
				FXMLLoader loader = new FXMLLoader(idView.getClass().getResource(view.getPath()));
				loader.setControllerFactory(factoryController);
				view.setRoot( loader.load() );

				// Enregistre le controller dans la vue
				scene = factoryScene.createScene( view );
				controller = loader.getController();
				view.setScene(scene);
				view.setController(controller);

			} catch (IOException e) {
				throw UtilFX.runtimeException(e);
			}
		}

		// Exécute la méthode refresh() du controlleur
		if (controller != null) {
			controller.refresh();
		}

		stage.setScene(scene);
		viewPrevious = viewActive;
		viewActive = view;
		boolean flagClosePrviousView = !flagNewStage;
		flagNewStage = false;

		// Réinitialise la forme du pointeur si besoin
		scene.setCursor(null);
		scene.setCursor(Cursor.DEFAULT);
		scene.getRoot().setCursor(Cursor.DISAPPEAR);

		if (flagNewScene) {
			delayAccelerators( view );
		}

		if (flagClosePrviousView) {
			closeViewObjects( viewPrevious );
		}

		// Force le réaffichage en cas d'utilisation de la mise à l'échelle
		refreshStage();
	}
	
	private void delayAccelerators( View view ) {
		
		final Scene scene = view.getScene();
		final Controller controller = view.getController();
		
		// Retarde les actions sur les boutons par défaut pour éviter
		// la propagation d'évènements à la vue suivante
		Platform.runLater( () -> {
			Runnable accEnter = scene.getAccelerators().get(KCC_ENTER);
			if (accEnter != null) {
				scene.getAccelerators().put(KCC_ENTER, () -> {
					if ( controller != null ) {
						controller.checkParseError();
					}
					Platform.runLater( () -> {
						accEnter.run();
					});
				});
			}
			Runnable accEscape = scene.getAccelerators().get(KCC_ESCAPE);
			if (accEscape != null) {
				scene.getAccelerators().put(KCC_ESCAPE, () -> {
					if ( controller != null ) {
						controller.cancelParseError();
					}
					Platform.runLater( () -> {
						accEscape.run();
					});
				});
				if ( controller != null ) {
					searchButtonCancel( scene.getRoot(), controller );
				}
			}
		});
	}

	
	private boolean searchButtonCancel( Parent root, Controller controller ) {
		for ( Node node : root.getChildrenUnmodifiable() ) {
			if ( node instanceof Button ) {
				if ( ((Button) node).isCancelButton() ) {
					node.focusedProperty().addListener( (obs, oldVal, newVal ) -> {
						if ( newVal ) {
							controller.cancelParseError();
						}
					});			
					return true;
				}
			} else if ( node instanceof Parent ) {
				if ( searchButtonCancel( (Parent) node, controller ) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	private void closeViewObjects( View view ) {
		
		// Libère les ressource de la vue si flagReuse == false
		if ( ! view.isFlagReuse()  ) {
			for ( Object o : view.getObjectsToClose() ) {
				try {
					Method method = o.getClass().getMethod("close");
					method.invoke( o );
				} catch (NoSuchMethodException e) {
				} catch (Exception e) {
					showDialogError(e);
				}
			}
			view.setScene(null);
			view.setController(null);
			view.getObjectsToClose().clear();
		}
	}
	
	private void refreshStage() {

		double height = stage.getHeight();

		if ( Screen.getPrimary().getOutputScaleX() > 1. && ! Double.isNaN( height ) ) {
			
			if ( stage.isMaximized() ) {

				stage.setFullScreen(true);
				stage.setFullScreen(false);
				
			} else {
				
		    	WritableValue<Double> writableHeight = new WritableValue<Double>() {
		    	    @Override
		    	    public Double getValue() {
		    	        return stage.getHeight();
		    	    }
		    	    @Override
		    	    public void setValue(Double value) {
		    	        stage.setHeight(value);
		    	    }
		    	};	
		    	
		    	Timeline timeline = new Timeline();
			    timeline.getKeyFrames().addAll(
		    	        new KeyFrame( new Duration(20), 
	        	            new KeyValue( writableHeight, height )
		    	        )
		    	    );
		    	stage.setHeight( height + 0.001 );
		    	timeline.play();    	
				
			}
		}
	}

	
	@Override
	public void showDialog(IEnumView idView) {
		showDialog( idView, typeConfigDialogDefault );
	}

	@Override
	public void showDialog(IEnumView idView, Class<? extends IConfigDialog> typeConfigDialog) {

		IConfigDialog configDialog;
		IFactoryScene factoryPrevious;
		View viewPrevious;
		try {
			configDialog = typeConfigDialog.getConstructor().newInstance();
			factoryPrevious = factoryScene;
			viewPrevious = viewActive;
			factoryScene = configDialog;
		} catch (Exception e) {
			throw UtilFX.runtimeException(e);
		}

		flagNewStage = true;
		Stage stagePrevious = stage;
		stage = new Stage();

		stage.initOwner(stagePrevious);
		stage.setTitle(stagePrevious.getTitle());
		stage.getIcons().addAll(stagePrevious.getIcons());
		stage.initModality(Modality.APPLICATION_MODAL);

		try {
			showView(idView);
			configDialog.configureStage(stage);
		} catch (Exception e) {
			returnToPreviousStage(factoryPrevious, viewPrevious);
			throw UtilFX.runtimeException(e);
		}

		EventHandler<WindowEvent> handlerOnShown = (event) -> {
			delayAccelerators( idView.getView() );
			stage.setOnShowing(null);
		};
		stage.setOnShown(handlerOnShown);
		
		stage.showAndWait();
		returnToPreviousStage(factoryPrevious, viewPrevious);
	}

	private void returnToPreviousStage(IFactoryScene factoryPrevious, View viewPrevious) {
		closeViewObjects( viewActive );
		factoryScene = factoryPrevious;
		viewActive = viewPrevious;
		stage = (Stage) stage.getOwner();
	}

	
	@Override
	public void closeDialog() {
		if ( stage.getOwner() != null ) {
			stage.close();
		}
	}

	@Override
	public void exit() {
		Platform.exit();
	}

	
	// Actions générales

	@Override
	public void execTask(RunnableWithException runnable) {

		final EventHandler<InputEvent> inputEventConsumer = (event) -> event.consume();
		stage.addEventFilter(InputEvent.ANY, inputEventConsumer);

		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() throws Exception {
				stage.getScene().setCursor(Cursor.WAIT);
				stage.getScene().getRoot().setCursor(null);
				runnable.run();
				return null;
			}
		};

		EventHandler<WorkerStateEvent> handlerEndOfTask = (event) -> {
			stage.removeEventFilter(InputEvent.ANY, inputEventConsumer);
			stage.getScene().setCursor(Cursor.DEFAULT);
			if (task.getException() != null) {
				showDialogError(task.getException());
			}
		};
		task.setOnCancelled(handlerEndOfTask);
		task.setOnFailed(handlerEndOfTask);
		task.setOnSucceeded(handlerEndOfTask);

		Thread thread = new Thread(task);
		thread.start();
	}

	@Override
	public void showDialogMessage(String message) {
		final Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.initOwner(stage);
		alert.setHeaderText(message);
		alert.showAndWait();
	}

	@Override
	public boolean showDialogConfirm(String message) {
		final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.initOwner(stage);
		alert.setHeaderText(message);
		final Optional<ButtonType> result = alert.showAndWait();
		return result.get() == ButtonType.OK;
	}

	@Override
	public void showDialogError( Throwable e ) {

		if (flagShowErrorInProgress) {
			return;
		}

		String message = BundleMessages.getString( "error.failure" );

		if (e != null) {

			e = UtilFX.unwrapException(e);

			boolean flagKnownType = false;

			if (!flagKnownType) {
				for (var type : listExceptionValidation) {
					if (type.isAssignableFrom(e.getClass())) {
						message = e.getMessage();
						flagKnownType = true;
						break;
					}
				}
			}
			if (!flagKnownType) {
				for (var type : listExceptionPermission) {
					if (type.isAssignableFrom(e.getClass())) {
						message = BundleMessages.getString( "error.permission" );
						logger.log(Level.FINEST, message, e);
						flagKnownType = true;
						break;
					}
				}
			}
			if (!flagKnownType) {
				for (var type : listExceptionAnomaly) {
					if (type.isAssignableFrom(e.getClass())) {
						if ( e.getCause() == null ) {
							logger.log(Level.SEVERE, e.getMessage(), e);
						} else {
							logger.log(Level.FINE, "", e);
						}
						flagKnownType = true;
						break;
					}
				}
			}
			if (!flagKnownType) {
				logger.log(Level.SEVERE, "", e);
			}
		}
		showDialogError(message);
	}

	@Override
	public void showDialogError(String message) {
		final Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.initOwner(stage);
		alert.setHeaderText(message);
		flagShowErrorInProgress = true;
		alert.showAndWait();
		flagShowErrorInProgress = false;
	}

	
	// Méthodes de configuraiton

	public abstract void configureStage() throws IOException;

	protected void addExceptionAnomaly(Class<? extends Exception> type) {
		listExceptionAnomaly.add(type);
	}

	protected void addExceptionPermission(Class<? extends Exception> type) {
		listExceptionPermission.add(type);
	}

	protected void addExceptionValidation(Class<? extends Exception> type) {
		listExceptionValidation.add(type);
	}

	
	// Méthodes auxiliaires

	
	private void handleUncaughtException(Thread t, Throwable e) {
		showDialogError(e);
	}

}
