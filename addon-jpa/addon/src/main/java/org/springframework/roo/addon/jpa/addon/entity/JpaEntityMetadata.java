package org.springframework.roo.addon.jpa.addon.entity;

import static org.springframework.roo.model.GoogleJavaType.GAE_DATASTORE_KEY;
import static org.springframework.roo.model.JavaType.LONG_OBJECT;
import static org.springframework.roo.model.JdkJavaType.BIG_DECIMAL;
import static org.springframework.roo.model.JpaJavaType.*;

import java.lang.reflect.Modifier;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.roo.addon.jpa.addon.identifier.Identifier;
import org.springframework.roo.addon.jpa.annotations.entity.JpaRelationType;
import org.springframework.roo.addon.jpa.annotations.entity.RooJpaEntity;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.PhysicalTypeIdentifierNamingUtils;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.*;
import org.springframework.roo.classpath.details.annotations.*;
import org.springframework.roo.classpath.itd.AbstractItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.classpath.itd.InvocableMemberBodyBuilder;
import org.springframework.roo.classpath.operations.Cardinality;
import org.springframework.roo.classpath.operations.InheritanceType;
import org.springframework.roo.classpath.scanner.MemberDetails;
import org.springframework.roo.metadata.MetadataItem;
import org.springframework.roo.model.*;
import org.springframework.roo.project.LogicalPath;

/**
 * The metadata for a JPA entity's *_Roo_Jpa_Entity.aj ITD.
 *
 * @author Andrew Swan
 * @author Juan Carlos García
 * @author Jose Manuel Vivó
 * @since 1.2.0
 */
public class JpaEntityMetadata extends AbstractItdTypeDetailsProvidingMetadataItem {

  private static final String PROVIDES_TYPE_STRING = JpaEntityMetadata.class.getName();


  /**
   * Array of supported JPA-relationship annotations
   */
  private static final JavaType[] JPA_ANNOTATIONS_SUPPORTED = {JpaJavaType.ONE_TO_ONE,
      JpaJavaType.ONE_TO_MANY, JpaJavaType.MANY_TO_MANY};

  private static final JavaSymbolName MAPPEDBY_ATTRIBUTE = new JavaSymbolName("mappedBy");


  /**
   * prefix for add/remove method names
   */
  private static final String REMOVE_METHOD_PREFIX = "removeFrom";
  private static final String ADD_METHOD_PREFIX = "addTo";

  /**
   * Suffix for add/remove method parameter names
   */
  private static final String REMOVE_PARAMETER_SUFFIX = "ToRemove";
  private static final String ADD_PARAMETER_SUFFIX = "ToAdd";



  private final JpaEntityAnnotationValues annotationValues;
  private final MemberDetails entityMemberDetails;
  private final Identifier identifier;
  private final JpaEntityMetadata parent;
  private FieldMetadata identifierField;
  private FieldMetadata versionField;
  private ClassOrInterfaceTypeDetails entityDetails;

  private final Map<String, RelationInfo> relationInfos;

  private final Map<String, RelationInfo> relationInfosByMappedBy;

  private final Map<String, FieldMetadata> relationsAsChild;

  private final FieldMetadata compositionRelationField;

  public static JavaType getJavaType(final String metadataIdentificationString) {
    return PhysicalTypeIdentifierNamingUtils.getJavaType(PROVIDES_TYPE_STRING,
        metadataIdentificationString);
  }

  public static String createIdentifier(final JavaType javaType, final LogicalPath path) {
    return PhysicalTypeIdentifierNamingUtils.createIdentifier(PROVIDES_TYPE_STRING, javaType, path);
  }

  public static String createIdentifier(ClassOrInterfaceTypeDetails details) {
    final LogicalPath logicalPath =
        PhysicalTypeIdentifier.getPath(details.getDeclaredByMetadataId());
    return createIdentifier(details.getType(), logicalPath);
  }

