package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.ScspToken;
import es.caib.pinbal.core.model.ScspTokenId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TokenRepository extends JpaRepository<ScspToken, ScspTokenId>  {

    @Query("select t.datos from ScspToken t where t.idPeticion = :peticioId and t.tipoMensaje = es.caib.pinbal.core.model.ScspTokenId.FAULT")
    String getFaultError(@Param("peticioId") String peticioId);
}
