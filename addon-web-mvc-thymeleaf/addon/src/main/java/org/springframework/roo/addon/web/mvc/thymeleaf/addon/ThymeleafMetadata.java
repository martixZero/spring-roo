package org.springframework.roo.addon.web.mvc.thymeleaf.addon;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.roo.addon.jpa.addon.entity.JpaEntityMetadata;
import org.springframework.roo.addon.jpa.addon.entity.JpaEntityMetadata.RelationInfo;
import org.springframework.roo.addon.layers.service.addon.ServiceMetadata;
import org.springframework.roo.addon.web.mvc.controller.addon.ControllerMetadata;
import org.springframework.roo.addon.web.mvc.controller.annotations.ControllerType;
import org.springframework.roo.addon.web.mvc.thymeleaf.annotations.RooThymeleaf;
import org.springframework.roo.classpath.PhysicalTypeIdentifierNamingUtils;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.ClassOrInterfaceTypeDetails;
import org.springframework.roo.classpath.details.ConstructorMetadata;
import org.springframework.roo.classpath.details.ConstructorMetadataBuilder;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.classpath.details.FieldMetadataBuilder;
import org.springframework.roo.classpath.details.MethodMetadata;
import org.springframework.roo.classpath.details.MethodMetadataBuilder;
import org.springframework.roo.classpath.details.annotations.AnnotatedJavaType;
import org.springframework.roo.classpath.details.annotations.AnnotationAttributeValue;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadata;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadataBuilder;
import org.springframework.roo.classpath.details.annotations.EnumAttributeValue;
import org.springframework.roo.classpath.details.annotations.StringAttributeValue;
import org.springframework.roo.classpath.itd.AbstractItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.classpath.itd.InvocableMemberBodyBuilder;
import org.springframework.roo.metadata.MetadataIdentificationUtils;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.model.JdkJavaType;
import org.springframework.roo.model.Jsr303JavaType;
import org.springframework.roo.model.SpringEnumDetails;
import org.springframework.roo.model.SpringJavaType;
import org.springframework.roo.model.SpringletsJavaType;
import org.springframework.roo.project.LogicalPath;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Metadata for {@link RooThymeleaf}.
 *
 * @author Juan Carlos García
 * @since 2.0
 */
public class ThymeleafMetadata extends AbstractItdTypeDetailsProvidingMetadataItem {

  private static final JavaType JODA_DATETIME_FORMAT_JAVA_TYPE = new JavaType(
      "org.joda.time.format.DateTimeFormat");
  private static final JavaSymbolName MESSAGE_SOURCE = new JavaSymbolName("messageSource");
  private static final AnnotationMetadata ANN_METADATA_MODEL_ATTRIBUTE =
      new AnnotationMetadataBuilder(SpringJavaType.MODEL_ATTRIBUTE).build();
  private static final JavaSymbolName LIST_URI_METHOD_NAME = new JavaSymbolName("listURI");
  private static final JavaSymbolName SHOW_URI_METHOD_NAME = new JavaSymbolName("showURI");
  private static final AnnotationMetadata ANN_METADATA_REQUEST_BODY =
      new AnnotationMetadataBuilder(SpringJavaType.REQUEST_BODY).build();
  private static final AnnotationMetadata ANN_METADATA_VALID = new AnnotationMetadataBuilder(
      Jsr303JavaType.VALID).build();
  private static final JavaSymbolName PAGEABLE_NAME = new JavaSymbolName("pageable");
  private static final JavaSymbolName GLOBAL_SEARCH_NAME = new JavaSymbolName("globalSearch");
  private static final JavaSymbolName CONSUMES_SYMBOL_NAME = new JavaSymbolName("consumes");
  private static final JavaSymbolName VALUE_SYMBOL_NAME = new JavaSymbolName("value");
  private static final JavaSymbolName PRODUCES_SYMBOL_NAME = new JavaSymbolName("produces");

  private static final String PROVIDES_TYPE_STRING = ThymeleafMetadata.class.getName();
  private static final String PROVIDES_TYPE = MetadataIdentificationUtils
      .create(PROVIDES_TYPE_STRING);

  private final boolean readOnly;
  private final ControllerMetadata controllerMetadata;
  private final ControllerType type;
  private final ConstructorMetadata constructor;
  private final ServiceMetadata serviceMetadata;
  private final JavaType entity;
  private final String entityPlural;
  private final List<Pair<RelationInfo, JpaEntityMetadata>> compositionRelationOneToOne;
  private final String entityItemName;
  private final JpaEntityMetadata entityMetadata;
  private final JavaType itemController;
  private final String entityIdentifierPlural;
  private final String entityIdentifier;

  // Common method
  private final MethodMetadata initBinderMethod;

  // Collection Methods
  private final MethodMetadata createFormMethod;
  private final MethodMetadata createMethod;
  private final MethodMetadata listMethod;
  private final MethodMetadata listURIMethod;
  private final MethodMetadata listDatatablesJSONMethod;

  // Item Methods
  private final MethodMetadata modelAttributeMethod;
  private final MethodMetadata editFormMethod;
  private final MethodMetadata updateMethod;
  private final MethodMetadata deleteMethod;
  private final MethodMetadata showMethod;
  private final MethodMetadata showURIMethod;
  private final MethodMetadata populateFormMethod;
  private final MethodMetadata populateFormatsMethod;

  //
  //  private MethodMetadata listFormMethod;
  //  private MethodMetadata deleteJSONMethod;
  //  private MethodMetadata deleteBatchMethod;
  //  private MethodMetadata createBatchMethod;
  //  private MethodMetadata updateBatchMethod;
  //  private List<MethodMetadata> finderMethods;
  //  private List<MethodMetadata> detailMethods;

  private final List<MethodMetadata> allMethods;
  private final FieldMetadata messageSourceField;
  private final String viewsPath;
  private final JavaType collectionController;
  private final List<FieldMetadata> dateTimeFields;
  private final List<FieldMetadata> enumFields;

  public static String createIdentifier(final JavaType javaType, final LogicalPath path) {
    return PhysicalTypeIdentifierNamingUtils.createIdentifier(PROVIDES_TYPE_STRING, javaType, path);
  }

  public static JavaType getJavaType(final String metadataIdentificationString) {
    return PhysicalTypeIdentifierNamingUtils.getJavaType(PROVIDES_TYPE_STRING,
        metadataIdentificationString);
  }

  public static String getMetadataIdentiferType() {
    return PROVIDES_TYPE;
  }

  public static LogicalPath getPath(final String metadataIdentificationString) {
    return PhysicalTypeIdentifierNamingUtils.getPath(PROVIDES_TYPE_STRING,
        metadataIdentificationString);
  }

  public static boolean isValid(final String metadataIdentificationString) {
    return PhysicalTypeIdentifierNamingUtils.isValid(PROVIDES_TYPE_STRING,
        metadataIdentificationString);
  }

  /**
   * Constructor
   *
   * @param identifier
   *            the identifier for this item of metadata (required)
   * @param aspectName
   *            the Java type of the ITD (required)
   * @param governorPhysicalTypeMetadata
   *            the governor, which is expected to contain a
   *            {@link ClassOrInterfaceTypeDetails} (required)
   * @param collectionController

   */
  public ThymeleafMetadata(final String identifier, final JavaType aspectName,
      final PhysicalTypeMetadata governorPhysicalTypeMetadata,
      ControllerMetadata controllerMetadata, ServiceMetadata serviceMetadata,
      JpaEntityMetadata entityMetadata, String entityPlural, String entityIdentifierPlural,
      final List<Pair<RelationInfo, JpaEntityMetadata>> compositionRelationOneToOne,
      final JavaType itemController, JavaType collectionController,
      List<FieldMetadata> dateTimeFields, List<FieldMetadata> enumFields) {
    super(identifier, aspectName, governorPhysicalTypeMetadata);


    this.readOnly = entityMetadata.isReadOnly();
    this.controllerMetadata = controllerMetadata;
    this.type = this.controllerMetadata.getType();
    this.serviceMetadata = serviceMetadata;
    this.entity = serviceMetadata.getEntity();
    this.entityMetadata = entityMetadata;
    this.entityIdentifier =
        entityMetadata.getCurrentIndentifierField().getFieldName().getSymbolName();
    this.entityIdentifierPlural = entityIdentifierPlural;
    this.entityItemName = StringUtils.uncapitalize(entity.getSimpleTypeName());
    this.entityPlural = entityPlural;
    this.compositionRelationOneToOne = compositionRelationOneToOne;
    this.itemController = itemController;
    this.collectionController = collectionController;
    this.dateTimeFields = Collections.unmodifiableList(dateTimeFields);
    this.enumFields = Collections.unmodifiableList(enumFields);
    this.viewsPath =
        controllerMetadata.getPath().startsWith("/") ? controllerMetadata.getPath().substring(1)
            : controllerMetadata.getPath();

    //Add @Controller
    ensureGovernorIsAnnotated(new AnnotationMetadataBuilder(SpringJavaType.CONTROLLER));
    // Add @RequestMapping
    ensureGovernorIsAnnotated(getRequestMappingAnnotation());

    this.messageSourceField = getMessageSourceField();
    ensureGovernorHasField(new FieldMetadataBuilder(this.messageSourceField));

    this.constructor = getConstructor();
    ensureGovernorHasConstructor(new ConstructorMetadataBuilder(constructor));

    List<MethodMetadata> allMethods = new ArrayList<MethodMetadata>();

    switch (this.type) {
      case COLLECTION:
        this.listMethod = addAndGet(getListMethod(), allMethods);
        this.listURIMethod = addAndGet(getListURIMethod(), allMethods);
        this.listDatatablesJSONMethod = addAndGet(getListDatatablesJSONMethod(), allMethods);

        if (readOnly) {
          this.initBinderMethod = null;
          this.populateFormMethod = null;
          this.populateFormatsMethod = null;
          this.createMethod = null;
          this.createFormMethod = null;
        } else {
          this.initBinderMethod = addAndGet(getInitBinderMethod(), allMethods);
          this.populateFormatsMethod = addAndGet(getPopulateFormatsMethod(), allMethods);
          this.populateFormMethod = addAndGet(getPopulateFormMethod(), allMethods);
          this.createMethod = addAndGet(getCreateMethod(), allMethods);
          this.createFormMethod = addAndGet(getCreateFormMethod(), allMethods);
        }


        this.modelAttributeMethod = null;
        this.editFormMethod = null;
        this.updateMethod = null;
        this.deleteMethod = null;
        this.showMethod = null;
        this.showURIMethod = null;
        break;

      case ITEM:
        this.modelAttributeMethod = addAndGet(getModelAttributeMethod(), allMethods);
        this.showMethod = addAndGet(getShowMethod(), allMethods);
        this.showURIMethod = addAndGet(getShowURIMethod(), allMethods);
        if (readOnly) {
          this.editFormMethod = null;
          this.updateMethod = null;
          this.deleteMethod = null;
          this.populateFormatsMethod = null;
          this.populateFormMethod = null;
        } else {
          this.populateFormatsMethod = addAndGet(getPopulateFormatsMethod(), allMethods);
          this.populateFormMethod = addAndGet(getPopulateFormMethod(), allMethods);
          this.editFormMethod = addAndGet(getEditFormMethod(), allMethods);
          this.updateMethod = addAndGet(getUpdateMethod(), allMethods);
          this.deleteMethod = addAndGet(getDeleteMethod(), allMethods);
        }


        this.listMethod = null;
        this.listURIMethod = null;
        this.listDatatablesJSONMethod = null;
        this.createMethod = null;
        this.createFormMethod = null;
        this.initBinderMethod = null;

        break;
      case SEARCH:
        // TODO

        this.listMethod = null;
        this.listURIMethod = null;
        this.listDatatablesJSONMethod = null;
        this.createMethod = null;
        this.createFormMethod = null;
        this.initBinderMethod = null;

        this.modelAttributeMethod = null;
        this.editFormMethod = null;
        this.updateMethod = null;
        this.deleteMethod = null;
        this.showMethod = null;
        this.showURIMethod = null;
        this.populateFormMethod = null;
        this.populateFormatsMethod = null;
        break;

      case DETAIL:
        // TODO

        this.listMethod = null;
        this.listURIMethod = null;
        this.listDatatablesJSONMethod = null;
        this.createMethod = null;
        this.createFormMethod = null;
        this.initBinderMethod = null;

        this.modelAttributeMethod = null;
        this.editFormMethod = null;
        this.updateMethod = null;
        this.deleteMethod = null;
        this.showMethod = null;
        this.showURIMethod = null;
        this.populateFormMethod = null;
        this.populateFormatsMethod = null;
        break;

      default:
        throw new IllegalArgumentException("Unsupported Controller type: " + this.type.name());
    }

    this.allMethods = Collections.unmodifiableList(allMethods);

    // Build the ITD
    itdTypeDetails = builder.build();
  }