  /**
   * Constructor
   *
   * @param metadataIdentificationString the JPA_ID of this
   *            {@link MetadataItem}
   * @param itdName the ITD's {@link JavaType} (required)
   * @param entityPhysicalType the entity's physical type (required)
   * @param parent can be <code>null</code> if none of the governor's
   *            ancestors provide {@link JpaEntityMetadata}
   * @param entityMemberDetails details of the entity's members (required)
   * @param identifier information about the entity's identifier field in the
   *            event that the annotation doesn't provide such information;
   *            can be <code>null</code>
   * @param annotationValues the effective annotation values taking into
   *            account the presence of a {@link RooJpaEntity} annotation (required)
   * @param entityDetails
   * @param fieldsRelationAsParent fields that declares a relation which current entity is the parent part
   * @param fieldsRelationAsChild fields that declares a relation which current entity is the child part
   * @param compositionRelationField field that declares a composition relation which current entity is the child part
   */
  public JpaEntityMetadata(final String metadataIdentificationString, final JavaType itdName,
      final PhysicalTypeMetadata entityPhysicalType, final JpaEntityMetadata parent,
      final MemberDetails entityMemberDetails, final Identifier identifier,
      final JpaEntityAnnotationValues annotationValues,
      final ClassOrInterfaceTypeDetails entityDetails, List<FieldMetadata> fieldsRelationAsParent,
      Map<String, FieldMetadata> fieldsRelationAsChild, FieldMetadata compositionRelationField) {
    super(metadataIdentificationString, itdName, entityPhysicalType);
    Validate.notNull(annotationValues, "Annotation values are required");
    Validate.notNull(entityMemberDetails, "Entity MemberDetails are required");

    /*
     * Ideally we'd pass these parameters to the methods below rather than
     * storing them in fields, but this isn't an option due to various calls
     * to the parent entity.
     */
    this.annotationValues = annotationValues;
    this.entityMemberDetails = entityMemberDetails;
    this.identifier = identifier;
    this.parent = parent;
    this.entityDetails = entityDetails;

    // Add @Entity or @MappedSuperclass annotation
    builder
        .addAnnotation(annotationValues.isMappedSuperclass() ? getTypeAnnotation(MAPPED_SUPERCLASS)
            : getEntityAnnotation());

    // Add @Table annotation if required
    builder.addAnnotation(getTableAnnotation());

    // Add @Inheritance annotation if required
    builder.addAnnotation(getInheritanceAnnotation());

    // Add @DiscriminatorColumn if required
    builder.addAnnotation(getDiscriminatorColumnAnnotation());

    // Ensure there's a no-arg constructor (explicit or default)
    builder.addConstructor(getNoArgConstructor());

    // Add identifier field and accessor
    identifierField = getIdentifierField();
    builder.addField(identifierField);

    MethodMetadataBuilder identifierAccessor = getIdentifierAccessor();
    if (identifierAccessor != null) {
      builder.addMethod(identifierAccessor);
    }

    MethodMetadataBuilder identifierMutator = getIdentifierMutator();
    if (identifierMutator != null) {
      builder.addMethod(identifierMutator);
    }
    // Add version field and accessor
    versionField = getVersionField();
    builder.addField(versionField);

    MethodMetadataBuilder versionAccessor = getVersionAccessor();
    if (versionAccessor != null) {
      builder.addMethod(versionAccessor);
    }

    MethodMetadataBuilder versionMutator = getVersionMutator();
    if (versionMutator != null) {
      builder.addMethod(versionMutator);
    }

    // Manage relations

    MethodMetadata addMethod, removeMethod;
    Cardinality cardinality;
    RelationInfo info;
    JavaType childType;
    JavaSymbolName addMethodName, removeMethodName;
    AnnotationMetadata jpaAnnotation;
    String fieldName;
    JpaRelationType relationType;
    AnnotationAttributeValue<?> relationTypeAttribute;

    Map<String, RelationInfo> fieldInfosTemporal = new TreeMap<String, RelationInfo>();
    Map<String, RelationInfo> fieldInfosMappedByTemporal = new TreeMap<String, RelationInfo>();
    ImportRegistrationResolver importResolver = builder.getImportRegistrationResolver();

    // process fields which this entity is parent part
    for (FieldMetadata field : fieldsRelationAsParent) {

      fieldName = field.getFieldName().getSymbolName();
      // Get cardinality
      jpaAnnotation = getJpaRelaationAnnotation(field);
      cardinality = getFieldCardinality(jpaAnnotation);
      Validate
          .notNull(
              cardinality,
              "Field '%s.%s' is annotated with @%s but this annotation only can be used in @OneToOne, @OneToMany or @ManyToMany field",
              governorPhysicalTypeMetadata.getType(), fieldName,
              RooJavaType.ROO_JPA_RELATION.getSimpleTypeName());

      // Get child type
      if (cardinality == Cardinality.ONE_TO_ONE) {
        childType = field.getFieldType();
      } else {
        childType = field.getFieldType().getBaseType();
      }

      // Get mappedBy annotation attribute
      String mappedBy = getMappedByValue(jpaAnnotation);
      Validate.notNull(mappedBy, "Missing 'mappedBy' attribute on @%s annotation of %s.%s field",
          jpaAnnotation.getAnnotationType(), governorPhysicalTypeMetadata.getType(),
          field.getFieldName());

      // Prepare methods
      addMethodName = getAddMethodName(field);
      removeMethodName = getRemoveMethodName(field);
      addMethod =
          getAddValueMethod(addMethodName, field, cardinality, childType, mappedBy,
              removeMethodName, importResolver);
      removeMethod =
          getRemoveMethod(removeMethodName, field, cardinality, childType, mappedBy, importResolver);

      // Add to ITD builder
      ensureGovernorHasMethod(new MethodMetadataBuilder(addMethod));
      ensureGovernorHasMethod(new MethodMetadataBuilder(removeMethod));

      relationTypeAttribute =
          field.getAnnotation(RooJavaType.ROO_JPA_RELATION).getAttribute("type");
      if (relationTypeAttribute == null) {
        relationType = JpaRelationType.AGGREGATION;
      } else {
        relationType =
            JpaRelationType.valueOf(((EnumDetails) relationTypeAttribute.getValue()).getField()
                .getSymbolName());
      }

      // Store info in temporal maps
      info =
          new RelationInfo(fieldName, addMethod, removeMethod, cardinality, childType, field,
              mappedBy, relationType);
      fieldInfosTemporal.put(fieldName, info);
      // Use ChildType+childField as keys to avoid overried values
      // (the same mappedBy on many relations. Ej. Order.customer and Invoice.customer)
      fieldInfosMappedByTemporal.put(
          getMappedByInfoKey(field.getFieldType().getBaseType(), mappedBy), info);

    }

    // store final info unmodifiable map
    this.relationsAsChild = Collections.unmodifiableMap(fieldsRelationAsChild);
    relationInfos = Collections.unmodifiableMap(fieldInfosTemporal);
    relationInfosByMappedBy = Collections.unmodifiableMap(fieldInfosMappedByTemporal);
    this.compositionRelationField = compositionRelationField;


    // Build the ITD based on what we added to the builder above
    itdTypeDetails = builder.build();
  }

  /**
   * Get key to use to locate a value on {@link #relationInfosByMappedBy}.
   *
   * This is due to _mappedBy_ value usually is the same between relations with
   * other entities (ej.: Order.customer, Invoice.customer, ContactNote.customer...)
   *
   * @param childEntity
   * @param mappedBy field value
   * @return
   */
  private String getMappedByInfoKey(JavaType childEntity, String mappedBy) {
    return childEntity.getFullyQualifiedTypeName() + "." + mappedBy;
  }

  /**
   * Generate method name to use for add method of selected field
   *
   * @param field
   * @return
   */
  private JavaSymbolName getAddMethodName(final FieldMetadata field) {
    final JavaSymbolName methodName =
        new JavaSymbolName(ADD_METHOD_PREFIX
            + StringUtils.capitalize(field.getFieldName().getSymbolName()));
    return methodName;
  }

  private AnnotationMetadata getDiscriminatorColumnAnnotation() {
    if (StringUtils.isNotBlank(annotationValues.getInheritanceType())
        && InheritanceType.SINGLE_TABLE.name().equals(annotationValues.getInheritanceType())) {
      // Theoretically not required based on @DiscriminatorColumn
      // JavaDocs, but Hibernate appears to fail if it's missing
      return getTypeAnnotation(DISCRIMINATOR_COLUMN);
    }
    return null;
  }

  /**
   * Generates the JPA @Entity annotation to be applied to the entity
   *
   * @return
   */
  private AnnotationMetadata getEntityAnnotation() {
    AnnotationMetadata entityAnnotation = getTypeAnnotation(ENTITY);
    if (entityAnnotation == null) {
      return null;
    }

    if (StringUtils.isNotBlank(annotationValues.getEntityName())) {
      final AnnotationMetadataBuilder entityBuilder =
          new AnnotationMetadataBuilder(entityAnnotation);
      entityBuilder.addStringAttribute("name", annotationValues.getEntityName());
      entityAnnotation = entityBuilder.build();
    }

    return entityAnnotation;
  }

