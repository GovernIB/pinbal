package es.caib.pinbal.client.comu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Classe que representa un recurs amb envoltori
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resource<T> {

    private T content;

}