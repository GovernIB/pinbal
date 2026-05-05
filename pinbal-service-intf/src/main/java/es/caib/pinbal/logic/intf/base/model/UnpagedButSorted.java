package es.caib.pinbal.logic.intf.base.model;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Extensió de Pageable sense paginació (isPaged() retorna false) però que es pot ordenar.
 *
 * @author Límit Tecnologies
 */
public class UnpagedButSorted implements Pageable {

	private final Sort sort;

	public UnpagedButSorted(Sort sort) {
		this.sort = sort;
	}

	@Override
	public boolean isPaged() {
		return false;
	}

	@Override
	public Pageable previousOrFirst() {
		return this;
	}

	@Override
	public Pageable next() {
		return this;
	}

	@Override
	public boolean hasPrevious() {
		return false;
	}

	@Override
	public Sort getSort() {
		return sort;
	}

	@Override
	public int getPageSize() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getPageNumber() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getOffset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Pageable first() {
		return this;
	}

	@Override
	public Pageable withPage(int pageNumber) {
		if (pageNumber == 0) {
			return this;
		}
		throw new UnsupportedOperationException();
	}

}
