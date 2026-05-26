package es.caib.pinbal.logic.base.springfilter;

import com.turkraft.springfilter.FilterParameters;
import com.turkraft.springfilter.FilterUtils;
import com.turkraft.springfilter.exception.BadFilterFunctionUsageException;
import com.turkraft.springfilter.exception.InternalFilterException;
import com.turkraft.springfilter.exception.UnauthorizedFilterPathException;
import com.turkraft.springfilter.parser.Filter;
import com.turkraft.springfilter.parser.FilterParser.FieldContext;
import com.turkraft.springfilter.parser.FilterParser.FunctionContext;
import com.turkraft.springfilter.parser.FilterParser.InfixContext;
import com.turkraft.springfilter.parser.FilterParser.InputContext;
import com.turkraft.springfilter.parser.StringConverter;
import com.turkraft.springfilter.parser.generator.expression.ExpressionGeneratorParameters;
import com.turkraft.springfilter.parser.operation.InfixOperation;
import com.turkraft.springfilter.shaded.org.antlr.v4.runtime.tree.ParseTreeProperty;
import es.caib.pinbal.logic.intf.base.util.CompositePkUtil;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import org.hibernate.query.criteria.internal.path.SingularAttributePath;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.ResolvableType;
import org.springframework.data.domain.Persistable;
import org.springframework.util.ReflectionUtils;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiFunction;

/**
 * Implementació de la interfície Specification per les consultes Spring Filter.
 * 
 * @author Límit Tecnologies
 */
public class ExpressionGenerator extends com.turkraft.springfilter.parser.generator.expression.ExpressionGenerator {

	static {
		FilterParameters.DATE_FORMATTER = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
		FilterParameters.LOCALDATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		FilterParameters.LOCALDATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		FilterParameters.OFFSETDATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		FilterParameters.OFFSETTIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSSXXX");
		FilterParameters.ZONEDDATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		FilterParameters.LOCALTIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
		FilterParameters.YEARMONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
		FilterParameters.MONTHDAY_FORMATTER = DateTimeFormatter.ofPattern("--MM-dd");
	}

	protected ExpressionGenerator(
			Root<?> root,
			CriteriaQuery<?> criteriaQuery,
			CriteriaBuilder criteriaBuilder,
			Map<String, Join<?, ?>> joins,
			Object payload) {
		super(root, criteriaQuery, criteriaBuilder, joins, payload);
	}

	public static Expression<?> run(
			Filter filter,
			Root<?> root,
			CriteriaQuery<?> criteriaQuery,
			CriteriaBuilder criteriaBuilder,
			Map<String, Join<?, ?>> joins,
			Object payload) {
		Objects.requireNonNull(filter);
		Objects.requireNonNull(root);
		Objects.requireNonNull(criteriaQuery);
		Objects.requireNonNull(criteriaBuilder);
		Objects.requireNonNull(joins);
		return new ExpressionGenerator(root, criteriaQuery, criteriaBuilder, joins, payload).visit(filter);
	}

	@Override
	public Expression<?> visitFunction(FunctionContext ctx) {
		String functionName = ctx.ID().getText().toLowerCase();
		if (functionName.equals("exists")) {
			return getCriteriaBuilder().exists(getSubquery(ctx));
		} else {
			return super.visitFunction(ctx);
		}
	}

	@Override
	public Expression<?> visitField(FieldContext ctx) {
		return getDatabasePath(
				getRoot(),
				getJoins(),
				getPayload(),
				ctx.getText(),
				ExpressionGeneratorParameters.FILTERING_AUTHORIZATION);
	}

