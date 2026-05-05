package es.caib.pinbal.client.comu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page<T> {

    private List<T> content;
    private int size;
    private int totalElements;
    private int totalPages;
    private int number;

}