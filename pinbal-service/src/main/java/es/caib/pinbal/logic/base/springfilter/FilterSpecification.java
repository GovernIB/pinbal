package es.caib.pinbal.logic.base.springfilter;

import com.turkraft.springfilter.parser.Filter;

import javax.persistence.criteria.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementació de la interfície Specification per les consultes Spring Filter.
 * 
 * @author Límit Tecnologies
 */
public class FilterSpecification<T> extends com.turkraft.springfilter.boot.FilterSpecification<T> {

	private static final long serialVersionUID = 1L;

	public FilterSpecification(String input) {
		super(input);
	}

	public FilterSpecification(Filter filter) {
		super(filter);
	}

	@Override
	public Predicate toPredicate(
			Root<T> root,
			CriteriaQuery<?> query,
			CriteriaBuilder criteriaBuilder) {
		Predicate predicate;
		Map<String, Join<?, ?>> j = getJoins() != null ? getJoins() : new HashMap<String, Join<?, ?>>();
		String input = getInput();
		if (input != null) {
			predicate = !input.trim().isEmpty() ? (Predicate)ExpressionGenerator.run(
				Filter.from(input),
				root,
				query,
				criteriaBuilder,
				j,
				getPayload()) : null;
		} else {
			predicate = (Predicate) ExpressionGenerator.run(
					getFilter(),
					root,
					query,
					criteriaBuilder,
					j,
					getPayload());
		}
		return predicate;
	}

}