  /**
   * Locates the identifier accessor method.
   * <p>
   * If {@link #getIdentifierField()} returns a field created by this ITD or
   * if the field is declared within the entity itself, a public accessor will
   * automatically be produced in the declaring class.
   *
   * @return the accessor (never returns null)
   */
  private MethodMetadataBuilder getIdentifierAccessor() {
    if (parent != null) {
      return parent.getIdentifierAccessor();
    }

    // Locate the identifier field, and compute the name of the accessor
    // that will be produced
    JavaSymbolName requiredAccessorName = BeanInfoUtils.getAccessorMethodName(identifierField);

    // See if the user provided the field
    if (!getId().equals(identifierField.getDeclaredByMetadataId())) {
      // Locate an existing accessor
      final MethodMetadata method =
          entityMemberDetails.getMethod(requiredAccessorName, new ArrayList<JavaType>());
      if (method != null) {
        if (Modifier.isPublic(method.getModifier())) {
          // Method exists and is public so return it
          return new MethodMetadataBuilder(method);
        }

        // Method is not public so make the required accessor name
        // unique
        requiredAccessorName = new JavaSymbolName(requiredAccessorName.getSymbolName() + "_");
      }
    }

    // We declared the field in this ITD, so produce a public accessor for
    // it
    final InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
    bodyBuilder.appendFormalLine("return this." + identifierField.getFieldName().getSymbolName()
        + ";");

    MethodMetadataBuilder methodMetadata =
        new MethodMetadataBuilder(getId(), Modifier.PUBLIC, requiredAccessorName,
            identifierField.getFieldType(), bodyBuilder);

    // If method exists, return null to prevent method creation
    if (existsMethod(methodMetadata)) {
      return null;
    }

    return methodMetadata;
  }

  /**
  * Method that checks if method exists on Java target
  *
  * @param methodMetadata
  * @return
  */
  private boolean existsMethod(MethodMetadataBuilder methodMetadata) {
    MethodMetadata method = entityDetails.getMethod(methodMetadata.getMethodName());

    if (method == null) {
      return false;
    }

    return true;
  }

  private String getIdentifierColumn() {
    if (StringUtils.isNotBlank(annotationValues.getIdentifierColumn())) {
      return annotationValues.getIdentifierColumn();
    } else if (identifier != null && StringUtils.isNotBlank(identifier.getColumnName())) {
      return identifier.getColumnName();
    }
    return "";
  }

  /**
   * Locates the identifier field.
   * <p>
   * If a parent is defined, it must provide the field.
   * <p>
   * If no parent is defined, one will be located or created. Any declared or
   * inherited field which has the {@link javax.persistence.Id @Id} or
   * {@link javax.persistence.EmbeddedId @EmbeddedId} annotation will be taken
   * as the identifier and returned. If no such field is located, a private
   * field will be created as per the details contained in the
   * {@link RooJpaEntity} annotation, as applicable.
   *
   * @param parent (can be <code>null</code>)
   * @param project the user's project (required)
   * @param annotationValues
   * @param identifier can be <code>null</code>
   * @return the identifier (never returns null)
   */
  private FieldMetadata getIdentifierField() {
    if (parent != null) {
      final FieldMetadata idField = parent.getIdentifierField();
      if (idField != null) {
        if (MemberFindingUtils.getAnnotationOfType(idField.getAnnotations(), ID) != null) {
          return idField;
        } else if (MemberFindingUtils.getAnnotationOfType(idField.getAnnotations(), EMBEDDED_ID) != null) {
          return idField;
        }
      }
      return parent.getIdentifierField();
    }

    // Try to locate an existing field with @javax.persistence.Id
    final List<FieldMetadata> idFields = governorTypeDetails.getFieldsWithAnnotation(ID);
    if (!idFields.isEmpty()) {
      return getIdentifierField(idFields, ID);
    }

    // Try to locate an existing field with @javax.persistence.EmbeddedId
    final List<FieldMetadata> embeddedIdFields =
        governorTypeDetails.getFieldsWithAnnotation(EMBEDDED_ID);
    if (!embeddedIdFields.isEmpty()) {
      return getIdentifierField(embeddedIdFields, EMBEDDED_ID);
    }

    // Ensure there isn't already a field called "id"; if so, compute a
    // unique name (it's not really a fatal situation at the end of the day)
    final JavaSymbolName idField = governorTypeDetails.getUniqueFieldName(getIdentifierFieldName());

    // We need to create one
    final JavaType identifierType = getIdentifierType();

    final List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();
    final boolean hasIdClass =
        !(identifierType.isCoreType() || identifierType.equals(GAE_DATASTORE_KEY));
    final JavaType annotationType = hasIdClass ? EMBEDDED_ID : ID;
    annotations.add(new AnnotationMetadataBuilder(annotationType));

    // Compute the column name, as required
    if (!hasIdClass) {
      if (!"".equals(annotationValues.getSequenceName())) {

        // ROO-3719: Add SEQUENCE as @GeneratedValue strategy
        String identifierStrategy = annotationValues.getIdentifierStrategy();
        // Check if provided identifierStrategy is valid
        boolean isValidIdentifierStrategy = false;
        for (IdentifierStrategy identifierStrategyType : IdentifierStrategy.values()) {
          if (identifierStrategyType.name().equals(identifierStrategy)) {
            isValidIdentifierStrategy = true;
            break;
          }
        }

        if (!isValidIdentifierStrategy) {
          identifierStrategy = IdentifierStrategy.AUTO.name();
        }

        // ROO-746: Use @GeneratedValue(strategy = GenerationType.TABLE)
        // If the root of the governor declares @Inheritance(strategy =
        // InheritanceType.TABLE_PER_CLASS)
        if (IdentifierStrategy.AUTO.name().equals(identifierStrategy)) {
          AnnotationMetadata inheritance = governorTypeDetails.getAnnotation(INHERITANCE);
          if (inheritance == null) {
            inheritance = getInheritanceAnnotation();
          }
          if (inheritance != null) {
            final AnnotationAttributeValue<?> value =
                inheritance.getAttribute(new JavaSymbolName("strategy"));
            if (value instanceof EnumAttributeValue) {
              final EnumAttributeValue enumAttributeValue = (EnumAttributeValue) value;
              final EnumDetails details = enumAttributeValue.getValue();
              if (details != null && details.getType().equals(INHERITANCE_TYPE)) {
                if ("TABLE_PER_CLASS".equals(details.getField().getSymbolName())) {
                  identifierStrategy = IdentifierStrategy.TABLE.name();
                }
              }
            }
          }
        }

        final AnnotationMetadataBuilder generatedValueBuilder =
            new AnnotationMetadataBuilder(GENERATED_VALUE);
        generatedValueBuilder.addEnumAttribute("strategy", new EnumDetails(GENERATION_TYPE,
            new JavaSymbolName(identifierStrategy)));

        if (StringUtils.isNotBlank(annotationValues.getSequenceName())) {
          final String sequenceKey =
              StringUtils.uncapitalize(destination.getSimpleTypeName()) + "Gen";
          generatedValueBuilder.addStringAttribute("generator", sequenceKey);
          final AnnotationMetadataBuilder sequenceGeneratorBuilder =
              new AnnotationMetadataBuilder(SEQUENCE_GENERATOR);
          sequenceGeneratorBuilder.addStringAttribute("name", sequenceKey);
          sequenceGeneratorBuilder.addStringAttribute("sequenceName",
              annotationValues.getSequenceName());
          annotations.add(sequenceGeneratorBuilder);
        }
        annotations.add(generatedValueBuilder);
      }

      final String identifierColumn = StringUtils.stripToEmpty(getIdentifierColumn());
      String columnName = idField.getSymbolName();
      if (StringUtils.isNotBlank(identifierColumn)) {
        // User has specified an alternate column name
        columnName = identifierColumn;
      }

      final AnnotationMetadataBuilder columnBuilder = new AnnotationMetadataBuilder(COLUMN);
      columnBuilder.addStringAttribute("name", columnName);
      if (identifier != null && StringUtils.isNotBlank(identifier.getColumnDefinition())) {
        columnBuilder.addStringAttribute("columnDefinition", identifier.getColumnDefinition());
      }

      // Add length attribute for String field
      if (identifier != null && identifier.getColumnSize() > 0 && identifier.getColumnSize() < 4000
          && identifierType.equals(JavaType.STRING)) {
        columnBuilder.addIntegerAttribute("length", identifier.getColumnSize());
      }

      // Add precision and scale attributes for numeric field
      if (identifier != null
          && identifier.getScale() > 0
          && (identifierType.equals(JavaType.DOUBLE_OBJECT)
              || identifierType.equals(JavaType.DOUBLE_PRIMITIVE) || identifierType
                .equals(BIG_DECIMAL))) {
        columnBuilder.addIntegerAttribute("precision", identifier.getColumnSize());
        columnBuilder.addIntegerAttribute("scale", identifier.getScale());
      }

      annotations.add(columnBuilder);
    }

    return new FieldMetadataBuilder(getId(), Modifier.PRIVATE, annotations, idField, identifierType)
        .build();
  }

