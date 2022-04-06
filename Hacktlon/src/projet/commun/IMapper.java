package projet.commun;


import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


import projet.data.Compte;


@Mapper
public interface IMapper {
	
	Compte update( @MappingTarget Compte target, Compte source  );
	
	/*Categorie update( @MappingTarget Categorie target, Categorie source );
	
	@Mapping( target="categorie", expression="java( source.getCategorie() )" )
	Memo update( @MappingTarget Memo target, Memo source );
	
	@Mapping( target="personne", expression="java( source.getPersonne() )" )
	Service update( @MappingTarget Service target, Service source );

	@Mapping( target="categorie", expression="java( source.getCategorie() )" )
	Personne update( @MappingTarget Personne target, Personne source );*/
	
	
    
}