	@Override
	public Expression<?> visitInput(InputContext ctx) {
		Field expectedInputTypesField = ReflectionUtils.findField(
				com.turkraft.springfilter.parser.generator.expression.ExpressionGenerator.class,
				"expectedInputTypes");
		ReflectionUtils.makeAccessible(expectedInputTypesField);
		@SuppressWarnings("unchecked")
		ParseTreeProperty<Class<?>> expectedInputTypes = (ParseTreeProperty<Class<?>>)ReflectionUtils.getField(expectedInputTypesField, this);
		if (expectedInputTypes.get(ctx) == null) {
			throw new InternalFilterException("The expected class should be set previous to visiting the input");
		}
		Class<?> expectedInputType = expectedInputTypes.get(ctx);
		if (inputContextIsOrEndsWithId(ctx)) {
			if (isInputTypeCompositePk(expectedInputType)) {
				Object value = CompositePkUtil.getInstance().getCompositePkFromSerializedId(
						StringConverter.cleanStringInput(ctx.getText()),
						getPkPathType(
								ctx,
								getRoot(),
								getJoins(),
								getPayload()));
				return getCriteriaBuilder().literal(value);
			} else {
				Object value = StringConverter.convert(
						StringConverter.cleanStringInput(ctx.getText()),
						getPkPathType(
								ctx,
								getRoot(),
								getJoins(),
								getPayload()));
				return getCriteriaBuilder().literal(value);
			}
		} else {
			Object value = StringConverter.convert(ctx.getText(), expectedInputType);
			if (value == null) {
				throw new InternalFilterException("The input '" + StringConverter.cleanStringInput(ctx.getText()) + "' could not be converted to " + expectedInputType);
			}
			return getCriteriaBuilder().literal(processInputValue(ctx, value));
		}
	}

	@SuppressWarnings({"unchecked"})
	private Subquery<Integer> getSubquery(FunctionContext ctx) {
		if (ctx.arguments.size() != 1) {
			throw new BadFilterFunctionUsageException("The function '" + ctx.ID().getText() + "' needs one argument");
		}
		Subquery<Integer> subquery = getCriteriaQuery().subquery(Integer.class);
		Root<?> subroot = subquery.correlate(getRoot());
		Expression<?> predicate = ExpressionGenerator.run(
				ctx.arguments.get(0),
				subroot,
				getCriteriaQuery(),
				getCriteriaBuilder(),
				new HashMap<String, Join<?, ?>>(),
				null);
		if (!Boolean.class.isAssignableFrom(predicate.getJavaType())) {
			throw new BadFilterFunctionUsageException("The function '" + ctx.ID().getText() + "' needs a predicate as its argument");
		}
		subquery.select(getCriteriaBuilder().literal(1));
		subquery.where((Expression<Boolean>)predicate);
		return subquery;
	}

	private static Path<?> getDatabasePath(
			Root<?> root,
			Map<String, Join<?, ?>> joins,
			Object payload,
			String fieldPath,
			BiFunction<Path<?>, Object, Boolean> authorizer) {
		/*if (!fieldPath.contains(".")) {
			return authorize(authorizer, getPathFromEntityOrEmbeddedResource(root, fieldPath), payload, fieldPath);
		}*/
		if (!FilterUtils.isHibernateCoreDependencyPresent()) {
			throw new UnsupportedOperationException(
					"The Hibernate Core dependency should be added in order to filter nested fields");
		}
		Path<?> path = root;
		From<?, ?> from = root;
		String[] fields = fieldPath.split("\\.");
		String chain = null;
		for (int i = 0; i < fields.length; i++) {
			String field = fields[i];
			path = getPathFromEntityOrEmbeddedResource(from, field);
			if (chain == null) {
				chain = field;
			} else {
				chain += "." + field;
			}
			authorize(authorizer, path, payload, chain);
			JoinType joinType = path instanceof PluralAttributePath ? JoinType.INNER
					: (path instanceof SingularAttributePath && ((SingularAttributePath<?>) path).getAttribute().getPersistentAttributeType() != PersistentAttributeType.BASIC
					? JoinType.LEFT : null);
			if (joinType != null && hasJoinColumnsOrFormulasAnnotation(from, field)) {
				// Per a evitar errors amb les joins dels camps de l'entity
				// amb l'anotació JoinColumnsOrFormulas forçam el tipus de
				// join a INNER.
				joinType = JoinType.INNER;
			}
			if (joinType != null && i < fields.length - 1) {
				if (!joins.containsKey(chain)) {
					joins.put(chain, from.join(field, joinType));
				}
				from = joins.get(chain);
			}
		}
		return path;
	}

