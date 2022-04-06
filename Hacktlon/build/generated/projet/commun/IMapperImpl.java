package projet.commun;

import javafx.collections.ObservableList;
import javax.annotation.processing.Generated;
import projet.data.Compte;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-04-06T09:29:11+0200",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 1.4.0.v20210708-0430, environment: Java 17 (Eclipse Adoptium)"
)
public class IMapperImpl implements IMapper {

    @Override
    public Compte update(Compte target, Compte source) {
        if ( source == null ) {
            return null;
        }

        target.setEmail( source.getEmail() );
        target.setId( source.getId() );
        target.setMotDePasse( source.getMotDePasse() );
        target.setPseudo( source.getPseudo() );
        if ( target.getRoles() != null ) {
            target.getRoles().clear();
            ObservableList<String> observableList = source.getRoles();
            if ( observableList != null ) {
                target.getRoles().addAll( observableList );
            }
        }

        return target;
    }
}
