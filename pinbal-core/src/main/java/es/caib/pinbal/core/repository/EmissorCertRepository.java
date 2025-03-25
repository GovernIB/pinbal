/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.EmissorCert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un emisor certificat que està emmagatzemat
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EmissorCertRepository extends JpaRepository<EmissorCert, Long> {

	public List<EmissorCert> findAll();
	
	public Page<EmissorCert> findAll(Pageable pageable);

	public EmissorCert findById(Long id);

	public EmissorCert  findByCif(String cif);
}
