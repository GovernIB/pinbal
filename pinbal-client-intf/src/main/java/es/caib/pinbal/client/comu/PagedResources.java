package es.caib.pinbal.client.comu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

// Classe que representa els recursos paginats
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResources<T> {

    private List<T> content;
    private PageMetadata page;


    // Classe interna per representar la metadata de la pàgina
    @Getter @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageMetadata {

        private int size;
        private int totalElements;
        private int totalPages;
        private int number;

    }
}