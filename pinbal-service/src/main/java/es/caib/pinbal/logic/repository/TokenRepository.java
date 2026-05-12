package es.caib.pinbal.logic.repository;

import es.caib.pinbal.logic.model.ScspToken;
import es.caib.pinbal.logic.model.ScspTokenId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TokenRepository extends JpaRepository<ScspToken, ScspTokenId>  {

    @Query("select t.datos from ScspToken t where t.idPeticion = :peticioId and t.tipoMensaje = es.caib.pinbal.core.model.ScspTokenId.FAULT")
    String getFaultError(@Param("peticioId") String peticioId);

    List<ScspToken> findByIdPeticionOrderByTipoMensajeAsc(String idPeticion);
}