  private FieldMetadata getIdentifierField(final List<FieldMetadata> identifierFields,
      final JavaType identifierType) {
    Validate.isTrue(identifierFields.size() == 1,
        "More than one field was annotated with @%s in '%s'", identifierType.getSimpleTypeName(),
        destination.getFullyQualifiedTypeName());
    return new FieldMetadataBuilder(identifierFields.get(0)).build();
  }

  private String getIdentifierFieldName() {
    if (StringUtils.isNotBlank(annotationValues.getIdentifierField())) {
      return annotationValues.getIdentifierField();
    } else if (identifier != null && identifier.getFieldName() != null) {
      return identifier.getFieldName().getSymbolName();
    }
    // Use the default
    return RooJpaEntity.ID_FIELD_DEFAULT;
  }

  /**
   * Locates the identifier mutator method.
   * <p>
   * If {@link #getIdentifierField()} returns a field created by this ITD or
   * if the field is declared within the entity itself, a public mutator will
   * automatically be produced in the declaring class.
   *
   * @return the mutator (never returns null)
   */
  private MethodMetadataBuilder getIdentifierMutator() {
    // TODO: This is a temporary workaround to support web data binding
    // approaches; to be reviewed more thoroughly in future
    if (parent != null) {
      return parent.getIdentifierMutator();
    }

    // Locate the identifier field, and compute the name of the accessor
    // that will be produced
    JavaSymbolName requiredMutatorName = BeanInfoUtils.getMutatorMethodName(identifierField);

    final List<JavaType> parameterTypes = Arrays.asList(identifierField.getFieldType());
    final List<JavaSymbolName> parameterNames = Arrays.asList(new JavaSymbolName("id"));

    // See if the user provided the field
    if (!getId().equals(identifierField.getDeclaredByMetadataId())) {
      // Locate an existing mutator
      final MethodMetadata method =
          entityMemberDetails.getMethod(requiredMutatorName, parameterTypes);
      if (method != null) {
        if (Modifier.isPublic(method.getModifier())) {
          // Method exists and is public so return it
          return new MethodMetadataBuilder(method);
        }

        // Method is not public so make the required mutator name unique
        requiredMutatorName = new JavaSymbolName(requiredMutatorName.getSymbolName() + "_");
      }
    }

    // We declared the field in this ITD, so produce a public mutator for it
    final InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
    bodyBuilder.appendFormalLine("this." + identifierField.getFieldName().getSymbolName()
        + " = id;");

    MethodMetadataBuilder methodMetadata =
        new MethodMetadataBuilder(getId(), Modifier.PUBLIC, requiredMutatorName,
            JavaType.VOID_PRIMITIVE, AnnotatedJavaType.convertFromJavaTypes(parameterTypes),
            parameterNames, bodyBuilder);

    // If method exists, return null to prevent method creation
    if (existsMethod(methodMetadata)) {
      return null;
    }

    return methodMetadata;
  }

  /**
   * Returns the {@link JavaType} of the identifier field
   *
   * @param annotationValues the values of the {@link RooJpaEntity} annotation
   *            (required)
   * @param identifier can be <code>null</code>
   * @return a non-<code>null</code> type
   */
  private JavaType getIdentifierType() {
    if (annotationValues.getIdentifierType() != null) {
      return annotationValues.getIdentifierType();
    } else if (identifier != null && identifier.getFieldType() != null) {
      return identifier.getFieldType();
    }
    // Use the default
    return LONG_OBJECT;
  }

  /**
   * Returns the JPA @Inheritance annotation to be applied to the entity, if
   * applicable
   *
   * @param annotationValues the values of the {@link RooJpaEntity} annotation
   *            (required)
   * @return <code>null</code> if it's already present or not required
   */
  private AnnotationMetadata getInheritanceAnnotation() {
    if (governorTypeDetails.getAnnotation(INHERITANCE) != null) {
      return null;
    }
    if (StringUtils.isNotBlank(annotationValues.getInheritanceType())) {
      final AnnotationMetadataBuilder inheritanceBuilder =
          new AnnotationMetadataBuilder(INHERITANCE);
      inheritanceBuilder.addEnumAttribute("strategy", new EnumDetails(INHERITANCE_TYPE,
          new JavaSymbolName(annotationValues.getInheritanceType())));
      return inheritanceBuilder.build();
    }
    return null;
  }

