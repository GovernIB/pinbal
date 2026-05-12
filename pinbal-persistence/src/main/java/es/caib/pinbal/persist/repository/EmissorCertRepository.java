/**
 * 
 */
package es.caib.pinbal.persist.repository;

import es.caib.pinbal.persist.entity.EmissorCert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

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

	public Optional<EmissorCert> findById(Long id);

	public EmissorCert  findByCif(String cif);
}
