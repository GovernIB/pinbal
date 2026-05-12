package es.caib.pinbal.persist.repository;

import es.caib.pinbal.persist.entity.ScspToken;
import es.caib.pinbal.persist.entity.ScspTokenId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TokenRepository extends JpaRepository<ScspToken, ScspTokenId>  {

    @Query("select t.datos from ScspToken t where t.idPeticion = :peticioId and t.tipoMensaje = es.caib.pinbal.persist.entity.ScspTokenId.FAULT")
    String getFaultError(@Param("peticioId") String peticioId);

    List<ScspToken> findByIdPeticionOrderByTipoMensajeAsc(String idPeticion);
}