  /**
   * Locates the no-arg constructor for this class, if available.
   * <p>
   * If a class defines a no-arg constructor, it is returned (irrespective of
   * access modifiers).
   * <p>
   * Otherwise, and if there is at least one other constructor declared in the
   * source file, this method creates one with public access.
   *
   * @return <code>null</code> if no constructor is to be produced
   */
  private ConstructorMetadataBuilder getNoArgConstructor() {
    // Search for an existing constructor
    final ConstructorMetadata existingExplicitConstructor =
        governorTypeDetails.getDeclaredConstructor(null);
    if (existingExplicitConstructor != null) {
      // Found an existing no-arg constructor on this class, so return it
      return new ConstructorMetadataBuilder(existingExplicitConstructor);
    }

    // To get this far, the user did not define a no-arg constructor
    if (governorTypeDetails.getDeclaredConstructors().isEmpty()) {
      // Java creates the default constructor => no need to add one
      return null;
    }

    // Create the constructor
    final InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
    bodyBuilder.appendFormalLine("super();");

    final ConstructorMetadataBuilder constructorBuilder = new ConstructorMetadataBuilder(getId());
    constructorBuilder.setBodyBuilder(bodyBuilder);
    constructorBuilder.setModifier(Modifier.PUBLIC);
    return constructorBuilder;
  }

  /**
   * Generates the JPA @Table annotation to be applied to the entity
   *
   * @param annotationValues
   * @return
   */
  private AnnotationMetadata getTableAnnotation() {
    final AnnotationMetadata tableAnnotation = getTypeAnnotation(TABLE);
    if (tableAnnotation == null) {
      return null;
    }
    final String catalog = annotationValues.getCatalog();
    final String schema = annotationValues.getSchema();
    final String table = annotationValues.getTable();
    if (StringUtils.isNotBlank(table) || StringUtils.isNotBlank(schema)
        || StringUtils.isNotBlank(catalog)) {
      final AnnotationMetadataBuilder tableBuilder = new AnnotationMetadataBuilder(tableAnnotation);
      if (StringUtils.isNotBlank(catalog)) {
        tableBuilder.addStringAttribute("catalog", catalog);
      }
      if (StringUtils.isNotBlank(schema)) {
        tableBuilder.addStringAttribute("schema", schema);
      }
      if (StringUtils.isNotBlank(table)) {
        tableBuilder.addStringAttribute("name", table);
      }
      return tableBuilder.build();
    }
    return null;
  }

  /**
   * Locates the version accessor method.
   * <p>
   * If {@link #getVersionField()} returns a field created by this ITD or if
   * the version field is declared within the entity itself, a public accessor
   * will automatically be produced in the declaring class.
   *
   * @param memberDetails
   * @return the version accessor (may return null if there is no version
   *         field declared in this class)
   */
  private MethodMetadataBuilder getVersionAccessor() {
    if (versionField == null) {
      // There's no version field, so there certainly won't be an accessor
      // for it
      return null;
    }

    if (parent != null) {
      final FieldMetadata result = parent.getVersionField();
      if (result != null) {
        // It's the parent's responsibility to provide the accessor, not
        // ours
        return parent.getVersionAccessor();
      }
    }

    // Compute the name of the accessor that will be produced
    JavaSymbolName requiredAccessorName = BeanInfoUtils.getAccessorMethodName(versionField);

    // See if the user provided the field
    if (!getId().equals(versionField.getDeclaredByMetadataId())) {
      // Locate an existing accessor
      final MethodMetadata method =
          entityMemberDetails.getMethod(requiredAccessorName, new ArrayList<JavaType>(), getId());
      if (method != null) {
        if (Modifier.isPublic(method.getModifier())) {
          // Method exists and is public so return it
          return new MethodMetadataBuilder(method);
        }

        // Method is not public so make the required accessor name
        // unique
        requiredAccessorName = new JavaSymbolName(requiredAccessorName.getSymbolName() + "_");
      }
    }

    // We declared the field in this ITD, so produce a public accessor for
    // it
    final InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
    bodyBuilder
        .appendFormalLine("return this." + versionField.getFieldName().getSymbolName() + ";");

    MethodMetadataBuilder methodMetadata =
        new MethodMetadataBuilder(getId(), Modifier.PUBLIC, requiredAccessorName,
            versionField.getFieldType(), bodyBuilder);

    // If method exists, return null to prevent method creation
    if (existsMethod(methodMetadata)) {
      return null;
    }

    return methodMetadata;
  }

  /**
   * Locates the version field.
   * <p>
   * If a parent is defined, it may provide the field.
   * <p>
   * If no parent is defined, one may be located or created. Any declared or
   * inherited field which is annotated with javax.persistence.Version will be
   * taken as the version and returned. If no such field is located, a private
   * field may be created as per the details contained in
   * {@link RooJpaEntity} annotation, as applicable.
   *
   * @return the version field (may be null)
   */
  private FieldMetadata getVersionField() {
    if (parent != null) {
      final FieldMetadata result = parent.getVersionField();
      if (result != null) {
        return result;
      }
    }

    // Try to locate an existing field with @Version
    final List<FieldMetadata> versionFields = governorTypeDetails.getFieldsWithAnnotation(VERSION);
    if (!versionFields.isEmpty()) {
      Validate.isTrue(versionFields.size() == 1,
          "More than 1 field was annotated with @Version in '%s'",
          destination.getFullyQualifiedTypeName());
      return versionFields.get(0);
    }

    // Quit at this stage if the user doesn't want a version field
    final String versionField = annotationValues.getVersionField();
    if ("".equals(versionField)) {
      return null;
    }

    // Ensure there isn't already a field called "version"; if so, compute a
    // unique name (it's not really a fatal situation at the end of the day)
    final JavaSymbolName verField = governorTypeDetails.getUniqueFieldName(versionField);

    // We're creating one
    JavaType versionType = annotationValues.getVersionType();
    String versionColumn =
        StringUtils.defaultIfEmpty(annotationValues.getVersionColumn(), verField.getSymbolName());

    final List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();
    annotations.add(new AnnotationMetadataBuilder(VERSION));

    final AnnotationMetadataBuilder columnBuilder = new AnnotationMetadataBuilder(COLUMN);
    columnBuilder.addStringAttribute("name", versionColumn);
    annotations.add(columnBuilder);

    return new FieldMetadataBuilder(getId(), Modifier.PRIVATE, annotations, verField, versionType)
        .build();
  }

