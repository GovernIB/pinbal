package es.caib.pinbal.webapp.mapper;

public interface BaseCommandMapper<C, D> {

    D commandToDto(C command);
    C dtoToCommand(D dto);

}
