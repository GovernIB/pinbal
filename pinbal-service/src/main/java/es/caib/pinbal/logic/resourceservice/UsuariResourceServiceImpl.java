package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.helper.AuthenticationHelper;
import es.caib.pinbal.logic.base.service.BaseMutableResourceService;
import es.caib.pinbal.logic.intf.model.UsuariResource;
import es.caib.pinbal.logic.intf.model.auth.PinbalAuthenticationDetails;
import es.caib.pinbal.logic.intf.resourceservice.UsuariResourceService;
import es.caib.pinbal.persist.resourceentity.UsuariResourceEntity;
import es.caib.pinbal.persist.resourcerepository.UsuariResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * Implementació del servei de gestió d'usuaris de l'aplicació.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuariResourceServiceImpl
	extends BaseMutableResourceService<UsuariResource, String, UsuariResourceEntity>
	implements UsuariResourceService {

	private final AuthenticationHelper authenticationHelper;
	private final UsuariResourceRepository usuariResourceRepository;

	@Override
	public void refresh() {
		UsuariResource usuariFromAuth = getUsuariResourceFromAuth();
		if (usuariFromAuth != null) {
			Optional<UsuariResourceEntity> usuariOptional = usuariResourceRepository.findById(authenticationHelper.getCurrentUserName());
			if (usuariOptional.isPresent()) {
				UsuariResourceEntity usuariFromDb = usuariOptional.get();
				if (hasToUpdateUsuari(usuariFromDb, usuariFromAuth)) {
//					usuariFromDb.setNomSencer(usuariFromAuth.getNomSencer());
					usuariFromDb.setNom(usuariFromAuth.getNom());
					usuariFromDb.setNif(usuariFromAuth.getNif());
					usuariFromDb.setEmail(usuariFromAuth.getEmail());
					usuariResourceRepository.save(usuariFromDb);
				}
			} else {
				UsuariResourceEntity usuari = UsuariResourceEntity.builder().
					resource(usuariFromAuth).
					build();
				usuariResourceRepository.save(usuari);
			}
		}
	}

	@Override
	protected String additionalSpringFilter(
		String currentSpringFilter,
		String[] namedQueries) {
		return "id:'" + authenticationHelper.getCurrentUserName() + "'";
	}

	private UsuariResource getUsuariResourceFromAuth() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() != null) {
			if (authentication.getPrincipal() instanceof Jwt) {
				// Authenticació provinent de Spring Boot
				UsuariResource usuariResource = new UsuariResource();
				Jwt jwt = (Jwt)authentication.getPrincipal();
				usuariResource.setCodi(authentication.getName());
				usuariResource.setNom(jwt.getClaimAsString("name"));
				usuariResource.setNif(jwt.getClaimAsString("nif"));
				usuariResource.setEmail(jwt.getClaimAsString("email"));
				usuariResource.setIdioma("CA");
				return usuariResource;
			} else if (authentication.getPrincipal() instanceof User) {
				UsuariResource usuariResource = new UsuariResource();
				PinbalAuthenticationDetails details = (PinbalAuthenticationDetails)authentication.getDetails();
				usuariResource.setCodi(authentication.getName());
				usuariResource.setNom(details.getName());
				usuariResource.setNif(details.getNif());
				usuariResource.setEmail(details.getEmail());
				usuariResource.setIdioma("CA");
				return usuariResource;
			}
		}
		return null;
	}

	private boolean hasToUpdateUsuari(UsuariResourceEntity usuariFromDb, UsuariResource usuariFromAuth) {
		return !Objects.equals(usuariFromDb.getNom(), usuariFromAuth.getNom()) ||
			!Objects.equals(usuariFromDb.getNif(), usuariFromAuth.getNif()) ||
			!Objects.equals(usuariFromDb.getEmail(), usuariFromAuth.getEmail());
	}

}