  /**
   * Locates the version mutator method.
   * <p>
   * If {@link #getVersionField()} returns a field created by this ITD or if
   * the version field is declared within the entity itself, a public mutator
   * will automatically be produced in the declaring class.
   *
   * @return the mutator (may return null if there is no version field
   *         declared in this class)
   */
  private MethodMetadataBuilder getVersionMutator() {
    // TODO: This is a temporary workaround to support web data binding
    // approaches; to be reviewed more thoroughly in future
    if (parent != null) {
      return parent.getVersionMutator();
    }

    // Locate the version field, and compute the name of the mutator that
    // will be produced
    if (versionField == null) {
      // There's no version field, so there certainly won't be a mutator
      // for it
      return null;
    }

    // Compute the name of the mutator that will be produced
    JavaSymbolName requiredMutatorName = BeanInfoUtils.getMutatorMethodName(versionField);

    final List<JavaType> parameterTypes = Arrays.asList(versionField.getFieldType());
    final List<JavaSymbolName> parameterNames = Arrays.asList(new JavaSymbolName("version"));

    // See if the user provided the field
    if (!getId().equals(versionField.getDeclaredByMetadataId())) {
      // Locate an existing mutator
      final MethodMetadata method =
          entityMemberDetails.getMethod(requiredMutatorName, parameterTypes, getId());
      if (method != null) {
        if (Modifier.isPublic(method.getModifier())) {
          // Method exists and is public so return it
          return new MethodMetadataBuilder(method);
        }

        // Method is not public so make the required mutator name unique
        requiredMutatorName = new JavaSymbolName(requiredMutatorName.getSymbolName() + "_");
      }
    }

    // We declared the field in this ITD, so produce a public mutator for it
    final InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
    bodyBuilder.appendFormalLine("this." + versionField.getFieldName().getSymbolName()
        + " = version;");


    MethodMetadataBuilder methodMetadata =
        new MethodMetadataBuilder(getId(), Modifier.PUBLIC, requiredMutatorName,
            JavaType.VOID_PRIMITIVE, AnnotatedJavaType.convertFromJavaTypes(parameterTypes),
            parameterNames, bodyBuilder);

    // If method exists, return null to prevent method creation
    if (existsMethod(methodMetadata)) {
      return null;
    }

    return methodMetadata;
  }

  /**
   * Returns cardinality value based on JPA field annotations
   *
   * @param field
   * @return ONE_TO_MANY, MANY_TO_MANY, ONE_TO_ONE or null
   */
  private Cardinality getFieldCardinality(final AnnotationMetadata fieldJpaAnnotation) {
    Cardinality cardinality = null;
    if (JpaJavaType.ONE_TO_MANY.equals(fieldJpaAnnotation.getAnnotationType())) {
      cardinality = Cardinality.ONE_TO_MANY;
    } else if (JpaJavaType.MANY_TO_MANY.equals(fieldJpaAnnotation.getAnnotationType())) {
      cardinality = Cardinality.MANY_TO_MANY;
    } else if (JpaJavaType.ONE_TO_ONE.equals(fieldJpaAnnotation.getAnnotationType())) {
      cardinality = Cardinality.ONE_TO_ONE;
    }
    return cardinality;
  }

  /**
   * Returns JPA field annotations
   *
   * @param field
   * @return ONE_TO_MANY, MANY_TO_MANY, ONE_TO_ONE or null
   */
  private AnnotationMetadata getJpaRelaationAnnotation(final FieldMetadata field) {
    AnnotationMetadata annotation = null;
    for (JavaType type : JPA_ANNOTATIONS_SUPPORTED) {
      annotation = field.getAnnotation(type);
      if (annotation != null) {
        break;
      }
    }
    return annotation;
  }

  /**
   * Gets `mappedBy` attribute value from JPA relationship annotation
   *
   * @param jpaAnnotation
   * @return
   */
  private String getMappedByValue(AnnotationMetadata jpaAnnotation) {
    AnnotationAttributeValue<?> mappedByValue = jpaAnnotation.getAttribute(MAPPEDBY_ATTRIBUTE);
    if (mappedByValue == null) {
      return null;
    }
    return ((StringAttributeValue) mappedByValue).getValue();
  }

  /**
   * Create remove method to handle referenced relation
   *
   * @param removeMethodName
   * @param field
   * @param cardinality
   * @param childType
   * @param mappedBy
   * @param importResolver
   * @return method metadata or null if method already in class
   */
  private MethodMetadata getRemoveMethod(final JavaSymbolName removeMethodName,
      final FieldMetadata field, final Cardinality cardinality, final JavaType childType,
      final String mappedBy, ImportRegistrationResolver importResolver) {

    // Identify parameters types and names (if any)
    final List<JavaType> parameterTypes = new ArrayList<JavaType>(1);
    final List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>(1);
    if (cardinality != Cardinality.ONE_TO_ONE) {
      parameterTypes.add(JavaType.iterableOf(childType));
      parameterNames.add(new JavaSymbolName(field.getFieldName().getSymbolName()
          + REMOVE_PARAMETER_SUFFIX));
    }

    // See if the type itself declared the method
    MethodMetadata existingMethod = getGovernorMethod(removeMethodName, parameterTypes);
    if (existingMethod != null) {
      return existingMethod;
    }


    final InvocableMemberBodyBuilder builder = new InvocableMemberBodyBuilder();

    if (cardinality == Cardinality.ONE_TO_ONE) {
      buildRemoveOneToOneBody(field, mappedBy, builder);
    } else if (cardinality == Cardinality.ONE_TO_MANY) {
      buildRemoveOneToManyBody(field, mappedBy, parameterNames.get(0), childType, builder,
          importResolver);
    } else {
      // ManyToMany
      buildRemoveManyToManyBody(field, mappedBy, parameterNames.get(0), childType, builder,
          importResolver);
    }

    return new MethodMetadataBuilder(getId(), Modifier.PUBLIC, removeMethodName,
        JavaType.VOID_PRIMITIVE, AnnotatedJavaType.convertFromJavaTypes(parameterTypes),
        parameterNames, builder).build();
  }

  /**
   * Build remove method body for OneToOne relation
   *
   * @param field
   * @param mappedBy
   * @param builder
   */
  private void buildRemoveOneToOneBody(final FieldMetadata field, final String mappedBy,
      final InvocableMemberBodyBuilder builder) {
    final String fieldName = field.getFieldName().getSymbolName();
    // Build toString method body

    /*
     * if (this.{prop} != null) {
     *   {prop}.set{mappedBy}(null);
     * }
     * this.{prop} = null;
     */

    // if (this.{prop} != null) {
    builder.appendFormalLine(String.format("if (this.%s != null) {", fieldName));

    // {prop}.set{mappedBy}(null);
    builder.indent();
    builder.appendFormalLine(String.format("%s.set%s(null);", fieldName,
        StringUtils.capitalize(mappedBy)));

    // }
    builder.indentRemove();
    builder.appendFormalLine("}");

    // this.{prop} = null;
    builder.appendFormalLine(String.format("this.%s = null;", fieldName));
  }

