package jfox.context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Inject;


public abstract class ContextAbstract implements IContext {
	
	
	// Champs
	
	protected final List<Object>	beans = new ArrayList<>();
	
	protected ContextAbstract 		mainContext = this;
	
	
	// Setters
	
	@Override
	public void setMainContext(ContextAbstract mainContext) {
		this.mainContext = mainContext;
	}
	
	
	// Actions
	
	@Override
	public final <T> T getBean( Class<T> type ) {
		return getBean( type, true );
	}
	
	
	@Override
	public final <T> T getBeanNew( Class<T> type ) {
		return getBean( type, false );
	}
	
	
	@SuppressWarnings( "unchecked" )
	public final <T> T getBean( Class<T> type, boolean flagSingleton ) {

		
		T bean = null;
		
		if ( flagSingleton ) {
			// Recherche dans la liste
			for ( Object item : beans ) {
				if ( type.isAssignableFrom( item.getClass() ) ) {
					if ( bean != null ) {
						throw new RuntimeException( "Several beans found for the type : " + type.getName() );
					}
					bean = (T) item;
				}
			}
			if ( bean != null ) {
				return bean;
			}
		}
		
		
		// Si pas trouvé dans la liste
		// détermine le type de l'objet à instancier
		
		try {
			
			// Instancie le bean et l'initialise 
			// (ou le récupère s'il existe déjà dans un autre contexte)
			bean = makeBean(type, flagSingleton);

			if (bean == null && mainContext == this ) {
				throw new RuntimeException( "Bean not found : " + type.getName() );
			}
			return bean;
			
		} catch ( Exception e) {
			if ( bean != null ) {
				beans.remove(bean);
			}
			throw runtimeException(e);
		}
	}
	
	
	@Override
	public final void addBean( Object bean ) {
		beans.add(bean);
	}
	
	
	@Override
	public void close() {
		
		Object bean;
		
		for( int i = beans.size()-1; i >= 0; --i ) {
			
			bean = beans.get(i);

			try {
				// Exécute la méthode de libération des ressources du bean
				if ( bean instanceof AutoCloseable ) {
					((AutoCloseable) bean).close();
					continue;
				}
				for ( Method method : bean.getClass().getMethods() ) {
					if ( method.isAnnotationPresent( PreDestroy.class ) )  {
						method.setAccessible(true);
						method.invoke( bean );						
					}
				}
			} catch ( Exception e ) {
				throw runtimeException(e);
			}
			
		}

	}
	
	
	// Méthodes auxiliaires

	protected abstract <T> T makeBean( Class<T> type, boolean flagSingleton ) throws Exception;

	protected <T> T initBean( Class<T> typeImpl, boolean flagSingleton )  {
		
		if ( mainContext != this ) {
			return mainContext.initBean( typeImpl, flagSingleton );
		}

		T bean = null;
		
		try {
		
			// Instancie le bean et l'ajoute à la liste
			bean = typeImpl.getConstructor().newInstance() ;
			if ( flagSingleton ) {
				beans.add(bean);
			}
	
			// Injecte les dépendances
			
			for( Field field : typeImpl.getDeclaredFields() ) {
				boolean flagInject = 
						field.isAnnotationPresent( Inject.class )
						|| field.isAnnotationPresent( Resource.class );
				if ( flagInject )  {
					field.setAccessible(true);
					Object dep = null;
					if ( field.getType().isAssignableFrom( this.getClass()  ) )  {
						dep = this;
					} else {
						dep = getBean( field.getType() );
					}
					if ( dep != null ) {
						field.set( bean, dep );
					} else {
						throw new RuntimeException( "Bean not found : " + field.getType().getName() );
					}
				}
			}
			
			for ( Method method : typeImpl.getMethods() ) {
				boolean flagInject = 
						( method.isAnnotationPresent( Inject.class )
							|| method.isAnnotationPresent( Resource.class )
						) && method.getParameterCount() > 0;
				if ( flagInject )  {
					method.setAccessible(true);
					
					Object deps[] = new Object[method.getParameterCount()];
					for ( int i = 0; i < deps.length; ++i  ) {
						deps[i] = null;
						if ( method.getParameterTypes()[i].isAssignableFrom( this.getClass()  ) )  {
							deps[i] = this;
						} else {
							deps[i] = getBean( method.getParameterTypes()[i] );
						}
						if ( deps[i] == null ) {
							throw new RuntimeException( "Bean not found : " + method.getParameterTypes()[i].getName() );
						}
					}
					method.invoke( bean, deps );
				}
			}
	
			// Exécute la méthode d'initialisaton du bean
			for ( Method method : bean.getClass().getMethods() ) {
				if ( method.isAnnotationPresent( PostConstruct.class ) )  {
					method.setAccessible(true);
					method.invoke( bean );						
				}
			}
		
			return bean;
			
		} catch ( Exception e) {
			if ( bean != null ) {
				beans.remove(bean);
			}
			throw runtimeException(e);
		}
	}
	
	
	public static final RuntimeException runtimeException( Throwable e ) {
		if ( e instanceof ReflectiveOperationException
				&& e.getCause() != null ) {
			e = e.getCause();
		}
		if ( e instanceof RuntimeException ) {
			return (RuntimeException) e;
		} else {
			return new RuntimeException(e);
		}
	}

}
