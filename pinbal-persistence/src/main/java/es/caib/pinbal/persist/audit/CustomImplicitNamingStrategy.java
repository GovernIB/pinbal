package es.caib.pinbal.persist.audit;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.boot.model.naming.*;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.internal.util.StringHelper;

@Slf4j
public class CustomImplicitNamingStrategy implements ImplicitNamingStrategy {

    public static final ImplicitNamingStrategy INSTANCE = new ImplicitNamingStrategyJpaCompliantImpl();

    public CustomImplicitNamingStrategy() {
    }

    public Identifier determinePrimaryTableName(ImplicitEntityNameSource source) {

        if (source == null) {
            throw new HibernateException("Entity naming information was not provided.");
        }
        var tableName = this.transformEntityName(source.getEntityNaming());
        if (tableName == null) {
            throw new HibernateException("Could not determine primary table name for entity");
        }
        return this.toIdentifier(tableName, source.getBuildingContext());
    }

    protected String transformEntityName(EntityNaming entityNaming) {
        return StringHelper.isNotEmpty(entityNaming.getJpaEntityName()) ? entityNaming.getJpaEntityName() : StringHelper.unqualify(entityNaming.getEntityName());
    }

    public Identifier determineJoinTableName(ImplicitJoinTableNameSource source) {

        var name = source.getOwningPhysicalTableName() + '_' + source.getNonOwningPhysicalTableName();
        return this.toIdentifier(name, source.getBuildingContext());
    }

    public Identifier determineCollectionTableName(ImplicitCollectionTableNameSource source) {

        var entityName = this.transformEntityName(source.getOwningEntityNaming());
        var name = entityName + '_' + this.transformAttributePath(source.getOwningAttributePath());
        return this.toIdentifier(name, source.getBuildingContext());
    }

    public Identifier determineIdentifierColumnName(ImplicitIdentifierColumnNameSource source) {
        return this.toIdentifier(this.transformAttributePath(source.getIdentifierAttributePath()), source.getBuildingContext());
    }

    public Identifier determineDiscriminatorColumnName(ImplicitDiscriminatorColumnNameSource source) {
        return this.toIdentifier(source.getBuildingContext().getMappingDefaults().getImplicitDiscriminatorColumnName(), source.getBuildingContext());
    }

    public Identifier determineTenantIdColumnName(ImplicitTenantIdColumnNameSource source) {
        return this.toIdentifier(source.getBuildingContext().getMappingDefaults().getImplicitTenantIdColumnName(), source.getBuildingContext());
    }

    public Identifier determineBasicColumnName(ImplicitBasicColumnNameSource source) {
        return this.toIdentifier(this.transformAttributePath(source.getAttributePath()), source.getBuildingContext());
    }

    public Identifier determineJoinColumnName(ImplicitJoinColumnNameSource source) {

        var name = source.getNature() != ImplicitJoinColumnNameSource.Nature.ELEMENT_COLLECTION && source.getAttributePath() != null
                        ? this.transformAttributePath(source.getAttributePath()) + '_' + source.getReferencedColumnName().getText()
                        : this.transformEntityName(source.getEntityNaming()) + '_' + source.getReferencedColumnName().getText();

        return this.toIdentifier(name, source.getBuildingContext());
    }

    public Identifier determinePrimaryKeyJoinColumnName(ImplicitPrimaryKeyJoinColumnNameSource source) {
        return source.getReferencedPrimaryKeyColumnName();
    }

    public Identifier determineAnyDiscriminatorColumnName(ImplicitAnyDiscriminatorColumnNameSource source) {
        return this.toIdentifier(this.transformAttributePath(source.getAttributePath()) + "_" + source.getBuildingContext().getMappingDefaults().getImplicitDiscriminatorColumnName(), source.getBuildingContext());
    }

    public Identifier determineAnyKeyColumnName(ImplicitAnyKeyColumnNameSource source) {
        return this.toIdentifier(this.transformAttributePath(source.getAttributePath()) + "_" + source.getBuildingContext().getMappingDefaults().getImplicitIdColumnName(), source.getBuildingContext());
    }

    public Identifier determineMapKeyColumnName(ImplicitMapKeyColumnNameSource source) {
        return this.toIdentifier(this.transformAttributePath(source.getPluralAttributePath()) + "_KEY", source.getBuildingContext());
    }

    public Identifier determineListIndexColumnName(ImplicitIndexColumnNameSource source) {
        return this.toIdentifier(this.transformAttributePath(source.getPluralAttributePath()) + "_ORDER", source.getBuildingContext());
    }

    public Identifier determineForeignKeyName(ImplicitForeignKeyNameSource source) {

        var userProvidedIdentifier = source.getUserProvidedIdentifier();
        return userProvidedIdentifier != null ? userProvidedIdentifier : this.toIdentifier(NamingHelper.withCharset(source.getBuildingContext().getBuildingOptions().getSchemaCharset()).generateHashedFkName("FK", source.getTableName(), source.getReferencedTableName(), source.getColumnNames()), source.getBuildingContext());
    }

    public Identifier determineUniqueKeyName(ImplicitUniqueKeyNameSource source) {

        var userProvidedIdentifier = source.getUserProvidedIdentifier();
        return userProvidedIdentifier != null ? userProvidedIdentifier : this.toIdentifier(NamingHelper.withCharset(source.getBuildingContext().getBuildingOptions().getSchemaCharset()).generateHashedConstraintName("UK", source.getTableName(), source.getColumnNames()), source.getBuildingContext());
    }

    public Identifier determineIndexName(ImplicitIndexNameSource source) {

        var userProvidedIdentifier = source.getUserProvidedIdentifier();
        return userProvidedIdentifier != null ? userProvidedIdentifier : this.toIdentifier(NamingHelper.withCharset(source.getBuildingContext().getBuildingOptions().getSchemaCharset()).generateHashedConstraintName("IDX", source.getTableName(), source.getColumnNames()), source.getBuildingContext());
    }

    protected String transformAttributePath(AttributePath attributePath) {
        return attributePath.getProperty();
    }

    protected Identifier toIdentifier(String stringForm, MetadataBuildingContext buildingContext) {

        var attr = convertAuditAtributes(stringForm);
        log.info("CUSTOM STRG --> In: {}, Out: {}", stringForm, attr);
        return buildingContext.getMetadataCollector().getDatabase().getJdbcEnvironment().getIdentifierHelper().toIdentifier(attr);
    }

    private String convertAuditAtributes(final String stringForm) {
        switch (stringForm.toLowerCase()) {
            case "createdby":
                return "createdby_codi";
            case "createddate":
                return "createdDate";
            case "lastmodifiedby":
                return "lastmodifiedby_codi";
            case "lastmodifieddate":
                return "lastModifiedDate";
            default:
                return stringForm;
        }
    }
}