  /**
   * Build remove method body for OneToMany relation
   *
   * @param field
   * @param mappedBy
   * @param parameterName
   * @param childType
   * @param builder
   * @param importResolver
   */
  private void buildRemoveOneToManyBody(final FieldMetadata field, final String mappedBy,
      JavaSymbolName parameterName, final JavaType childType,
      final InvocableMemberBodyBuilder builder, ImportRegistrationResolver importResolver) {
    final String fieldName = field.getFieldName().getSymbolName();
    // Build method body

    /*
     *  Assert.notNull({param}, "The given Iterable of items to remove can't be null!");
     * for ({childType} item : {param}) {
     *   this.{field}.remove(item);
     *   item.set{mappedBy}(null);
     * }
     */

    importResolver.addImport(SpringJavaType.ASSERT);

    // Assert.notNull({param}, "The given Iterable of items to remove can't be null!");
    builder.appendFormalLine(String.format(
        "Assert.notNull(%s, \"The given Iterable of items to remove can't be null!\");",
        parameterName));

    // for ({childType} item : {param}) {
    builder.appendFormalLine(String.format("for (%s item : %s) {", childType.getSimpleTypeName(),
        parameterName));

    // this.{field}.remove(item);
    builder.indent();
    builder.appendFormalLine(String.format("this.%s.remove(item);", fieldName));

    // item.set{mappedBy}(null);
    builder.appendFormalLine(String.format("item.set%s(null);", StringUtils.capitalize(mappedBy)));

    // }
    builder.indentRemove();
    builder.appendFormalLine("}");
  }

  /**
   * Build remove method body for ManyToMany relation
   *
   * @param field
   * @param mappedBy
   * @param parameterName
   * @param childType
   * @param builder
   * @param importResolver
   */
  private void buildRemoveManyToManyBody(final FieldMetadata field, final String mappedBy,
      JavaSymbolName parameterName, final JavaType childType,
      final InvocableMemberBodyBuilder builder, ImportRegistrationResolver importResolver) {
    final String fieldName = field.getFieldName().getSymbolName();
    // Build method body

    /*
     * Assert.notNull({param}, "The given Iterable of items to remove can't be null!");
     * for ({childType} item : {param}) {
     *   this.{field}.remove(item);
     *   item.get{mappedBy}().remove(this);
     * }
     */

    importResolver.addImport(SpringJavaType.ASSERT);

    // Assert.notNull({param}, "The given Iterable of items to remove can't be null!");
    builder.appendFormalLine(String.format(
        "Assert.notNull(%s, \"The given Iterable of items to remove can't be null!\");",
        parameterName));

    // for ({childType} item : {param}) {
    builder.appendFormalLine(String.format("for (%s item : %s) {", childType.getSimpleTypeName(),
        parameterName));

    // this.{field}.remove(item);
    builder.indent();
    builder.appendFormalLine(String.format("this.%s.remove(item);", fieldName));

    // item.get{mappedBy}().remove(this);
    builder.appendFormalLine(String.format("item.get%s().remove(this);",
        StringUtils.capitalize(mappedBy)));

    // }
    builder.indentRemove();
    builder.appendFormalLine("}");
  }

  /**
   * Generate method name to use for remove method of selected field
   *
   * @param field
   * @return
   */
  private JavaSymbolName getRemoveMethodName(final FieldMetadata field) {
    final JavaSymbolName methodName =
        new JavaSymbolName(REMOVE_METHOD_PREFIX
            + StringUtils.capitalize(field.getFieldName().getSymbolName()));
    return methodName;
  }

  /**
   * Create add method to handle referenced relation
   *
   * @param addMethodName
   * @param field
   * @param cardinality
   * @param childType
   * @param mappedBy
   * @param removeMethodName
   * @param importResolver
   * @return method metadata or null if method already in class
   */
  private MethodMetadata getAddValueMethod(final JavaSymbolName addMethodName,
      final FieldMetadata field, final Cardinality cardinality, final JavaType childType,
      final String mappedBy, final JavaSymbolName removeMethodName,
      ImportRegistrationResolver importResolver) {

    // Identify parameters type and name
    final List<JavaType> parameterTypes = new ArrayList<JavaType>(1);
    final List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>(1);
    if (cardinality == Cardinality.ONE_TO_ONE) {
      parameterTypes.add(childType);
      parameterNames.add(field.getFieldName());
    } else {
      parameterTypes.add(JavaType.iterableOf(childType));
      parameterNames.add(new JavaSymbolName(field.getFieldName().getSymbolName()
          + ADD_PARAMETER_SUFFIX));
    }

    // See if the type itself declared the method
    MethodMetadata existingMethod = getGovernorMethod(addMethodName, parameterTypes);
    if (existingMethod != null) {
      return existingMethod;
    }


    final InvocableMemberBodyBuilder builder = new InvocableMemberBodyBuilder();

    if (cardinality == Cardinality.ONE_TO_ONE) {
      buildAddOneToOneBody(field, mappedBy, parameterNames.get(0), childType, removeMethodName,
          builder);
    } else if (cardinality == Cardinality.ONE_TO_MANY) {
      buildAddOneToManyBody(field, mappedBy, parameterNames.get(0), childType, builder,
          importResolver);
    } else {
      buildAddManyToManyBody(field, mappedBy, parameterNames.get(0), childType, builder,
          importResolver);
    }

    return new MethodMetadataBuilder(getId(), Modifier.PUBLIC, addMethodName,
        JavaType.VOID_PRIMITIVE, AnnotatedJavaType.convertFromJavaTypes(parameterTypes),
        parameterNames, builder).build();
  }

  /**
   * Build add method body for OneToOne relation
   *
   * @param field
   * @param mappedBy
   * @param parameter
   * @param childType
   * @param removeMethodName
   * @param builder
   */
  private void buildAddOneToOneBody(final FieldMetadata field, final String mappedBy,
      final JavaSymbolName parameter, final JavaType childType,
      final JavaSymbolName removeMethodName, InvocableMemberBodyBuilder builder) {

    final String fieldName = field.getFieldName().getSymbolName();
    // Build method body

    /*
     * if ({param} == null) {
     *   {removeMethod}();
     * } else {
     *   this.{field} = {param};
     *   {param}.set{mappedBy}(this);
     * }
     */

    // if ({param} == null) {
    builder.appendFormalLine(String.format("if (%s == null) {", parameter));

    // {removeMethod}();
    builder.indent();
    builder.appendFormalLine(String.format("%s();", removeMethodName));

    // } else {
    builder.indentRemove();
    builder.appendFormalLine("} else {");

    // this.{field} = {param};
    builder.indent();
    builder.appendFormalLine(String.format("this.%s = %s;", fieldName, parameter));

    // {param}.set{mappedBy}(this);
    builder.appendFormalLine(String.format("%s.set%s(this);", parameter,
        StringUtils.capitalize(mappedBy)));

    // }
    builder.indentRemove();
    builder.appendFormalLine("}");

  }

