/**
 * 
 */
package es.caib.pinbal.core.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import es.caib.pinbal.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.pinbal.core.model.ProcedimentServei;
import es.caib.pinbal.core.model.Usuari;
import es.caib.pinbal.core.repository.ProcedimentServeiRepository;
import es.caib.pinbal.core.repository.UsuariRepository;
import es.caib.pinbal.plugins.DadesUsuari;
import es.caib.pinbal.plugins.SistemaExternException;


/**
 * Helper per a operacions amb usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UsuariHelper {

	@Resource
	private UsuariRepository usuariRepository;
	@Resource
	private ProcedimentServeiRepository procedimentServeiRepository;

	@Resource
	private PluginHelper externHelper;

	@Resource
	private MutableAclService aclService;



	public void init(String usuariCodi) {
		Usuari usuari = usuariRepository.findOne(usuariCodi);
		if (usuari == null || !usuari.isInicialitzat()) {
			// Només inicialitza els usuaris que entren per primera vegada
			// Consulta les dades de l'usuari al sistema extern
			DadesUsuari dadesUsuari = null;
			try {
				dadesUsuari = externHelper.dadesUsuariConsultarAmbUsuariCodi(usuariCodi);
			} catch (SistemaExternException ex) {
				LOGGER.error("Error al consultar les dades de l'usuari (codi=" + usuariCodi + ") al sistema extern", ex);
			}
			if (dadesUsuari != null) {
				if (usuari == null) {
					// Si l'usuari no està donat d'alta a la taula d'usuaris
					Usuari usuariNif = null;
					// Cerca l'usuari amb el mateix NIF a la taula d'usuaris
					if (dadesUsuari.getNif() != null && !dadesUsuari.getNif().isEmpty())
						usuariNif = usuariRepository.findByNif(dadesUsuari.getNif());
					if (usuariNif == null) {
						// Si no hi ha cap usuari amb el mateix NIF
						// en crea un de nou
						if (dadesUsuari != null) {
							LOGGER.debug("Creant l'usuari (codi=" + usuariCodi + ")");
							usuari = usuariRepository.save(
									Usuari.getBuilderInicialitzat(
											dadesUsuari.getCodi(),
											dadesUsuari.getNom(),
											dadesUsuari.getNif()).build());
						}
					} else {
						// Si hi ha un usuari amb el mateix NIF
						// actualitza les seves dades
						if (!usuariNif.isInicialitzat()) {
							LOGGER.debug("Inicialitzant l'usuari (codi=" + usuariNif.getCodi() + ")");
							usuari = moure(
									usuariNif,
									dadesUsuari,
									usuariRepository,
									procedimentServeiRepository,
									aclService);
						} else {
							LOGGER.debug("L'usuari (codi=" + usuariNif.getCodi() + ") ja estava inicialitzat");
						}
					}
				} else if (!usuari.isInicialitzat()) {
					// Si l'usuari està donat d'alta però sense inicialitzar
					usuari.update(
							dadesUsuari.getNom(),
							dadesUsuari.getNif());
				}
			}
		}
	}

	public Usuari moure(
			Usuari usuari,
			DadesUsuari dadesUsuari,
			UsuariRepository usuariRepository,
			ProcedimentServeiRepository procedimentServeiRepository,
			MutableAclService aclService) {
		// Cerca els registres ProcedimentServei de l'usuari origen
		List<ProcedimentServei> pss = procedimentServeiRepository.findByUsuariAmbAccesEntitat(usuari.getCodi());
		// Crea el nou usuari
		Usuari usuariNou = Usuari.getBuilderInicialitzat(
				dadesUsuari.getCodi(),
				dadesUsuari.getNom(),
				dadesUsuari.getNif()).build();
		// Mou els registres EntitatUsuari cap al nou usuari
		usuari.moureEntitats(usuariNou);
		usuariRepository.delete(usuari);
		usuariRepository.save(usuariNou);
		// Reassigna els permisos a damunt ProcedimentServei al nou usuari
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		PermisosHelper.filterGrantedAll(
				pss,
				new ObjectIdentifierExtractor<ProcedimentServei>() {
					public Long getObjectIdentifier(ProcedimentServei object) {
						return object.getId();
					}
				},
				ProcedimentServei.class,
				new Permission[] {BasePermission.READ},
				aclService,
				auth);
		for (ProcedimentServei ps: pss) {
			PermisosHelper.mourePermisUsuari(
					usuari.getCodi(),
					usuariNou.getCodi(),
					ProcedimentServei.class,
					ps.getId(),
					BasePermission.READ,
					aclService);
		}
		return usuariNou;
	}

	public Authentication generarUsuariAutenticat(
			DadesUsuari dadesUsuari,
			boolean establirComAUsuariActual) {
		List<GrantedAuthority> authorities = null;
		if (dadesUsuari != null && dadesUsuari.getRols() != null) {
			authorities = new ArrayList<GrantedAuthority>();
			for (String rol: dadesUsuari.getRols()) {
				authorities.add(new SimpleGrantedAuthority(rol));
			}
		}
		Authentication auth = new DadesUsuariAuthenticationToken(
			dadesUsuari.getCodi(),
			authorities);
		if (establirComAUsuariActual)
			SecurityContextHolder.getContext().setAuthentication(auth);
		return auth;
	}
	public Authentication generarUsuariAutenticat(
			String usuariCodi,
			boolean establirComAUsuariActual) {
		List<GrantedAuthority> authorities = null;
		Authentication auth = new DadesUsuariAuthenticationToken(
				usuariCodi,
			authorities);
		if (establirComAUsuariActual)
			SecurityContextHolder.getContext().setAuthentication(auth);
		return auth;
	}

	public class DadesUsuariAuthenticationToken extends AbstractAuthenticationToken {
		String principal;
		public DadesUsuariAuthenticationToken(
				String usuariCodi,
				Collection<GrantedAuthority> authorities) {
			super(authorities);
			principal = usuariCodi;
		}
		@Override
		public Object getCredentials() {
			return principal;
		}
		@Override
		public Object getPrincipal() {
			return principal;
		}
		private static final long serialVersionUID = 5974089352023050267L;
	}



	private static final Logger LOGGER = LoggerFactory.getLogger(UsuariHelper.class);

}
