package projet.commun;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public final class Roles {
	
	public static final String ADMIN = "ADMINISTRATEUR";
	public static final String SECRETAIRE = "SECRETAIRE";
	public static final String JUGE = "JUGE";
	
	
	private static final List<String> roles = Collections.unmodifiableList(Arrays.asList( 
			ADMIN,			
			SECRETAIRE,
			JUGE
	));

	private static final String[] libelles = new String[] {
			"ADMINISTRATEUR",
			"SECRETAIRE",
	};
	
	private Roles() {}


	public static List<String> getRoles() {
		return roles;
	}
	
	public static String getLibelle( String role ) {
		int index = roles.indexOf( role );
		if ( index == -1 ) {
			throw new IllegalArgumentException();
		} 
		return libelles[index];
	}

}
