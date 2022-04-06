SET search_path TO tuto;


-- Supprimer toutes les données
DELETE FROM telephone;
DELETE FROM personne;
DELETE FROM categorie;
DELETE FROM role;
DELETE FROM compte;
DELETE FROM memo;
DELETE FROM service;


-- Compte

INSERT INTO compte (idcompte, pseudo, motdepasse, email ) VALUES 
  (1, 'geek', 'geek', 'geek@jfox.fr' ),
  (2, 'chef', 'chef', 'chef@jfox.fr' ),
  (3, 'job', 'job', 'job@jfox.fr' );

ALTER TABLE compte ALTER COLUMN idcompte RESTART WITH 4;


-- Role

INSERT INTO role (idcompte, role) VALUES 
  ( 1, 'ADMINISTRATEUR' ),
  ( 1, 'UTILISATEUR' ),
  ( 2, 'UTILISATEUR' ),
  ( 3, 'UTILISATEUR' );


-- Categorie
  
INSERT INTO categorie (idcategorie, libelle, debut ) VALUES 
  (1, 'Employés', {d '2021-02-25' } ),
  (2, 'Partenaires', NULL ),
  (3, 'Clients', NULL ),
  (4, 'Fournisseurs', {d '2021-02-25' } ),
  (5, 'Dirigeants', {d '2021-02-25' } );

ALTER TABLE categorie ALTER COLUMN idcategorie RESTART WITH 6;


-- Personne

INSERT INTO personne (idpersonne, idcategorie, nom, prenom) VALUES 
  ( 1, 1, 'GRASSET', 'Jérôme' ),
  ( 2, 1, 'BOUBY', 'Claude' ),
  ( 3, 1, 'AMBLARD', 'Emmanuel' );

ALTER TABLE personne ALTER COLUMN idpersonne RESTART WITH 4;


-- Telephone

INSERT INTO telephone (idtelephone, idpersonne, libelle, numero ) VALUES 
  ( 11, 1, 'Portable', '06 11 11 11 11' ),
  ( 12, 1, 'Fax', '05 55 99 11 11' ),
  ( 13, 1, 'Bureau', '05 55 11 11 11' ),
  ( 21, 2, 'Portable', '06 22 22 22 22' ),
  ( 22, 2, 'Fax', '05 55 99 22 22' ),
  ( 23, 2, 'Bureau', '05 55 22 22 22' ),
  ( 31, 3, 'Portable', '06 33 33 33 33' ),
  ( 32, 3, 'Fax', '05 55 99 33 33' ),
  ( 33, 3, 'Bureau', '05 55 33 33 33' );

ALTER TABLE telephone ALTER COLUMN idtelephone RESTART WITH 100;

INSERT INTO memo (idmemo, titre, description, flagurgent, idcategorie ) VALUES
 ( 1, 'Mémo n°1', 'Texte du mémo n°1', TRUE, NULL ),
 ( 2, 'Mémo n°2', 'Texte du mémo n°2', FALSE, 1 ),
 ( 3, 'Mémo n°3', NULL, TRUE, 2 );

ALTER TABLE memo ALTER COLUMN idmemo RESTART WITH 4;

INSERT INTO service (idservice, nom, anneecreation, flagsiege, idpersonne ) VALUES
 ( 1, 'Service n°1', 2022, TRUE, NULL ),
 ( 2, 'Service n°2', 1099, FALSE, 1 ),
 ( 3, 'Service n°3', 1999, TRUE, 2 );

ALTER TABLE service ALTER COLUMN idservice RESTART WITH 4;
