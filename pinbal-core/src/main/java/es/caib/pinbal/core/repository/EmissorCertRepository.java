/**
 * 
 */
package es.caib.pinbal.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.pinbal.core.model.EmissorCert;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un emisor certificat que està emmagatzemat
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EmissorCertRepository extends JpaRepository<EmissorCert, Long> {

	public List<EmissorCert> findAll();

	public EmissorCert findById(Long id);

}