	private static Class<?> getPkPathType(
			InputContext ctx,
			Root<?> root,
			Map<String, Join<?, ?>> joins,
			Object payload) {
		String path = ctx.getParent().getChild(0).getText();
		Class<?> pathJavaType;
		if ("id".equals(path)) {
			pathJavaType = root.getJavaType();
		} else {
			Expression<?> databasePath = getDatabasePath(
					root,
					joins,
					payload,
					path.substring(0, path.length() - ".id".length()),
					ExpressionGeneratorParameters.FILTERING_AUTHORIZATION);
			pathJavaType = databasePath.getJavaType();
			if (Collection.class.isAssignableFrom(pathJavaType)) {
				String fieldName = path.substring(0, path.length() - ".id".length());
				Field collectionField = Objects.requireNonNull(ReflectionUtils.findField(root.getJavaType(), fieldName));
				pathJavaType = ResolvableType.forField(collectionField).getGeneric(0).resolve();
			}
		}
		if (Persistable.class.isAssignableFrom(Objects.requireNonNull(pathJavaType))) {
			return Objects.requireNonNull(GenericTypeResolver.resolveTypeArguments(
					pathJavaType,
					Persistable.class))[0];
		} else {
			throw new InternalFilterException("Couldn't determine type for pk path " + path);
		}
	}

	private static Object processInputValue(
			InputContext ctx,
			Object value) {
		if (value instanceof Date) {
			InfixOperation op = InfixOperation.from(((InfixContext)ctx.getParent()).operator.getType());
			if (op == InfixOperation.GREATER_THAN || op == InfixOperation.GREATER_THAN_OR_EQUAL) {
				Calendar cal = Calendar.getInstance();
				cal.setTime((Date)value);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				return cal.getTime();
			} else if (op == InfixOperation.LESS_THAN || op == InfixOperation.LESS_THAN_OR_EQUAL) {
				Calendar cal = Calendar.getInstance();
				cal.setTime((Date)value);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				cal.set(Calendar.MILLISECOND, 999);
				return cal.getTime();
			} else {
				return value;
			}
		} else {
			return value;
		}
	}

	private static Path<?> authorize(
			BiFunction<Path<?>, Object, Boolean> authorizer,
			Path<?> path,
			Object payload,
			String fieldPath) {
		if (authorizer != null) {
			if (!Boolean.TRUE.equals(authorizer.apply(path, payload))) {
				throw new UnauthorizedFilterPathException(path, fieldPath);
			}
		}
		return path;
	}

	private static Path<?> getPathFromEntityOrEmbeddedResource(
			Path<?> basePath,
			String fieldPath) {
		return basePath.get(fieldPath);
	}

	private static boolean hasJoinColumnsOrFormulasAnnotation(
			From<?, ?> from,
			String fieldPath) {
		Field field = ReflectionUtils.findField(from.getJavaType(), fieldPath);
		if (field != null) {
			return field.getAnnotation(JoinColumnsOrFormulas.class) != null;
		} else {
			return false;
		}
	}

	private static boolean isInputTypeCompositePk(Class<?> inputType) {
		return CompositePkUtil.getInstance().isCompositePkClass(inputType);
	}

	private static boolean inputContextIsOrEndsWithId(InputContext ctx) {
		int childCount = ctx.getParent().getChildCount();
		String first = ctx.getParent().getChild(0).getText();
		String last = ctx.getParent().getChild(childCount - 1).getText();
		return first.equals("id") || first.endsWith(".id") || last.equals("id") || last.endsWith(".id");
	}

}
