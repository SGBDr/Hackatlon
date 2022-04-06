

-- Supprime le schéma tuto

DROP SCHEMA IF EXISTS tuto CASCADE;


-- Crée l'utilisateur tuto
-- (après l'avoir supprimé au préalable s'il existait déjà)

DO $code$
BEGIN
	IF EXISTS (SELECT  FROM pg_catalog.pg_roles WHERE rolname  = 'tuto')
	THEN
		REVOKE CREATE ON DATABASE postgres FROM tuto;
		DROP USER tuto;
	END IF;
END
$code$;

CREATE USER tuto WITH PASSWORD 'tuto';
GRANT CREATE ON DATABASE postgres TO tuto;