  private FieldMetadata getMessageSourceField() {
    return new FieldMetadataBuilder(getId(), Modifier.PUBLIC,
        new ArrayList<AnnotationMetadataBuilder>(), MESSAGE_SOURCE, SpringJavaType.MESSAGE_SOURCE)
        .build();
  }

  private MethodMetadata addAndGet(MethodMetadata method, List<MethodMetadata> allMethods) {
    allMethods.add(method);
    ensureGovernorHasMethod(new MethodMetadataBuilder(method));
    return method;
  }

  private AnnotationMetadataBuilder getRequestMappingAnnotation() {
    List<AnnotationAttributeValue<?>> requestMappingAttributes =
        new ArrayList<AnnotationAttributeValue<?>>();

    String path = controllerMetadata.getPath();
    if (type == ControllerType.ITEM) {
      path = path + "/{" + entityItemName + "}";
    }
    // Adding path attribute
    requestMappingAttributes.add(new StringAttributeValue(VALUE_SYMBOL_NAME, path));

    requestMappingAttributes.add(new EnumAttributeValue(PRODUCES_SYMBOL_NAME,
        SpringEnumDetails.MEDIA_TYPE_TEXT_HTML_VALUE));

    return new AnnotationMetadataBuilder(SpringJavaType.REQUEST_MAPPING, requestMappingAttributes);
  }


  private ConstructorMetadata getConstructor() {
    InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();

    // Generating constructor
    ConstructorMetadataBuilder constructor = new ConstructorMetadataBuilder(getId());
    constructor.setModifier(Modifier.PUBLIC);
    constructor.addAnnotation(new AnnotationMetadataBuilder(SpringJavaType.AUTOWIRED));

    // Getting serviceFieldName
    String serviceFieldName = controllerMetadata.getServiceField().getFieldName().getSymbolName();
    constructor.addParameter(serviceFieldName, controllerMetadata.getService());
    bodyBuilder
        .appendFormalLine(String.format("this.%s = %s;", serviceFieldName, serviceFieldName));

    if (this.type == ControllerType.DETAIL) {

      // Getting parentServiceFieldName
      String childServiceFieldName =
          controllerMetadata.getDetailsServiceField().getFieldName().getSymbolName();

      // Adding parameters
      constructor.addParameter(childServiceFieldName, controllerMetadata.getDetailsService());

      // Generating body
      bodyBuilder.appendFormalLine(String.format("this.%s = %s;", childServiceFieldName,
          childServiceFieldName));
    }

    String messageSourceName = messageSourceField.getFieldName().getSymbolName();
    constructor.addParameter(messageSourceName, messageSourceField.getFieldType());
    bodyBuilder.appendFormalLine("this.%1$s = %1$s;", messageSourceName);


    // Adding body
    constructor.setBodyBuilder(bodyBuilder);

    return constructor.build();

  }

  /**
   * Creates listURI method
   *
   * @return
   */
  private MethodMetadata getListURIMethod() {
    // Define method parameter types and parameter names
    List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
    List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();

    MethodMetadata existingMethod =
        getGovernorMethod(LIST_URI_METHOD_NAME,
            AnnotatedJavaType.convertFromAnnotatedJavaTypes(parameterTypes));
    if (existingMethod != null) {
      return existingMethod;
    }

    // return MvcUriComponentsBuilder.fromMethodCall(
    //     MvcUriComponentsBuilder.on(CustomersCollectionThymeleafController.class).list(null))
    //     .build().encode();
    InvocableMemberBodyBuilder body = new InvocableMemberBodyBuilder();

    body.appendFormalLine("return %s.fromMethodCall(",
        getNameOfJavaType(SpringJavaType.MVC_URI_COMPONENTS_BUILDER));
    body.indent();
    body.indent();
    body.appendFormalLine("%s.on(%s.class).%s(null))",
        getNameOfJavaType(SpringJavaType.MVC_URI_COMPONENTS_BUILDER),
        getNameOfJavaType(getDestination()), this.listMethod.getMethodName());
    body.appendFormalLine(".build().encode();");
    body.reset();

    // Use the MethodMetadataBuilder for easy creation of MethodMetadata
    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(getId(), Modifier.PUBLIC + Modifier.STATIC, LIST_URI_METHOD_NAME,
            SpringJavaType.URI_COMPONENTS, parameterTypes, parameterNames, body);

    return methodBuilder.build();
  }

  /**
   * Creates getInitBinderMethod method
   *
   * @return
   */
  private MethodMetadata getInitBinderMethod() {
    // Define methodName
    final JavaSymbolName methodName =
        new JavaSymbolName("init" + entity.getSimpleTypeName() + "Binder");

    // Define method parameter types and parameter names
    List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
    parameterTypes.add(AnnotatedJavaType.convertFromJavaType(SpringJavaType.WEB_DATA_BINDER));
    List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
    parameterNames.add(new JavaSymbolName("dataBinder"));

    MethodMetadata existingMethod =
        getGovernorMethod(methodName,
            AnnotatedJavaType.convertFromAnnotatedJavaTypes(parameterTypes));
    if (existingMethod != null) {
      return existingMethod;
    }

    InvocableMemberBodyBuilder body = new InvocableMemberBodyBuilder();

    // dataBinder.setDisallowedFields("id");
    body.appendFormalLine("dataBinder.setDisallowedFields(\"%s\");", entityIdentifier);

    for (Pair<RelationInfo, JpaEntityMetadata> item : compositionRelationOneToOne) {
      body.appendFormalLine("dataBinder.setDisallowedFields(\"%s.%s\");", item.getKey().fieldName,
          item.getValue().getCurrentIndentifierField().getFieldName());
    }

    // Use the MethodMetadataBuilder for easy creation of MethodMetadata
    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, JavaType.VOID_PRIMITIVE,
            parameterTypes, parameterNames, body);