  /**
   * Build add method body for OneToMany relation
   *
   * @param field
   * @param mappedBy
   * @param parameterName
   * @param childType
   * @param builder
   * @param importResolver
   */
  private void buildAddOneToManyBody(final FieldMetadata field, final String mappedBy,
      final JavaSymbolName parameterName, final JavaType childType,
      final InvocableMemberBodyBuilder builder, ImportRegistrationResolver importResolver) {
    final String fieldName = field.getFieldName().getSymbolName();
    // Build method body

    /*
     * Assert.notNull({param}, "The given Iterable of items to add can't be null!");
     * for ({childType} item : {param}) {
     *   this.{field}.add(item);
     *   item.set{mappedBy}(this);
     * }
     */

    importResolver.addImport(SpringJavaType.ASSERT);

    // Assert.notNull({param}, "The given Iterable of items to add can't be null!");
    builder.appendFormalLine(String
        .format("Assert.notNull(%s, \"The given Iterable of items to add can't be null!\");",
            parameterName));

    // for ({childType} item : {param}) {
    builder.appendFormalLine(String.format("for (%s item : %s) {", childType.getSimpleTypeName(),
        parameterName));

    // this.{field}.remove(item);
    builder.indent();
    builder.appendFormalLine(String.format("this.%s.add(item);", fieldName));

    // item.set{mappedBy}(null);
    builder.appendFormalLine(String.format("item.set%s(this);", StringUtils.capitalize(mappedBy)));

    // }
    builder.indentRemove();
    builder.appendFormalLine("}");

  }

  /**
   * Build add method body for ManyToMany relation
   *
   * @param field
   * @param mappedBy
   * @param parameterName
   * @param childType
   * @param builder
   * @param importResolver
   */
  private void buildAddManyToManyBody(final FieldMetadata field, final String mappedBy,
      final JavaSymbolName parameterName, final JavaType childType,
      final InvocableMemberBodyBuilder builder, ImportRegistrationResolver importResolver) {
    final String fieldName = field.getFieldName().getSymbolName();
    // Build method body

    /*
     * Assert.notNull({param}, "The given Iterable of items to add can't be null!");
     * for ({childType} item : {param}) {
     *   this.{field}.add(item);
     *   item.get{mappedBy}().add(this);
     * }
     */

    importResolver.addImport(SpringJavaType.ASSERT);

    // Assert.notNull({param}, "The given Iterable of items to add can't be null!");
    builder.appendFormalLine(String
        .format("Assert.notNull(%s, \"The given Iterable of items to add can't be null!\");",
            parameterName));

    // for ({childType} item : {param}) {
    builder.appendFormalLine(String.format("for (%s item : %s) {", childType.getSimpleTypeName(),
        parameterName));

    // this.{field}.remove(item);
    builder.indent();
    builder.appendFormalLine(String.format("this.%s.add(item);", fieldName));

    // item.get{mappedBy}().add(this);
    builder.appendFormalLine(String.format("item.get%s().add(this);",
        StringUtils.capitalize(mappedBy)));

    // }
    builder.indentRemove();
    builder.appendFormalLine("}");

  }

  /**
   * @return information about relations which current entity is parent. Map key is current entity field name
   */
  public Map<String, RelationInfo> getRelationInfos() {
    return relationInfos;
  }

  /**
   * @return information about relations which current entity is parent. Map key is child entity plus related field name
   * @see #getRelationInfosByMappedBy(JavaType, String)
   */
  public Map<String, RelationInfo> getRelationInfosByMappedBy() {
    return relationInfosByMappedBy;
  }

  /**
   * @return information about relations which current entity is parent.
   */
  public RelationInfo getRelationInfosByMappedBy(JavaType childType, String childFieldName) {
    return relationInfosByMappedBy.get(getMappedByInfoKey(childType, childFieldName));
  }

  /**
   * @return fields declared on entity which entity is child part.
   */
  public Map<String, FieldMetadata> getRelationsAsChild() {
    return relationsAsChild;
  }

  /**
   * @return information about current identifier field
   */
  public FieldMetadata getCurrentIndentifierField() {
    return identifierField;
  }

  /**
   * @return information about current version field
   */
  public FieldMetadata getCurrentVersionField() {
    return versionField;
  }

  public boolean isReadOnly() {
    return annotationValues.isReadOnly();
  }

  /**
   * @return true if this entity is the child part of a composition relation
   */
  public boolean isCompositionChild() {
    return compositionRelationField != null;
  }

  /**
   * @return field of current entity which defines the child part of a composition relation
   */
  public FieldMetadata getCompositionRelationField() {
    return compositionRelationField;
  }

  /**
   * = _RelationInfo_
   *
   * *Immutable* information about a managed relation
   *
   * @author Jose Manuel Vivó
   * @since 2.0.0
   */
  public static class RelationInfo implements Comparable<RelationInfo> {

    /**
     * relation field name
     */
    public final String fieldName;


    /**
     * Method to use to add/set a item
     */
    public final MethodMetadata addMethod;

    /**
     * Method to use to remove/clean a item
     */
    public final MethodMetadata removeMethod;

    /**
     * Relationship carinality
     */
    public final Cardinality cardinality;

    /**
     * Child item type
     */
    public final JavaType childType;

    /**
     * parent field metadata
     */
    public final FieldMetadata fieldMetadata;

    /**
     * Child field name
     */
    public final String mappedBy;

    /**
     * Relation type (Aggregation/Composition)
     */
    public final JpaRelationType type;

    /**
     * Constructor
     *
     * @param fieldName
     * @param addMethod
     * @param removeMethod
     * @param cardinality
     * @param childType
     * @param fieldMetadata
     * @param mappedBy
     * @param type
     */
    protected RelationInfo(final String fieldName, final MethodMetadata addMethod,
        final MethodMetadata removeMethod, final Cardinality cardinality, final JavaType childType,
        final FieldMetadata fieldMetadata, final String mappedBy, final JpaRelationType type) {
      super();
      this.fieldName = fieldName;
      this.addMethod = addMethod;
      this.removeMethod = removeMethod;
      this.cardinality = cardinality;
      this.childType = childType;
      this.fieldMetadata = fieldMetadata;
      this.mappedBy = mappedBy;
      this.type = type;
    }

    @Override
    public int compareTo(RelationInfo o) {
      return fieldName.compareTo(o.fieldName);
    }
  }
}