    return methodBuilder.build();
  }

  /**
   * This method provides the getModelAttributeMethod() method
   *
   * @return MethodMetadata
   */
  private MethodMetadata getModelAttributeMethod() {

    // Define methodName
    final JavaSymbolName methodName = new JavaSymbolName("get" + entity.getSimpleTypeName());

    final List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
    AnnotationMetadataBuilder pathVariable =
        new AnnotationMetadataBuilder(SpringJavaType.PATH_VARIABLE);
    pathVariable.addStringAttribute("value", entityItemName);
    parameterTypes.add(new AnnotatedJavaType(entityMetadata.getCurrentIndentifierField()
        .getFieldType(), pathVariable.build()));
    parameterTypes.add(AnnotatedJavaType.convertFromJavaType(JdkJavaType.LOCALE));

    MethodMetadata existingMethod =
        getGovernorMethod(methodName,
            AnnotatedJavaType.convertFromAnnotatedJavaTypes(parameterTypes));
    if (existingMethod != null) {
      return existingMethod;
    }


    final List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
    parameterNames.add(new JavaSymbolName(entityIdentifier));
    parameterNames.add(new JavaSymbolName("locale"));

    // Generate body
    InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();

    // Customer customer = customerService.findOne(id);
    bodyBuilder.appendFormalLine("%s %s = %s.%s(%s);", getNameOfJavaType(entity), entityItemName,
        controllerMetadata.getServiceField().getFieldName(), serviceMetadata
            .getCurrentFindOneMethod().getMethodName(), entityIdentifier);

    // if (customer == null) {
    //   String message = messageSource.getMessage("error_customerNotFound", null, locale);
    //   throw new NotFoundException(message);
    // }
    bodyBuilder.appendFormalLine("if (%s == null) {", entityItemName);
    bodyBuilder.indent();
    bodyBuilder.appendFormalLine(
        "String message = %s.getMessage(\"error_customerNotFound\", null, locale);",
        messageSourceField.getFieldName());
    bodyBuilder.appendFormalLine("throw new %s(message);",
        getNameOfJavaType(SpringletsJavaType.SPRINGLETS_NOT_FOUND_EXCEPTION),
        entity.getSimpleTypeName(), entityIdentifier);
    bodyBuilder.indentRemove();
    bodyBuilder.appendFormalLine("}");

    // return customer;
    bodyBuilder.appendFormalLine("return %s;", entityItemName);

    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, entity, parameterTypes,
            parameterNames, bodyBuilder);

    methodBuilder.addAnnotation(new AnnotationMetadataBuilder(SpringJavaType.MODEL_ATTRIBUTE)
        .build());

    return methodBuilder.build();
  }

  /**
   * Creates showURI method
   *
   * @return
   */
  private MethodMetadata getShowURIMethod() {
    // Define method parameter types and parameter names
    List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
    parameterTypes.add(AnnotatedJavaType.convertFromJavaType(entity));
    List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
    parameterNames.add(new JavaSymbolName(entityItemName));

    MethodMetadata existingMethod =
        getGovernorMethod(SHOW_URI_METHOD_NAME,
            AnnotatedJavaType.convertFromAnnotatedJavaTypes(parameterTypes));
    if (existingMethod != null) {
      return existingMethod;
    }

    // return MvcUriComponentsBuilder
    //    .fromMethodCall(
    //     MvcUriComponentsBuilder.on(CustomersItemJsonController.class).show(customer, null))
    //     .buildAndExpand(customer.getId()).encode();
    InvocableMemberBodyBuilder body = new InvocableMemberBodyBuilder();

    body.appendFormalLine("return %s", getNameOfJavaType(SpringJavaType.MVC_URI_COMPONENTS_BUILDER));
    body.indent();
    body.appendFormalLine(".fromMethodCall(");
    body.indent();
    body.appendFormalLine("%s.on(%s.class).%s(%s, null))",
        getNameOfJavaType(SpringJavaType.MVC_URI_COMPONENTS_BUILDER),
        getNameOfJavaType(getDestination()), this.showMethod.getMethodName(), entityItemName);
    body.indentRemove();
    body.appendFormalLine(".buildAndExpand(%s.get%s()).encode();", entityItemName,
        StringUtils.capitalize(entityIdentifier));
    body.indentRemove();

    // Use the MethodMetadataBuilder for easy creation of MethodMetadata
    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(getId(), Modifier.PUBLIC + Modifier.STATIC, SHOW_URI_METHOD_NAME,
            SpringJavaType.URI_COMPONENTS, parameterTypes, parameterNames, body);

    return methodBuilder.build();
  }

  /*
   * ===================================================================================
   */

  /**
   * This method provides the "list" form method using Thymeleaf view response
   * type
   *
   * @return MethodMetadata
   */
  private MethodMetadata getListMethod() {
    // Define methodName
    final JavaSymbolName methodName = new JavaSymbolName("list");

    List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
    parameterTypes.add(new AnnotatedJavaType(SpringJavaType.MODEL));

    MethodMetadata existingMethod =
        getGovernorMethod(methodName,
            AnnotatedJavaType.convertFromAnnotatedJavaTypes(parameterTypes));
    if (existingMethod != null) {
      return existingMethod;
    }

    final List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
    parameterNames.add(new JavaSymbolName("model"));

    // Adding annotations
    final List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Adding @GetMapping annotation
    annotations.add(new AnnotationMetadataBuilder(SpringJavaType.GET_MAPPING));

    // Generate body
    InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();

    // return new ModelAndView("customers/list");
    bodyBuilder.appendFormalLine("return new %s(\"%s/list\");",
        getNameOfJavaType(SpringJavaType.MODEL_AND_VIEW), controllerMetadata.getPath());

    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName,
            SpringJavaType.MODEL_AND_VIEW, parameterTypes, parameterNames, bodyBuilder);
    methodBuilder.setAnnotations(annotations);

    return methodBuilder.build();
  }

  /**
   * This method provides the "create" form method using Thymeleaf view
   * response type
   *
   * @return MethodMetadata
   */
  private MethodMetadata getCreateFormMethod() {
    // Define methodName
    final JavaSymbolName methodName = new JavaSymbolName("createForm");

    List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
    parameterTypes.add(new AnnotatedJavaType(SpringJavaType.MODEL));

    MethodMetadata existingMethod =
        getGovernorMethod(methodName,
            AnnotatedJavaType.convertFromAnnotatedJavaTypes(parameterTypes));
    if (existingMethod != null) {
      return existingMethod;
    }

    final List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
    parameterNames.add(new JavaSymbolName("model"));

    // Adding annotations
    final List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Adding @GetMapping annotation
    AnnotationMetadataBuilder getMapping =
        new AnnotationMetadataBuilder(SpringJavaType.GET_MAPPING);
    getMapping.addStringAttribute("value", "/create-form");
    annotations.add(getMapping);

    // Generate body
    InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();

    // populateForm(model);
    bodyBuilder.appendFormalLine("%s(model);", populateFormMethod.getMethodName());
    bodyBuilder.newLine();

    // model.addAttribute(new Entity());
    bodyBuilder.appendFormalLine(String.format("model.addAttribute(new %s());",
        this.entity.getSimpleTypeName()));

    // return "path/create";
    bodyBuilder.appendFormalLine("return new %s(\"%s/create\");",
        getNameOfJavaType(SpringJavaType.MODEL_AND_VIEW), viewsPath);

    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName,
            SpringJavaType.MODEL_AND_VIEW, parameterTypes, parameterNames, bodyBuilder);
    methodBuilder.setAnnotations(annotations);

    return methodBuilder.build();
  }

  /**
   * This method provides the "create" method using Thymeleaf view response
   * type
   *
   * @return MethodMetadata
   */
  private MethodMetadata getCreateMethod() {
    // Define methodName
    final JavaSymbolName methodName = new JavaSymbolName("create");

    List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
    parameterTypes.add(new AnnotatedJavaType(this.entity, ANN_METADATA_VALID,
        ANN_METADATA_MODEL_ATTRIBUTE));
    parameterTypes.add(AnnotatedJavaType.convertFromJavaType(SpringJavaType.BINDING_RESULT));
    parameterTypes.add(AnnotatedJavaType.convertFromJavaType(SpringJavaType.MODEL));

    MethodMetadata existingMethod =
        getGovernorMethod(methodName,
            AnnotatedJavaType.convertFromAnnotatedJavaTypes(parameterTypes));
    if (existingMethod != null) {
      return existingMethod;
    }

    final List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
    parameterNames.add(new JavaSymbolName(entityItemName));
    parameterNames.add(new JavaSymbolName("result"));
    parameterNames.add(new JavaSymbolName("model"));

    // Adding annotations
    final List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Adding @RequestMapping annotation
    annotations.add(new AnnotationMetadataBuilder(SpringJavaType.POST_MAPPING));

    // Generate body
    InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();

    // if (result.hasErrors()) {
    bodyBuilder.appendFormalLine("if (result.hasErrors()) {");
    bodyBuilder.indent();

    // populateFormats(model);
    bodyBuilder.appendFormalLine("%s(model);", populateFormMethod.getMethodName());
    bodyBuilder.newLine();

    // return new ModelAndView("customers/create");
    bodyBuilder.appendFormalLine("return new %s(\"%s/create\");",
        getNameOfJavaType(SpringJavaType.MODEL_AND_VIEW), controllerMetadata.getPath());
    bodyBuilder.indentRemove();

    // }
    bodyBuilder.appendFormalLine("}");

    final String newValueVar = "new" + entity.getSimpleTypeName();
    // Customer newCustomer = customerService.save(customer);
    bodyBuilder.appendFormalLine("%s %s = %s.%s(%s);", getNameOfJavaType(entity), newValueVar,
        controllerMetadata.getServiceField().getFieldName(), serviceMetadata.getCurrentSaveMethod()
            .getMethodName(), entityItemName);

    // UriComponents showURI = CustomersItemThymeleafController.showURI(newCustomer);
    bodyBuilder.appendFormalLine("%s showURI = %s.%s(%s);",
        getNameOfJavaType(SpringJavaType.URI_COMPONENTS), getNameOfJavaType(itemController),
        SHOW_URI_METHOD_NAME, newValueVar);

    // return new ModelAndView("redirect:" + showURI.toUriString());
    bodyBuilder.appendFormalLine("return new %s(\"redirect:\" + showURI.toUriString());",
        getNameOfJavaType(SpringJavaType.MODEL_AND_VIEW));

    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName,
            SpringJavaType.MODEL_AND_VIEW, parameterTypes, parameterNames, bodyBuilder);
    methodBuilder.setAnnotations(annotations);

    return methodBuilder.build();
  }

  /**
   * This method provides the "update" method using Thymeleaf view response
   * type
   *
   * @return MethodMetadata
   */
  private MethodMetadata getUpdateMethod() {
    // Define methodName
    final JavaSymbolName methodName = new JavaSymbolName("update");

    List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
    parameterTypes.add(new AnnotatedJavaType(this.entity, new AnnotationMetadataBuilder(
        new JavaType("javax.validation.Valid")).build(), ANN_METADATA_MODEL_ATTRIBUTE));
    parameterTypes.add(new AnnotatedJavaType(SpringJavaType.BINDING_RESULT));
    parameterTypes.add(new AnnotatedJavaType(SpringJavaType.MODEL));

    MethodMetadata existingMethod =
        getGovernorMethod(methodName,
            AnnotatedJavaType.convertFromAnnotatedJavaTypes(parameterTypes));
    if (existingMethod != null) {
      return existingMethod;
    }

    final List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
    parameterNames.add(new JavaSymbolName(entityItemName));
    parameterNames.add(new JavaSymbolName("result"));
    parameterNames.add(new JavaSymbolName("model"));

    // Adding annotations
    final List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Adding @PutMapping annotation
    annotations.add(new AnnotationMetadataBuilder(SpringJavaType.PUT_MAPPING));

    // Generate body
    final InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();

    // if (result.hasErrors()) {
    bodyBuilder.appendFormalLine("if (result.hasErrors()) {");
    bodyBuilder.indent();

    // populateFormats(model);
    bodyBuilder.appendFormalLine("%s(model);", populateFormMethod.getMethodName());
    bodyBuilder.newLine();

    // return "path/create";
    bodyBuilder.appendFormalLine("return new %s(\"%s/edit\");",
        getNameOfJavaType(SpringJavaType.MODEL_AND_VIEW), viewsPath);
    bodyBuilder.indentRemove();

    // }
    bodyBuilder.appendFormalLine("}");

    String savedVarName = "saved" + entity.getSimpleTypeName();
    // Customer savedCustomer = customerService.save(customer);;
    bodyBuilder.appendFormalLine("%s %s = %s.%s(%s);", getNameOfJavaType(entity), savedVarName,
        controllerMetadata.getServiceField().getFieldName(), serviceMetadata.getCurrentSaveMethod()
            .getMethodName(), entityItemName);

    // UriComponents showURI = CustomersItemThymeleafController.showURI(savedCustomer);
    bodyBuilder.appendFormalLine("%s showURI = %s.%s(%s);",
        getNameOfJavaType(SpringJavaType.URI_COMPONENTS), getDestination().getSimpleTypeName(),
        showURIMethod.getMethodName(), savedVarName);

    // return new ModelAndView("redirect:" + showURI.toUriString());
    bodyBuilder.appendFormalLine("return new %s(\"redirect:\" + showURI.toUriString());",
        getNameOfJavaType(SpringJavaType.MODEL_AND_VIEW));

    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName,
            SpringJavaType.MODEL_AND_VIEW, parameterTypes, parameterNames, bodyBuilder);
    methodBuilder.setAnnotations(annotations);

    return methodBuilder.build();
  }

  /**
   * This method provides the "delete" method using Thymeleaf view response
   * type
   *
   * @return MethodMetadata
   */
  private MethodMetadata getDeleteMethod() {
    // Define methodName
    final JavaSymbolName methodName = new JavaSymbolName("delete");

    List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();

    AnnotationMetadataBuilder modelAttributeAnnotation =
        new AnnotationMetadataBuilder(SpringJavaType.MODEL_ATTRIBUTE);
    parameterTypes.add(new AnnotatedJavaType(this.entity, modelAttributeAnnotation.build()));
    parameterTypes.add(new AnnotatedJavaType(SpringJavaType.MODEL));

    MethodMetadata existingMethod =
        getGovernorMethod(methodName,
            AnnotatedJavaType.convertFromAnnotatedJavaTypes(parameterTypes));
    if (existingMethod != null) {
      return existingMethod;
    }

    final List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
    parameterNames.add(new JavaSymbolName(entityItemName));
    parameterNames.add(new JavaSymbolName("model"));

    // Adding annotations
    final List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Adding @RequestMapping annotation
    annotations.add(new AnnotationMetadataBuilder(SpringJavaType.DELETE_MAPPING));

    // Generate body
    InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();

    // customerService.delete(customer);
    bodyBuilder.appendFormalLine("%s.%s(%s);", controllerMetadata.getServiceField().getFieldName(),
        serviceMetadata.getCurrentDeleteMethod().getMethodName(), entityItemName);

    // UriComponents listURI = CustomersCollectionThymeleafController.listURI();
    bodyBuilder.appendFormalLine("%s listURI = %s.%s();",
        getNameOfJavaType(SpringJavaType.URI_COMPONENTS), getNameOfJavaType(collectionController),
        LIST_URI_METHOD_NAME);

    // return new ModelAndView("redirect:" + listURI.toUriString());
    bodyBuilder.appendFormalLine("return new %s(\"redirect:\" + listURI.toUriString());",
        getNameOfJavaType(SpringJavaType.MODEL_AND_VIEW));

    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName,
            SpringJavaType.MODEL_AND_VIEW, parameterTypes, parameterNames, bodyBuilder);
    methodBuilder.setAnnotations(annotations);

    return methodBuilder.build();
  }

  /**
   * This method provides a finder method using THYMELEAF response type
   *
   * @param finderMethod
   *
   * @return MethodMetadata
   */
  private MethodMetadata getFinderMethod(MethodMetadata finderMethod) {
    // TODO
    return null;
    /*
    final List<AnnotatedJavaType> originalParameterTypes = finderMethod.getParameterTypes();

    // Get finder parameter names
    final List<JavaSymbolName> originalParameterNames = finderMethod.getParameterNames();
    List<String> stringParameterNames = new ArrayList<String>();
    for (JavaSymbolName parameterName : originalParameterNames) {
      stringParameterNames.add(parameterName.getSymbolName());
    }

    // Define methodName
    final JavaSymbolName methodName = finderMethod.getMethodName();

    // Define path
    String path = "";
    if (StringUtils.startsWith(methodName.getSymbolName(), "count")) {
      path = StringUtils.removeStart(methodName.getSymbolName(), "count");
    } else if (StringUtils.startsWith(methodName.getSymbolName(), "find")) {
      path = StringUtils.removeStart(methodName.getSymbolName(), "find");
    } else if (StringUtils.startsWith(methodName.getSymbolName(), "query")) {
      path = StringUtils.removeStart(methodName.getSymbolName(), "query");
    } else if (StringUtils.startsWith(methodName.getSymbolName(), "read")) {
      path = StringUtils.removeStart(methodName.getSymbolName(), "read");
    } else {
      path = methodName.getSymbolName();
    }
    path = StringUtils.uncapitalize(path);

    // Check if exists other method with the same @RequesMapping to generate
    MethodMetadata existingMVCMethod = getControllerMVCService().getMVCMethodByRequestMapping(
        controller.getType(), SpringEnumDetails.REQUEST_METHOD_GET, "/" + path,
        stringParameterNames, null, "application/vnd.datatables+json", "");
    if (existingMVCMethod != null
        && !existingMVCMethod.getDeclaredByMetadataId().equals(this.metadataIdentificationString)) {
      return existingMVCMethod;
    }

    // Get parameters
    List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
    List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
    StringBuffer finderParamsString = new StringBuffer();
    for (int i = 0; i < originalParameterTypes.size(); i++) {

      // Add @ModelAttribute for search param
      if (i == 0) {
        AnnotationMetadataBuilder requestParamAnnotation =
            new AnnotationMetadataBuilder(SpringJavaType.MODEL_ATTRIBUTE);
        requestParamAnnotation.addStringAttribute("value",
            originalParameterNames.get(i).getSymbolName());
        parameterTypes.add(new AnnotatedJavaType(originalParameterTypes.get(i).getJavaType(),
            requestParamAnnotation.build()));
        parameterNames.add(originalParameterNames.get(i));
        finderParamsString.append(originalParameterNames.get(i).getSymbolName());
      } else if (originalParameterTypes.get(i).getJavaType()
          .equals(SpringletsJavaType.SPRINGLETS_GLOBAL_SEARCH)) {
        parameterTypes.add(originalParameterTypes.get(i));
        addTypeToImport(originalParameterTypes.get(i).getJavaType());
        parameterNames.add(originalParameterNames.get(i));

        // Build finder parameters String
        finderParamsString.append(", ".concat(originalParameterNames.get(i).getSymbolName()));
      }
    }

    // Add DatatablesPageable param
    parameterTypes
        .add(AnnotatedJavaType.convertFromJavaType(addTypeToImport(this.datatablesPageable)));
    parameterNames.add(new JavaSymbolName("pageable"));
    finderParamsString.append(", pageable");

    // Add additional 'draw' param
    AnnotationMetadataBuilder requestParamAnnotation =
        new AnnotationMetadataBuilder(addTypeToImport(SpringJavaType.REQUEST_PARAM));
    requestParamAnnotation.addStringAttribute("value", "draw");
    parameterTypes.add(new AnnotatedJavaType(JavaType.INT_OBJECT, requestParamAnnotation.build()));
    parameterNames.add(new JavaSymbolName("draw"));

    // Adding annotations
    final List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Adding @RequestMapping annotation
    annotations.add(
        getControllerMVCService().getRequestMappingAnnotation(SpringEnumDetails.REQUEST_METHOD_GET,
            "/" + path, stringParameterNames, null, "application/vnd.datatables+json", ""));

    // Adding @ResponseBody annotation
    AnnotationMetadataBuilder responseBodyAnnotation =
        new AnnotationMetadataBuilder(SpringJavaType.RESPONSE_BODY);
    annotations.add(responseBodyAnnotation);

    // Generate body
    InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();

    // Generating returnType
    JavaType returnType = finderMethod.getReturnType();
    List<JavaType> returnParameterTypes = returnType.getParameters();
    StringBuffer returnTypeParamsString = new StringBuffer();
    for (int i = 0; i < returnParameterTypes.size(); i++) {
      addTypeToImport(returnParameterTypes.get(i));
      if (i > 0) {
        returnTypeParamsString.append(",");
      }
      returnTypeParamsString.append(returnParameterTypes.get(i).getSimpleTypeName());

      // Add module dependency
      getTypeLocationService().addModuleDependency(this.controller.getType().getModule(),
          returnParameterTypes.get(i));
    }

    // ReturnType<ReturnTypeParams> entity =
    // ENTITY_SERVICE_FIELD.FINDER_NAME(SEARCH_PARAMS);
    String returnParameterName =
        StringUtils.uncapitalize(returnParameterTypes.get(0).getSimpleTypeName());
    bodyBuilder.newLine();
    if (StringUtils.isEmpty(returnTypeParamsString)) {
      bodyBuilder.appendFormalLine(String.format("%s %s = %s.%s(%s);",
          addTypeToImport(returnType).getSimpleTypeName(), returnParameterName,
          getServiceField().getFieldName(), methodName, finderParamsString));
    } else {
      bodyBuilder.appendFormalLine(String.format("%s<%s> %s = %s.%s(%s);",
          addTypeToImport(returnType).getSimpleTypeName(), returnTypeParamsString,
          returnParameterName, getServiceField().getFieldName(), methodName, finderParamsString));
    }

    // long allAvailableEntity/Projection =
    // ENTITY_SERVICE_FIELD.COUNT_METHOD(formBean);
    bodyBuilder.newLine();
    bodyBuilder.appendFormalLine(String.format("long allAvailable%s = %s.%s(%s);",
        StringUtils.capitalize(Noun.pluralOf(returnParameterName, Locale.ENGLISH)),
        getServiceField().getFieldName(), "count".concat(StringUtils.capitalize(path)),
        parameterNames.get(0)));
    bodyBuilder.newLine();

    // return new DatatablesData<Entity/Projection>(entity/projection,
    // allAvailableEntity/Projection, draw);
    bodyBuilder.appendFormalLine(String.format("return new %s<%s>(%s, allAvailable%s, draw);",
        addTypeToImport(this.datatablesDataType).getSimpleTypeName(), returnTypeParamsString,
        returnParameterName,
        StringUtils.capitalize(Noun.pluralOf(returnParameterName, Locale.ENGLISH))));

    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(this.metadataIdentificationString, Modifier.PUBLIC, methodName,
            addTypeToImport(new JavaType(this.datatablesDataType.getFullyQualifiedTypeName(), 0,
                DataType.TYPE, null, returnParameterTypes)),
            parameterTypes, parameterNames, bodyBuilder);
    methodBuilder.setAnnotations(annotations);

    return methodBuilder.build();
    */
  }

  /**
   * This method provides a finder redirect method using THYMELEAF response
   * type
   *
   * @param finderMethod
   * @return
   */
  private MethodMetadata getFinderRedirectMethod(MethodMetadata finderMethod) {
    // TODO
    return null;
    /*

    // Get finder parameter names
    final List<JavaSymbolName> originalParameterNames = finderMethod.getParameterNames();
    List<String> stringParameterNames = new ArrayList<String>();
    for (JavaSymbolName parameterName : originalParameterNames) {
      stringParameterNames.add(parameterName.getSymbolName());
    }

    // Define methodName
    final JavaSymbolName methodName =
        new JavaSymbolName(finderMethod.getMethodName().getSymbolName().concat("Redirect"));

    // Define path
    String path = "";
    if (StringUtils.startsWith(methodName.getSymbolName(), "count")) {
      path = StringUtils.removeStart(methodName.getSymbolName(), "count");
    } else if (StringUtils.startsWith(methodName.getSymbolName(), "find")) {
      path = StringUtils.removeStart(methodName.getSymbolName(), "find");
    } else if (StringUtils.startsWith(methodName.getSymbolName(), "query")) {
      path = StringUtils.removeStart(methodName.getSymbolName(), "query");
    } else if (StringUtils.startsWith(methodName.getSymbolName(), "read")) {
      path = StringUtils.removeStart(methodName.getSymbolName(), "read");
    } else {
      path = methodName.getSymbolName();
    }
    path = StringUtils.uncapitalize(StringUtils.removeEnd(path, "Redirect"));

    // Check if exists other method with the same @RequesMapping to generate
    MethodMetadata existingMVCMethod = getControllerMVCService().getMVCMethodByRequestMapping(
        controller.getType(), SpringEnumDetails.REQUEST_METHOD_POST, "/" + path,
        stringParameterNames, null, SpringEnumDetails.MEDIA_TYPE_TEXT_HTML_VALUE.toString(), "");
    if (existingMVCMethod != null
        && !existingMVCMethod.getDeclaredByMetadataId().equals(this.metadataIdentificationString)) {
      return existingMVCMethod;
    }

    // Get parameters
    List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
    List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();

    // Check if finder parameter is a DTO
    JavaType formBean = finderMethod.getParameterTypes().get(0).getJavaType();
    if (getTypeLocationService().getTypeDetails(formBean) != null && getTypeLocationService()
        .getTypeDetails(formBean).getAnnotation(RooJavaType.ROO_DTO) == null) {

      // Finder parameter are entity fields
      formBean = this.entity;
    }

    // Add form bean parameter
    AnnotationMetadataBuilder modelAttributeAnnotation =
        new AnnotationMetadataBuilder(SpringJavaType.MODEL_ATTRIBUTE);
    modelAttributeAnnotation.addStringAttribute("value", "formBean");
    parameterTypes.add(new AnnotatedJavaType(formBean, modelAttributeAnnotation.build()));
    parameterNames.add(new JavaSymbolName("formBean"));

    // Add redirect parameter
    parameterTypes.add(
        AnnotatedJavaType.convertFromJavaType(addTypeToImport(SpringJavaType.REDIRECT_ATTRIBUTES)));
    parameterNames.add(new JavaSymbolName("redirect"));

    // Adding annotations
    final List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Adding @RequestMapping annotation
    annotations.add(getControllerMVCService().getRequestMappingAnnotation(
        SpringEnumDetails.REQUEST_METHOD_POST, "/" + path, stringParameterNames, null,
        SpringEnumDetails.MEDIA_TYPE_TEXT_HTML_VALUE, ""));

    // Generate body
    InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
    bodyBuilder.newLine();

    // redirect.addFlashAttribute(entity/dtoSearch);
    bodyBuilder.appendFormalLine(
        String.format("redirect.addFlashAttribute(\"formBean\", %s);", parameterNames.get(0)));
    bodyBuilder.newLine();

    // return "redirect:PATH_PREFIX/ENTITY_PLURAL/FINDER_NAME";
    bodyBuilder.appendFormalLine(String.format("return \"redirect:%s/%s/search/%s\";",
        this.pathPrefix, this.entityPlural, path));

    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(this.metadataIdentificationString, Modifier.PUBLIC, methodName,
            JavaType.STRING, parameterTypes, parameterNames, bodyBuilder);
    methodBuilder.setAnnotations(annotations);

    return methodBuilder.build();
    */
  }

  /**
   * This method provides a finder form method using THYMELEAF response type
   *
   * @param finderMethod
   * @return
   */
  private MethodMetadata getFinderFormMethod(MethodMetadata finderMethod) {
    // TODO
    return null;
    /*

    // Get finder parameter names
    List<String> stringParameterNames = new ArrayList<String>();
    stringParameterNames.add("model");

    // Define methodName
    final JavaSymbolName methodName =
        new JavaSymbolName(finderMethod.getMethodName().getSymbolName().concat("Form"));

    // Define path
    String path = "";
    if (StringUtils.startsWith(finderMethod.getMethodName().getSymbolName(), "count")) {
      path = StringUtils.removeStart(finderMethod.getMethodName().getSymbolName(), "count");
    } else if (StringUtils.startsWith(finderMethod.getMethodName().getSymbolName(), "find")) {
      path = StringUtils.removeStart(finderMethod.getMethodName().getSymbolName(), "find");
    } else if (StringUtils.startsWith(finderMethod.getMethodName().getSymbolName(), "query")) {
      path = StringUtils.removeStart(finderMethod.getMethodName().getSymbolName(), "query");
    } else if (StringUtils.startsWith(finderMethod.getMethodName().getSymbolName(), "read")) {
      path = StringUtils.removeStart(finderMethod.getMethodName().getSymbolName(), "read");
    } else {
      path = methodName.getSymbolName();
    }
    path = StringUtils.uncapitalize(path).concat("/search-form");

    // Check if exists other method with the same @RequesMapping to generate
    MethodMetadata existingMVCMethod = getControllerMVCService().getMVCMethodByRequestMapping(
        controller.getType(), SpringEnumDetails.REQUEST_METHOD_GET, "/" + path,
        stringParameterNames, null, SpringEnumDetails.MEDIA_TYPE_TEXT_HTML_VALUE.toString(), "");
    if (existingMVCMethod != null
        && !existingMVCMethod.getDeclaredByMetadataId().equals(this.metadataIdentificationString)) {
      return existingMVCMethod;
    }

    // Get parameters
    List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
    List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();

    // Add model parameter
    parameterTypes
        .add(AnnotatedJavaType.convertFromJavaType(addTypeToImport(SpringJavaType.MODEL)));
    parameterNames.add(new JavaSymbolName("model"));

    // Adding annotations
    final List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Adding @RequestMapping annotation
    annotations.add(getControllerMVCService().getRequestMappingAnnotation(
        SpringEnumDetails.REQUEST_METHOD_GET, "/" + path, stringParameterNames, null,
        SpringEnumDetails.MEDIA_TYPE_TEXT_HTML_VALUE, ""));

    // Check if finder parameter is a DTO
    JavaType formBean = finderMethod.getParameterTypes().get(0).getJavaType();
    if (getTypeLocationService().getTypeDetails(formBean) != null && getTypeLocationService()
        .getTypeDetails(formBean).getAnnotation(RooJavaType.ROO_DTO) == null) {

      // Finder parameter are entity fields
      formBean = this.entity;
    }

    // Generate body
    InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
    bodyBuilder.newLine();

    // Entity/DTO search = new Entity/DTO();
    bodyBuilder.appendFormalLine(String.format("%1$s %2$s = new %1$s();",
        addTypeToImport(formBean).getSimpleTypeName(), "formBean"));
    bodyBuilder.newLine();

    // model.addAttribute("search", search);
    bodyBuilder.appendFormalLine(String.format("model.addAttribute(\"%1$s\", %1$s);", "formBean"));
    bodyBuilder.newLine();

    // populateForm(model);
    bodyBuilder.appendFormalLine("populateForm(model);");
    bodyBuilder.newLine();

    // return "PATH_PREFIX/ENTITY_PLURAL/FINDER_NAMEForm";
    String pathPrefix = "";
    if (StringUtils.isBlank(this.pathPrefix)) {
      pathPrefix = this.pathPrefix;
    } else {
      pathPrefix = this.pathPrefix.concat("/");
    }
    bodyBuilder.appendFormalLine(String.format("return \"%s%s/%s\";",
        StringUtils.removeStart(pathPrefix, "/"), this.entityPlural, methodName.getSymbolName()));

    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(this.metadataIdentificationString, Modifier.PUBLIC, methodName,
            JavaType.STRING, parameterTypes, parameterNames, bodyBuilder);
    methodBuilder.setAnnotations(annotations);

    return methodBuilder.build();
    */
  }

  /**
   * This method provides populateFormats method that allows to configure date
   * time format for every entity
   *
   * @return
   */
  private MethodMetadata getPopulateFormatsMethod() {
    // Define methodName
    final JavaSymbolName methodName = new JavaSymbolName("populateFormats");

    List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
    parameterTypes.add(AnnotatedJavaType.convertFromJavaType(SpringJavaType.MODEL));

    MethodMetadata existingMethod =
        getGovernorMethod(methodName,
            AnnotatedJavaType.convertFromAnnotatedJavaTypes(parameterTypes));
    if (existingMethod != null) {
      return existingMethod;
    }

    final List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
    parameterNames.add(new JavaSymbolName("model"));

    // Adding annotations
    final List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Generate body
    InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();

    // Always save locale
    bodyBuilder.appendFormalLine(
        "model.addAttribute(\"application_locale\", %s.getLocale().getLanguage());",
        getNameOfJavaType(SpringJavaType.LOCALE_CONTEXT_HOLDER));

    // Getting all enum types from provided entity
    for (FieldMetadata field : dateTimeFields) {

      // Getting annotation format
      AnnotationMetadata dateTimeFormatAnnotation =
          field.getAnnotation(SpringJavaType.DATE_TIME_FORMAT);

      if (dateTimeFormatAnnotation != null
          && (dateTimeFormatAnnotation.getAttribute("style") != null || dateTimeFormatAnnotation
              .getAttribute("pattern") != null)) {

        AnnotationAttributeValue<String> formatAttr =
            dateTimeFormatAnnotation.getAttribute("style");
        if (formatAttr != null) {
          String format = formatAttr.getValue();
          // model.addAttribute("field_date_format",
          // DateTimeFormat.patternForStyle("M-",
          // LocaleContextHolder.getLocale()));
          bodyBuilder
              .appendFormalLine(
                  "model.addAttribute(\"%s_date_format\", %s.patternForStyle(\"%s\", %s.getLocale()));",
                  field.getFieldName(), getNameOfJavaType(JODA_DATETIME_FORMAT_JAVA_TYPE), format,
                  getNameOfJavaType(SpringJavaType.LOCALE_CONTEXT_HOLDER));
        } else {
          formatAttr = dateTimeFormatAnnotation.getAttribute("pattern");
          String format = formatAttr.getValue();
          // model.addAttribute("field_date_format", "pattern");
          bodyBuilder.appendFormalLine(String.format(
              "model.addAttribute(\"%s_date_format\", \"%s\");", field.getFieldName(), format));
        }
      }

    }

    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, JavaType.VOID_PRIMITIVE,
            parameterTypes, parameterNames, bodyBuilder);
    methodBuilder.setAnnotations(annotations);

    return methodBuilder.build();
  }


  /**
   * This method provides the "list" Datatables JSON method using JSON
   * response type and returns Datatables element
   *
   * @return MethodMetadata
   */
  private MethodMetadata getListDatatablesJSONMethod() {
    // Define methodName
    final JavaSymbolName methodName = new JavaSymbolName("list");

    List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
    parameterTypes.add(AnnotatedJavaType
        .convertFromJavaType(SpringletsJavaType.SPRINGLETS_GLOBAL_SEARCH));
    parameterTypes.add(AnnotatedJavaType
        .convertFromJavaType(SpringletsJavaType.SPRINGLETS_DATATABLES_PAGEABLE));
    AnnotationMetadataBuilder requestParamAnnotation =
        new AnnotationMetadataBuilder(SpringJavaType.REQUEST_PARAM);
    requestParamAnnotation.addStringAttribute("value", "draw");
    parameterTypes.add(new AnnotatedJavaType(JavaType.INT_OBJECT, requestParamAnnotation.build()));

    MethodMetadata existingMethod =
        getGovernorMethod(methodName,
            AnnotatedJavaType.convertFromAnnotatedJavaTypes(parameterTypes));
    if (existingMethod != null) {
      return existingMethod;
    }

    final List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
    parameterNames.add(new JavaSymbolName("search"));
    parameterNames.add(new JavaSymbolName("pageable"));
    parameterNames.add(new JavaSymbolName("draw"));

    // Adding annotations
    final List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Adding @GetMapping annotation
    AnnotationMetadataBuilder getMappingAnnotation =
        new AnnotationMetadataBuilder(SpringJavaType.GET_MAPPING);
    getMappingAnnotation.addEnumAttribute("produces", SpringletsJavaType.SPRINGLETS_DATATABLES,
        "MEDIA_TYPE");
    annotations.add(getMappingAnnotation);

    // Adding @ResponseBody annotation
    AnnotationMetadataBuilder responseBodyAnnotation =
        new AnnotationMetadataBuilder(SpringJavaType.RESPONSE_BODY);
    annotations.add(responseBodyAnnotation);

    // Generate body
    InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();

    // Page<Customer> customers = customerService.findAll(search, pageable);
    bodyBuilder.appendFormalLine("%s<%s> %s = %s.%s(search, pageable);",
        getNameOfJavaType(SpringJavaType.PAGE), getNameOfJavaType(entity), this.entityPlural,
        controllerMetadata.getServiceField().getFieldName(), serviceMetadata
            .getCurrentFindAllMethod().getMethodName());

    final String totalVarName = "total" + StringUtils.capitalize(this.entityPlural) + "Count";
    // long totalCustomersCount = customers.getTotalElements();
    bodyBuilder.appendFormalLine("long %s = %s.getTotalElements();", totalVarName, entityPlural);

    //  if (search != null && StringUtils.hasText(search.getText())) {
    //      totalCustomersCount = customerService.count();
    //  }
    bodyBuilder.appendFormalLine("if (search != null && %s.hasText(search.getText())) {",
        getNameOfJavaType(SpringJavaType.STRING_UTILS));
    bodyBuilder.indent();
    bodyBuilder.appendFormalLine("%s = %s.%s();", totalVarName, controllerMetadata
        .getServiceField().getFieldName(), serviceMetadata.getCurrentCountMethod().getMethodName());
    bodyBuilder.indentRemove();
    bodyBuilder.appendFormalLine("}");



    // return new DatatablesData<Entity>(entityNamePlural,
    // allAvailableentityNamePlural,
    // draw);
    bodyBuilder.appendFormalLine(String.format("return new %s<%s>(%s, %s, draw);",
        getNameOfJavaType(SpringletsJavaType.SPRINGLETS_DATATABLES_DATA),
        getNameOfJavaType(entity), this.entityPlural, totalVarName));

    // Generating returnType
    JavaType returnType = JavaType.wrapperOf(SpringletsJavaType.SPRINGLETS_DATATABLES_DATA, entity);

    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, returnType, parameterTypes,
            parameterNames, bodyBuilder);
    methodBuilder.setAnnotations(annotations);

    return methodBuilder.build();
  }



  /**
   * This method provides the "edit" form method using Thymeleaf view response
   * type
   *
   * @return MethodMetadata
   */
  private MethodMetadata getEditFormMethod() {
    // Define methodName
    final JavaSymbolName methodName = new JavaSymbolName("editForm");

    List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
    AnnotationMetadataBuilder modelAttributeAnnotation =
        new AnnotationMetadataBuilder(SpringJavaType.MODEL_ATTRIBUTE);
    parameterTypes.add(new AnnotatedJavaType(this.entity, modelAttributeAnnotation.build()));
    parameterTypes.add(new AnnotatedJavaType(SpringJavaType.MODEL));

    MethodMetadata existingMethod =
        getGovernorMethod(methodName,
            AnnotatedJavaType.convertFromAnnotatedJavaTypes(parameterTypes));
    if (existingMethod != null) {
      return existingMethod;
    }

    final List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
    parameterNames.add(new JavaSymbolName(entityItemName));
    parameterNames.add(new JavaSymbolName("model"));

    // Adding annotations
    final List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Adding @GetMapping annotation
    AnnotationMetadataBuilder getMapping =
        new AnnotationMetadataBuilder(SpringJavaType.GET_MAPPING);
    getMapping.addStringAttribute("value", "/edit-form");
    annotations.add(getMapping);

    // Generate body
    InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();

    // populateFormats(model);
    bodyBuilder.appendFormalLine("%s(model);", populateFormMethod.getMethodName());
    bodyBuilder.newLine();

    // return new ModelAndView("customers/edit");
    bodyBuilder.appendFormalLine("return new %s(\"%s/edit\");",
        getNameOfJavaType(SpringJavaType.MODEL_AND_VIEW), viewsPath);

    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName,
            SpringJavaType.MODEL_AND_VIEW, parameterTypes, parameterNames, bodyBuilder);
    methodBuilder.setAnnotations(annotations);

    return methodBuilder.build();
  }



  /**
   * This method provides a finder list method using THYMELEAF response type
   *
   * @param finderMethod
   * @return
   */
  private MethodMetadata getFinderListMethod(MethodMetadata finderMethod) {
    // TODO

    return null;
    /*
    // Get finder parameter names
    final List<JavaSymbolName> originalParameterNames = finderMethod.getParameterNames();
    List<String> stringParameterNames = new ArrayList<String>();
    for (JavaSymbolName parameterName : originalParameterNames) {
      stringParameterNames.add(parameterName.getSymbolName());
    }

    // Define methodName
    final JavaSymbolName methodName = finderMethod.getMethodName();

    // Define path
    String path = "";
    if (StringUtils.startsWith(finderMethod.getMethodName().getSymbolName(), "count")) {
      path = StringUtils.removeStart(finderMethod.getMethodName().getSymbolName(), "count");
    } else if (StringUtils.startsWith(finderMethod.getMethodName().getSymbolName(), "find")) {
      path = StringUtils.removeStart(finderMethod.getMethodName().getSymbolName(), "find");
    } else if (StringUtils.startsWith(finderMethod.getMethodName().getSymbolName(), "query")) {
      path = StringUtils.removeStart(finderMethod.getMethodName().getSymbolName(), "query");
    } else if (StringUtils.startsWith(finderMethod.getMethodName().getSymbolName(), "read")) {
      path = StringUtils.removeStart(finderMethod.getMethodName().getSymbolName(), "read");
    } else {
      path = methodName.getSymbolName();
    }
    path = StringUtils.uncapitalize(path);

    // Check if exists other method with the same @RequesMapping to generate
    MethodMetadata existingMVCMethod = getControllerMVCService().getMVCMethodByRequestMapping(
        controller.getType(), SpringEnumDetails.REQUEST_METHOD_GET, "/" + path,
        stringParameterNames, null, SpringEnumDetails.MEDIA_TYPE_TEXT_HTML_VALUE.toString(), "");
    if (existingMVCMethod != null
        && !existingMVCMethod.getDeclaredByMetadataId().equals(this.metadataIdentificationString)) {
      return existingMVCMethod;
    }

    // Get parameters
    List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
    List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();

    // Add model parameter
    parameterTypes
        .add(AnnotatedJavaType.convertFromJavaType(addTypeToImport(SpringJavaType.MODEL)));
    parameterNames.add(new JavaSymbolName("model"));

    // Adding annotations
    final List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Adding @RequestMapping annotation
    annotations.add(getControllerMVCService().getRequestMappingAnnotation(
        SpringEnumDetails.REQUEST_METHOD_GET, "/" + path, stringParameterNames, null,
        SpringEnumDetails.MEDIA_TYPE_TEXT_HTML_VALUE, ""));

    // Generate body
    InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();

    // return "PATH_PREFIX/ENTITY_PLURAL/FINDER_NAMEList";
    String pathPrefix = "";
    if (StringUtils.isBlank(this.pathPrefix)) {
      pathPrefix = this.pathPrefix;
    } else {
      pathPrefix = this.pathPrefix.concat("/");
    }
    bodyBuilder.appendFormalLine(String.format("return \"%s%s/%sList\";",
        StringUtils.removeStart(pathPrefix, "/"), this.entityPlural, methodName.getSymbolName()));

    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(this.metadataIdentificationString, Modifier.PUBLIC, methodName,
            JavaType.STRING, parameterTypes, parameterNames, bodyBuilder);
    methodBuilder.setAnnotations(annotations);

    return methodBuilder.build();
    */
  }


  /**
   * This method provides the "show" method using Thymeleaf view response type
   *
   * @return MethodMetadata
   */
  private MethodMetadata getShowMethod() {
    // Define methodName
    final JavaSymbolName methodName = new JavaSymbolName("show");

    List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
    AnnotationMetadataBuilder modelAttributeAnnotation =
        new AnnotationMetadataBuilder(SpringJavaType.MODEL_ATTRIBUTE);
    parameterTypes.add(new AnnotatedJavaType(this.entity, modelAttributeAnnotation.build()));
    parameterTypes.add(new AnnotatedJavaType(SpringJavaType.MODEL));

    MethodMetadata existingMethod =
        getGovernorMethod(methodName,
            AnnotatedJavaType.convertFromAnnotatedJavaTypes(parameterTypes));
    if (existingMethod != null) {
      return existingMethod;
    }

    final List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
    parameterNames.add(new JavaSymbolName(entityItemName));
    parameterNames.add(new JavaSymbolName("model"));

    // Adding annotations
    final List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Adding @RequestMapping annotation
    annotations.add(new AnnotationMetadataBuilder(SpringJavaType.GET_MAPPING));

    // Generate body
    InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();

    // return new ModelAndView("customers/show");
    bodyBuilder.appendFormalLine("return new %s(\"%s/show\");",
        getNameOfJavaType(SpringJavaType.MODEL_AND_VIEW), viewsPath);

    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName,
            SpringJavaType.MODEL_AND_VIEW, parameterTypes, parameterNames, bodyBuilder);
    methodBuilder.setAnnotations(annotations);

    return methodBuilder.build();
  }

  /**
   * This method provides the "populateForm" method
   *
   * @return MethodMetadata
   */
  private MethodMetadata getPopulateFormMethod() {

    // Define methodName
    final JavaSymbolName methodName = new JavaSymbolName("populateForm");

    List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
    parameterTypes.add(AnnotatedJavaType.convertFromJavaType(SpringJavaType.MODEL));

    MethodMetadata existingMethod =
        getGovernorMethod(methodName,
            AnnotatedJavaType.convertFromAnnotatedJavaTypes(parameterTypes));
    if (existingMethod != null) {
      return existingMethod;
    }

    final List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
    parameterNames.add(new JavaSymbolName("model"));

    // Adding annotations
    final List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Generate body
    InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();

    // populateFormats(model);
    bodyBuilder.appendFormalLine("%s(model);", populateFormatsMethod.getMethodName());

    // Getting all enum types from provided entity
    for (FieldMetadata field : enumFields) {
      // model.addAttribute("enumField",
      // Arrays.asList(Enum.values()));
      bodyBuilder.appendFormalLine("model.addAttribute(\"%s\", %s.asList(%s.values()));",
          field.getFieldName(), getNameOfJavaType(JavaType.ARRAYS),
          getNameOfJavaType(field.getFieldType()));
    }

    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, JavaType.VOID_PRIMITIVE,
            parameterTypes, parameterNames, bodyBuilder);
    methodBuilder.setAnnotations(annotations);

    return methodBuilder.build();
  }

  /**
   * This method provides detail list method using Thymeleaf response type
   *
   * @return MethodMetadata
   */
  private MethodMetadata getListDetailMethod() {
    // TODO
    return null;
    /*
    // First of all, check if exists other method with the same
    // @RequesMapping to generate
    MethodMetadata existingMVCMethod = getControllerMVCService().getMVCMethodByRequestMapping(
        controller.getType(), SpringEnumDetails.REQUEST_METHOD_GET, "", null, null,
        SpringEnumDetails.MEDIA_TYPE_APPLICATION_JSON_VALUE.toString(), "");
    if (existingMVCMethod != null
        && !existingMVCMethod.getDeclaredByMetadataId().equals(this.metadataIdentificationString)) {
      return existingMVCMethod;
    }

    // Define methodName
    final JavaSymbolName methodName = new JavaSymbolName(
        "list".concat(this.controllerDetailInfo.getEntity().getSimpleTypeName()));

    // Create PageableDefault annotation
    AnnotationMetadataBuilder pageableDefaultAnnotation =
        new AnnotationMetadataBuilder(SpringJavaType.PAGEABLE_DEFAULT);

    String sortFieldName = "";
    MemberDetails entityDetails = getMemberDetails(
        getTypeLocationService().getTypeDetails(this.controllerDetailInfo.getEntity()));
    List<FieldMetadata> fields = entityDetails.getFields();
    for (FieldMetadata field : fields) {
      if (field.getAnnotation(new JavaType("javax.persistence.Id")) != null) {
        sortFieldName = field.getFieldName().getSymbolName();
      }
    }
    if (!sortFieldName.isEmpty()) {
      pageableDefaultAnnotation.addStringAttribute("sort", sortFieldName);
    }

    List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
    AnnotationMetadataBuilder modelAttributeAnnotation =
        new AnnotationMetadataBuilder(SpringJavaType.MODEL_ATTRIBUTE);
    parameterTypes.add(new AnnotatedJavaType(this.controllerDetailInfo.getParentEntity(),
        modelAttributeAnnotation.build()));
    parameterTypes.add(new AnnotatedJavaType(SpringletsJavaType.SPRINGLETS_GLOBAL_SEARCH));
    parameterTypes
        .add(new AnnotatedJavaType(SpringJavaType.PAGEABLE, pageableDefaultAnnotation.build()));

    final List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
    parameterNames.add(new JavaSymbolName(
        StringUtils.uncapitalize(this.controllerDetailInfo.getParentEntity().getSimpleTypeName())));
    parameterNames.add(new JavaSymbolName("search"));
    parameterNames.add(new JavaSymbolName("pageable"));

    // Adding annotations
    final List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Adding @RequestMapping annotation
    annotations.add(
        getControllerMVCService().getRequestMappingAnnotation(SpringEnumDetails.REQUEST_METHOD_GET,
            "", null, null, SpringEnumDetails.MEDIA_TYPE_APPLICATION_JSON_VALUE, ""));

    // Adding @ResponseBody annotation
    AnnotationMetadataBuilder responseBodyAnnotation =
        new AnnotationMetadataBuilder(SpringJavaType.RESPONSE_BODY);
    annotations.add(responseBodyAnnotation);

    // Generate body
    InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();

    // Get finder method
    ClassOrInterfaceTypeDetails serviceDetails =
        getTypeLocationService().getTypeDetails(this.controllerDetailInfo.getService());

    final LogicalPath serviceLogicalPath =
        PhysicalTypeIdentifier.getPath(serviceDetails.getDeclaredByMetadataId());
    final String serviceMetadataKey =
        ServiceMetadata.createIdentifier(serviceDetails.getType(), serviceLogicalPath);
    final ServiceMetadata serviceMetadata =
        (ServiceMetadata) getMetadataService().get(serviceMetadataKey);

    // Get parent field
    FieldMetadata parentRelationField = null;
    MemberDetails memberDetails = getMemberDetails(this.controllerDetailInfo.getParentEntity());
    List<FieldMetadata> parentFields = memberDetails.getFields();
    for (FieldMetadata parentField : parentFields) {
      if (parentField.getFieldName().getSymbolName()
          .equals(this.controllerDetailInfo.getParentReferenceFieldName())) {
        AnnotationMetadata oneToManyAnnotation = parentField.getAnnotation(JpaJavaType.ONE_TO_MANY);
        if (oneToManyAnnotation != null && (parentField.getFieldType().getFullyQualifiedTypeName()
            .equals(JavaType.LIST.getFullyQualifiedTypeName())
            || parentField.getFieldType().getFullyQualifiedTypeName()
                .equals(JavaType.SET.getFullyQualifiedTypeName()))) {
          parentRelationField = parentField;
          break;
        }
      }
    }

    Validate.notNull(parentRelationField,
        String.format("ERROR: '%s' must have a field related to '%s'",
            this.controllerDetailInfo.getParentEntity().getSimpleTypeName(),
            this.controllerDetailInfo.getEntity().getSimpleTypeName()));

    // Generating returnType
    Map<FieldMetadata, MethodMetadata> referencedFieldsFindAllDefinedMethods =
        serviceMetadata.getReferencedFieldsFindAllDefinedMethods();
    AnnotationAttributeValue<Object> attributeMappedBy =
        parentRelationField.getAnnotation(JpaJavaType.ONE_TO_MANY).getAttribute("mappedBy");

    Validate.notNull(attributeMappedBy,
        String.format("ERROR: The field '%s' of '%s' must have 'mappedBy' value",
            parentRelationField.getFieldName(),
            this.controllerDetailInfo.getParentEntity().getSimpleTypeName()));

    String mappedBy = (String) attributeMappedBy.getValue();
    MethodMetadata findByMethod = null;
    Iterator<Entry<FieldMetadata, MethodMetadata>> it =
        referencedFieldsFindAllDefinedMethods.entrySet().iterator();
    while (it.hasNext()) {
      Entry<FieldMetadata, MethodMetadata> finder = it.next();
      if (finder.getKey().getFieldName().getSymbolName().equals(mappedBy)) {
        findByMethod = finder.getValue();
        break;
      }
    }

    JavaType returnType = findByMethod.getReturnType();
    List<JavaType> returnParameterTypes = returnType.getParameters();
    StringBuffer returnTypeParamsString = new StringBuffer();
    for (int i = 0; i < returnParameterTypes.size(); i++) {
      addTypeToImport(returnParameterTypes.get(i));
      if (i > 0) {
        returnTypeParamsString.append(",");
      }
      returnTypeParamsString.append(returnParameterTypes.get(i).getSimpleTypeName());

      // Add module dependency
      getTypeLocationService().addModuleDependency(this.controller.getType().getModule(),
          returnParameterTypes.get(i));
    }

    // Page<ENTITYREL> entityrelplural =
    // entityRelNameService.findAllByENTITYNAME(ENTITYNAME, search,
    // pageable);
    bodyBuilder.newLine();
    bodyBuilder.appendFormalLine(String.format("%s<%s> %s = %s.%s(%s, search, pageable);",
        addTypeToImport(returnType).getSimpleTypeName(), returnTypeParamsString,
        StringUtils.uncapitalize(StringUtils.lowerCase(Noun
            .pluralOf(this.controllerDetailInfo.getEntity().getSimpleTypeName(), Locale.ENGLISH))),
        getServiceDetailField(this.controllerDetailInfo.getService()).getFieldName()
            .getSymbolNameUnCapitalisedFirstLetter(),
        findByMethod.getMethodName(),
        StringUtils.uncapitalize(this.controllerDetailInfo.getParentEntity().getSimpleTypeName())));

    // return entityrelplural;
    bodyBuilder.appendFormalLine(
        String.format("return %s;", StringUtils.uncapitalize(StringUtils.lowerCase(Noun.pluralOf(
            this.controllerDetailInfo.getEntity().getSimpleTypeName(), Locale.ENGLISH)))));

    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(this.metadataIdentificationString, Modifier.PUBLIC, methodName,
            returnType, parameterTypes, parameterNames, bodyBuilder);
    methodBuilder.setAnnotations(annotations);
    return methodBuilder.build();
    */
  }

  /**
   * This method provides detail datatables list method using Thymeleaf
   * response type
   *
   * @return MethodMetadata
   */
  private MethodMetadata getListDatatablesDetailMethod() {
    // TODO
    return null;
    /*

    // First of all, check if exists other method with the same
    // @RequesMapping to generate
    MethodMetadata existingMVCMethod = getControllerMVCService().getMVCMethodByRequestMapping(
        controller.getType(), SpringEnumDetails.REQUEST_METHOD_GET, "", null, "",
        "application/vnd.datatables+json", "");
    if (existingMVCMethod != null
        && !existingMVCMethod.getDeclaredByMetadataId().equals(this.metadataIdentificationString)) {
      return existingMVCMethod;
    }

    // Define methodName
    final JavaSymbolName methodName = new JavaSymbolName(
        "list".concat(this.controllerDetailInfo.getEntity().getSimpleTypeName()));

    List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
    AnnotationMetadataBuilder modelAttributeAnnotation =
        new AnnotationMetadataBuilder(SpringJavaType.MODEL_ATTRIBUTE);
    parameterTypes.add(new AnnotatedJavaType(this.controllerDetailInfo.getParentEntity(),
        modelAttributeAnnotation.build()));
    parameterTypes
        .add(AnnotatedJavaType.convertFromJavaType(SpringletsJavaType.SPRINGLETS_GLOBAL_SEARCH));
    parameterTypes.add(AnnotatedJavaType.convertFromJavaType(this.datatablesPageable));
    AnnotationMetadataBuilder requestParamAnnotation =
        new AnnotationMetadataBuilder(SpringJavaType.REQUEST_PARAM);
    requestParamAnnotation.addStringAttribute("value", "draw");
    parameterTypes.add(new AnnotatedJavaType(JavaType.INT_OBJECT, requestParamAnnotation.build()));

    final List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
    parameterNames.add(new JavaSymbolName(
        StringUtils.uncapitalize(this.controllerDetailInfo.getParentEntity().getSimpleTypeName())));
    parameterNames.add(new JavaSymbolName("search"));
    parameterNames.add(new JavaSymbolName("pageable"));
    parameterNames.add(new JavaSymbolName("draw"));

    // Adding annotations
    final List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();

    // Adding @RequestMapping annotation
    annotations.add(getControllerMVCService().getRequestMappingAnnotation(
        SpringEnumDetails.REQUEST_METHOD_GET, "", null, "", "application/vnd.datatables+json", ""));

    // Adding @ResponseBody annotation
    AnnotationMetadataBuilder responseBodyAnnotation =
        new AnnotationMetadataBuilder(SpringJavaType.RESPONSE_BODY);
    annotations.add(responseBodyAnnotation);

    // Generate body
    InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();

    // Page<ENTITYRELNAME> entityrelplural = listENTITYREL(entityName,
    // search, pageable);
    bodyBuilder.appendFormalLine(String.format("%s<%s> %s = list%s(%s, search, pageable);",
        addTypeToImport(SpringJavaType.PAGE).getSimpleTypeName(),
        this.controllerDetailInfo.getEntity().getSimpleTypeName(),
        StringUtils.lowerCase(Noun
            .pluralOf(this.controllerDetailInfo.getEntity().getSimpleTypeName(), Locale.ENGLISH)),
        this.controllerDetailInfo.getEntity().getSimpleTypeName(),
        StringUtils.uncapitalize(this.controllerDetailInfo.getParentEntity().getSimpleTypeName())));

    // Get finder method
    ClassOrInterfaceTypeDetails serviceDetails =
        getTypeLocationService().getTypeDetails(this.controllerDetailInfo.getService());

    final LogicalPath serviceLogicalPath =
        PhysicalTypeIdentifier.getPath(serviceDetails.getDeclaredByMetadataId());
    final String serviceMetadataKey =
        ServiceMetadata.createIdentifier(serviceDetails.getType(), serviceLogicalPath);
    final ServiceMetadata serviceMetadata =
        (ServiceMetadata) getMetadataService().get(serviceMetadataKey);

    // Get parent field
    FieldMetadata parentRelationField = null;
    MemberDetails memberDetails = getMemberDetails(this.controllerDetailInfo.getParentEntity());
    List<FieldMetadata> parentFields = memberDetails.getFields();
    for (FieldMetadata parentField : parentFields) {
      if (parentField.getFieldName().getSymbolName()
          .equals(this.controllerDetailInfo.getParentReferenceFieldName())) {
        AnnotationMetadata oneToManyAnnotation = parentField.getAnnotation(JpaJavaType.ONE_TO_MANY);
        if (oneToManyAnnotation != null && (parentField.getFieldType().getFullyQualifiedTypeName()
            .equals(JavaType.LIST.getFullyQualifiedTypeName())
            || parentField.getFieldType().getFullyQualifiedTypeName()
                .equals(JavaType.SET.getFullyQualifiedTypeName()))) {
          parentRelationField = parentField;
          break;
        }
      }
    }

    Validate.notNull(parentRelationField,
        String.format("ERROR: '%s' must have a field related to '%s'",
            this.controllerDetailInfo.getParentEntity().getSimpleTypeName(),
            this.controllerDetailInfo.getEntity().getSimpleTypeName()));

    // Generating returnType
    Map<FieldMetadata, MethodMetadata> referencedCountDefinedMethods =
        serviceMetadata.getCountByReferenceFieldDefinedMethod();
    AnnotationAttributeValue<Object> attributeMappedBy =
        parentRelationField.getAnnotation(JpaJavaType.ONE_TO_MANY).getAttribute("mappedBy");

    Validate.notNull(attributeMappedBy,
        String.format("ERROR: The field '%s' of '%s' must have 'mappedBy' value",
            parentRelationField.getFieldName(),
            this.controllerDetailInfo.getParentEntity().getSimpleTypeName()));

    String mappedBy = (String) attributeMappedBy.getValue();
    MethodMetadata searchCountMethod = null;
    Iterator<Entry<FieldMetadata, MethodMetadata>> it =
        referencedCountDefinedMethods.entrySet().iterator();
    while (it.hasNext()) {
      Entry<FieldMetadata, MethodMetadata> countMethod = it.next();
      if (countMethod.getKey().getFieldName().getSymbolName().equals(mappedBy)) {
        searchCountMethod = countMethod.getValue();
        break;
      }
    }

    // long allAvailableEntityRelPlural =
    // entityRelNameService.countByENTITYNAMEPLURALContains(entityName);
    bodyBuilder.appendFormalLine(String.format("long allAvailable%s = %s.%s(%s);",
        Noun.pluralOf(this.controllerDetailInfo.getEntity().getSimpleTypeName(), Locale.ENGLISH),
        getServiceDetailField(this.controllerDetailInfo.getService()).getFieldName()
            .getSymbolNameUnCapitalisedFirstLetter(),
        searchCountMethod.getMethodName(),
        StringUtils.uncapitalize(this.controllerDetailInfo.getParentEntity().getSimpleTypeName())));

    // return new DatatablesData<ENTITYRELNAME>(entityrelplural,
    // allAvailableENTITYRELNAME, draw);
    bodyBuilder.appendFormalLine(String.format("return new %s<%s>(%s, allAvailable%s, draw);",
        addTypeToImport(this.datatablesDataType).getSimpleTypeName(),
        this.controllerDetailInfo.getEntity().getSimpleTypeName(),
        StringUtils.lowerCase(Noun
            .pluralOf(this.controllerDetailInfo.getEntity().getSimpleTypeName(), Locale.ENGLISH)),
        Noun.pluralOf(this.controllerDetailInfo.getEntity().getSimpleTypeName(), Locale.ENGLISH)));

    // Generating returnType
    JavaType returnType = new JavaType(this.datatablesDataType.getFullyQualifiedTypeName(), 0,
        DataType.TYPE, null, Arrays.asList(this.controllerDetailInfo.getEntity()));

    MethodMetadataBuilder methodBuilder =
        new MethodMetadataBuilder(this.metadataIdentificationString, Modifier.PUBLIC, methodName,
            returnType, parameterTypes, parameterNames, bodyBuilder);
    methodBuilder.setAnnotations(annotations);

    return methodBuilder.build();
    */
  }



  /*
   * ==============================================================================
   */

  /**
   * Method that returns list Datatables JSON method
   *
   * @return
   */
  public MethodMetadata getCurrentListDatatablesJSONMethod() {
    return this.listDatatablesJSONMethod;
  }

  /**
   * Method that returns create form Thymeleaf method
   *
   * @return
   */
  public MethodMetadata getCurrentCreateFormMethod() {
    return this.createFormMethod;
  }

  /**
   * Method that returns create Thymeleaf method
   *
   * @return
   */
  public MethodMetadata getCurrentCreateMethod() {
    return this.createMethod;
  }

  /**
   * Method that returns edit form Thymeleaf method
   *
   * @return
   */
  public MethodMetadata getCurrentEditForm() {
    return this.editFormMethod;
  }

  /**
   * Method that returns update Thymeleaf method
   *
   * @return
   */
  public MethodMetadata getCurrentUpdateMethod() {
    return this.updateMethod;
  }

  /**
   * Method that returns delete Thymeleaf method
   *
   * @return
   */
  public MethodMetadata getCurrentDeleteMethod() {
    return this.deleteMethod;
  }

  /**
   * Method that returns show Thymeleaf method
   *
   * @return
   */
  public MethodMetadata getCurrentShowMethod() {
    return this.showMethod;
  }

  /**
   * Method that returns populateForm method
   *
   * @return
   */
  public MethodMetadata getCurrentPopulateFormMethod() {
    return this.populateFormMethod;
  }

  /**
   * Method that returns if related entity is readOnly or not.
   *
   * @return
   */
  public boolean isReadOnly() {
    return this.readOnly;
  }

  @Override
  public String toString() {
    final ToStringBuilder builder = new ToStringBuilder(this);
    builder.append("identifier", getId());
    builder.append("valid", valid);
    builder.append("aspectName", aspectName);
    builder.append("destinationType", destination);
    builder.append("governor", governorPhysicalTypeMetadata.getId());
    builder.append("itdTypeDetails", itdTypeDetails);
    return builder.toString();
  }
}
