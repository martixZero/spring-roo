<!DOCTYPE html>
<html lang="en" data-layout-decorator="layouts/default-layout">

<head>
  <meta charset="UTF-8" data-th-remove="all"/>
  <meta http-equiv="X-UA-Compatible" content="IE=edge" data-th-remove="all"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"
    data-th-remove="all"/>
  <meta name="description"
    content="Spring Roo, a next-generation rapid application development tool for Java developers. With Roo you can easily build full Java applications in minutes."
    data-th-remove="all"/>
  <meta name="author"
    content="Spring Roo development team"
    data-th-remove="all"/>
  <#if isSecurityEnabled == true>
  <meta data-th-if="${r"${_csrf != null}"}" name="_csrf" data-th-content="${r"${_csrf.token}"}" />
  <meta data-th-if="${r"${_csrf != null}"}" name="_csrf_header" data-th-content="${r"${_csrf.headerName}"}" />
  </#if>

 <link rel="shortcut icon" href="../../static/public/img/favicon.ico"
    data-th-remove="all"/>
 <link rel="apple-touch-icon" href="../../static/public/img/apple-touch-icon.png"
    data-th-remove="all"/>

 <title data-th-text="|${r"#{"}label_list_entity(${r"#{"}${entityLabelPlural}${r"}"})${r"}"} - ${projectName}|">List ${entityName} - ${projectName}</title>

 <!-- Bootstrap -->
 <link rel="stylesheet" type="text/css"
   href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.css"
   data-th-remove="all"></link>

 <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
 <link rel="stylesheet" type="text/css"
   href="https://maxcdn.bootstrapcdn.com/css/ie10-viewport-bug-workaround.css"
   data-th-remove="all"></link>

 <!-- Font Awesome -->
 <link rel="stylesheet" type="text/css"
   href="https://maxcdn.bootstrapcdn.com/font-awesome/4.6.2/css/font-awesome.min.css"
   data-th-remove="all"/>

 <!-- Datatables -->
 <link
   data-th-href="@{/webjars/datatables/1.10.11/media/css/jquery.dataTables.css}"
   rel="stylesheet" type="text/css"
   href="https://cdn.datatables.net/1.10.11/css/jquery.dataTables.css"></link>

 <link
   data-th-href="@{/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.css}"
   rel="stylesheet" type="text/css"
   href="https://cdn.datatables.net/1.10.11/css/dataTables.bootstrap.css"></link>

 <link
   data-th-href="@{/webjars/datatables.net-responsive-bs/2.0.2/css/responsive.bootstrap.css}"
   rel="stylesheet" type="text/css"
   href="https://cdn.datatables.net/responsive/2.0.2/css/responsive.bootstrap.css"></link>

 <link
   data-th-href="@{/webjars/datatables.net-buttons-bs/1.1.2/css/buttons.bootstrap.css}"
   rel="stylesheet" type="text/css"
   href="https://cdn.datatables.net/buttons/1.1.2/css/buttons.bootstrap.css"></link>

 <link
   data-th-href="@{/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.css}"
   rel="stylesheet" type="text/css"
   href="https://cdn.datatables.net/select/1.1.2/css/select.bootstrap.css"></link>

 <!-- Bootswatch CSS custom -->
 <link rel="stylesheet" type="text/css"
   href="../../static/public/css/theme.css"
   data-th-remove="all"/>

 <!-- Roo CSS -->
 <link rel="stylesheet" type="text/css"
    href="../../static/public/css/springroo.css"
    data-th-remove="all"/>

 <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
 <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
 <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
 <![endif]-->

</head>

<#if userManagedComponents?has_content && userManagedComponents["body"]??>
  ${userManagedComponents["body"]}
<#else>
  <body id="body">

    <!-- HEADER -->
    <header role="banner">

        <!-- BANNER -->
        <div class="bg-header">
          <div class="jumbotron bg-banner">
              <div class="container">
                <h1 class="project-name">${projectName}</h1>
                <p class="project-tagline" data-th-text="${r"#{"}info_homepage_project${r"}"}">Hello, this is your home page.</p>
              </div>
          </div>
        </div>

        <!-- Main navbar -->
        <nav class="navbar navbar-inverse navbar-fixed-top">
         <div class="container">

            <!-- navbar-header -->
            <div class="navbar-header">
              <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#mainnav" aria-expanded="false">
                <span class="sr-only">Menu</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
              </button>

              <!-- Brand logo -->
              <div class="organization-logo navbar-brand">
                <a title="${projectName}" href="/">
                  <img alt="${projectName}" data-th-src="@{/public/img/logo.png}" src="../../static/public/img/logo.png" />
                </a>
              </div>
              <!-- Name application -->
              <div class="application-name navbar-brand hidden-xs"><a href="/" data-th-href="@{/}">${projectName}</a></div>

            </div>
            <!-- /navbar header -->

            <!-- menu -->
            <div id="mainnav" class="navbar-collapse collapse">

              <ul class="nav navbar-nav">
                <li class="active"><a href="#">Menu 1 active</a></li>
                <li><a href="#">Menu 2</a></li>
                <li class="dropdown">
                  <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Menu 3 dropdown<span class="caret"></span></a>
                  <ul class="dropdown-menu">
                    <li><a href="#">Submenu 1</a></li>
                    <li><a href="#">Submenu 2</a></li>
                    <li><a href="#">Submenu 3</a></li>
                  </ul>
                </li>
              </ul>

              <!-- Language -->
              <ul class="nav navbar-nav navbar-right upper-nav languages">
                <li class="dropdown">
                  <a class="dropdown-toggle" data-toggle="dropdown" role="button"
                    aria-haspopup="true" aria-expanded="false">
                    <span class="glyphicon glyphicon-globe" aria-hidden="true">
                    </span>&nbsp;<span class="hidden-sm"
                    data-th-text="${r"#{"}language_label${r"}"}">Language</span><span class="caret"></span>
                  </a>
                  <ul class="dropdown-menu" id="languageFlags">
                   <li id="enFlag"><a href="?lang=en"><img class="flag"
                    data-th-src="@{/public/img/en.png}" src="../static/public/img/en.png"
                    alt="English">&nbsp;<span data-th-text="${r"#{"}language_label_en${r"}"}">English</span></a> </li>
                   <li id="esFlag"><a href="?lang=es"><img class="flag"
                    data-th-src="@{/public/img/es.png}" src="../static/public/img/es.png"
                    alt="Spanish">&nbsp;<span data-th-text="${r"#{"}language_label_es${r"}"}">Spanish</span></a> </li>
                  </ul>
                </li>
              </ul>
              <!-- User menu -->
              <ul class="nav navbar-nav navbar-right upper-nav session">
                <li class="dropdown">
                  <a class="dropdown-toggle" data-toggle="dropdown" role="button"
                    aria-haspopup="true" aria-expanded="false">
                    <span class="glyphicon glyphicon-user" aria-hidden="true"></span>&nbsp;
                    <span class="hidden-sm" data-sec-authentication="principal.username">User</span>
                    <span class="caret"></span>
                  </a>
                  <ul class="dropdown-menu">
                   <li><a href="#"><span class="glyphicon glyphicon-wrench" aria-hidden="true"></span>
                   &nbsp;<span data-th-text="${r"#{"}label_profile${r"}"}">Admin Profile</span></a></li>
                   <li><a href="#"><span class="glyphicon glyphicon-lock" aria-hidden="true"></span>
                   &nbsp;<span data-th-text="${r"#{"}label_change_password${r"}"}">Change password</span></a></li>
                   <li>
                     <form data-th-action="@{/logout}" action="/logout" method="post">
                       <button type="submit" class="btn btn-link">
                        <span class="glyphicon glyphicon-log-out" aria-hidden="true"></span>
                        <span data-th-text="${r"#{"}label_logout ${r"}"}">Log out</span>
                       </button>
                     </form>
                   </li>
                 </ul>
                </li>
              </ul>
              <!-- User menu links -->
              <ul class="nav navbar-nav navbar-right upper-nav links">
                <li><a href="#"><span class="glyphicon glyphicon-envelope" aria-hidden="true"></span>
                &nbsp;<span class="hidden-sm" data-th-text="${r"#{"}label_contact${r"}"}">Contact</span></a></li>
                <li><a href="#"><span class="glyphicon glyphicon-question-sign" aria-hidden="true"></span>
                &nbsp;<span class="hidden-sm" data-th-text="${r"#{"}label_help${r"}"}">Help</span></a></li>
              </ul>
           </div>
          </div>
        </nav>

    </header>
    <!-- /HEADER -->

    <!--CONTAINER-->
    <div class="container bg-container">

      <!-- CONTENT -->
      <section data-layout-fragment="content">
        <div class="container-fluid content">
         <!--
  	   Only the inner content of the following tag "section" is included
  	   within the template, in the section "content"
          -->

          <h1 data-th-text="${r"#{"}${entityLabelPlural}${r"}"}">${entityName}s</h1>

          <!--TABLE-->
          <div class="table-responsive" id="containerFields">
            <#if entity.userManaged>
               ${entity.codeManaged}
            <#else>
              <table id="${entity.entityItemId}-table"
                   class="table table-striped table-hover table-bordered"
                   data-row-id="${entity.configuration.identifierField}"
                   data-select="single"
                   data-z="${entity.z}"
                   data-order="[[ 0, &quot;asc&quot; ]]"
                   data-th-attr="data-create-url=@{${entity.configuration.controllerPath}/create-form/}">
                <caption data-th-text="${r"#{"}label_list_entity(${r"#{"}${entityLabelPlural}${r"}"})${r"}"}">${entityName} List</caption>
                <thead>
                  <tr>
                    <#list fields as field>
                    <#if field.type != "LIST">
                    <th data-th-text="${r"#{"}${field.label}${r"}"}">${field.fieldName}</th>
                    </#if>
                    </#list>
                    <th data-th-text="${r"#{"}label_tools${r"}"}">Tools</th>
                  </tr>
                </thead>
                <tbody data-th-remove="all">
                  <tr>
                    <#list fields as field>
                    <#if field.type != "LIST">
                    <td>${field.fieldName}</td>
                    </#if>
                    </#list>
                    <td data-th-text="${r"#{"}label_tools${r"}"}">Tools</td>
                  </tr>
                </tbody>
              </table>
            </#if>
          </div>
          <!-- /TABLE -->

          <#if details?size != 0>
            <hr>
            <ul class="nav nav-tabs" id="nav-tabs">
            <#assign firstDetail=true>
            <#list details as detail>
              <#if firstDetail == true>
                  <li class="active">
                  <#if detail.tabLinkCode??>
                    ${detail.tabLinkCode}
                  <#else>
                    <a id="${detail.entityItemId}-table-tab" data-toggle="tab" href="#detail-${detail.entityItemId}" data-z="${detail.z}">${detail.fieldNameCapitalized}</a>
                  </#if>
                  </li>
                  <#assign firstDetail=false>
                <#else>
                    <li>
                    <#if detail.tabLinkCode??>
                      ${detail.tabLinkCode}
                    <#else>
                      <a id="${detail.entityItemId}-table-tab" data-toggle="tab" href="#detail-${detail.entityItemId}" data-z="${detail.z}">${detail.fieldNameCapitalized}</a>
                    </#if>
                    </li>
                </#if>
            </#list>
            </ul>

            <div class="tab-content" id="tab-content">
                  <#assign firstDetail=true>
                  <#list details as detail>
                      <#if firstDetail == true>
                          <div id="detail-${detail.entityItemId}" class="tab-pane active">
                          <#assign firstDetail=false>
                      <#else>
                          <div id="detail-${detail.entityItemId}" class="tab-pane">
                      </#if>
                          <!--START TABLE-->
                          <div class="table-responsive">
                            <#if detail.userManaged>
                              ${detail.codeManaged}
                            <#else>
                              <table id="${detail.entityItemId}-table"
                                class="table table-striped table-hover table-bordered"
                                data-z="${detail.z}"
                                data-row-id="${detail.configuration.identifierField}" data-defer-loading="0"
                                data-order="[[ 0, &quot;asc&quot; ]]"
                                data-create-url-function="create${detail.configuration.referencedFieldType}Url">
                                <caption data-th-text="${r"#{"}label_list_of_entity(${r"#{"}${detail.configuration.referencedFieldLabel}${r"}"})${r"}"}">${detail.fieldNameCapitalized} List</caption>
                                <thead>
                                  <tr>
                                    <#list detail.configuration.referenceFieldFields as referencedFieldField>
                                    <#if referencedFieldField != entityName>
                                        <th data-th-text="${r"#{"}${referencedFieldField.label}${r"}"}">${referencedFieldField.fieldName}</th>
                                    </#if>
                                    </#list>
                                    <th data-th-text="${r"#{"}label_tools${r"}"}">Tools</th>
                                  </tr>
                                </thead>
                                <tbody data-th-remove="all">
                                  <tr>
                                    <#list detail.configuration.referenceFieldFields as referencedFieldField>
                                    <#if referencedFieldField != entityName>
                                        <td>${referencedFieldField.fieldName}</td>
                                    </#if>
                                    </#list>
                                    <td data-th-text="${r"#{"}label_tools${r"}"}">Tools</td>
                                  </tr>
                                </tbody>
                              </table>
                            </#if>
                          </div>
                          <!--END TABLE-->
                      </div>
                    </#list>
                </div>

          </#if>

          <div class="clearfix">
            <div class="pull-left">
              <a href="../index.html" class="btn btn-default" data-th-href="@{/}">
                 <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
                 <span data-th-text="${r"#{label_back}"}">Back</span>
              </a>
            </div>
          </div>

        </div>
        <!-- /CONTENT-->

        <!-- MODAL -->
        <div
          data-layout-include="fragments/modal-confirm :: modalConfirm(id='confirmDelete${entityName}',
            title=${r"#{"}label_delete_entity(${r"#{"}${entityLabel}})}, message=${r"#{"}info_delete_item_confirm},
            onclickCallback='javascript:loading();javascript:jQuery.delete${entityName}();')">
            <!-- content replaced by modal-confirm fragment of modal-confirm.html -->
        </div>

        <div
          data-layout-include="fragments/modal :: modal(id='delete${entityName}', title=${r"#{"}label_delete_entity(${r"#{"}${entityLabel}})}, message=${r"#{"}info_deleted_items_number(1)})">

          <script type="text/javascript">
              function openDeleteModal(){
                jQuery('#staticModal').modal();
              }
            </script>

          <div class="modal fade" id="staticModal" tabindex="-1" role="dialog"
            aria-labelledby="staticModalLabel">
            <div class="modal-dialog" role="document">
              <div class="modal-content">
                <div class="modal-header">
                  <button type="button" class="close" data-dismiss="modal"
                    aria-label="${r"#{"}label_close${r"}"}">
                    <span aria-hidden="true">&times;</span>
                  </button>
                  <h2 class="modal-title" id="staticModalLabel" data-th-text="${r"#{"}label_delete${r"}"}">Delete</h2>
                </div>
                <div class="modal-body" id="staticModalBody">
                  <p data-th-text="${r"#{"}label_message${r"}"}">Message</p>
                </div>
              </div>
            </div>
          </div>
        </div>

      </section>
    </div>
    <!-- /CONTAINER-->

  <footer class="container">
     <div class="row">
      <div class="col-sm-6 col-sm-offset-3">
        <small class="clearfix">
            Made with <a href="http://projects.spring.io/spring-roo/" target="_blank">
            Spring Roo &copy; 2016</a> •
            We <span class="glyphicon glyphicon-heart"></span>
            <a href="https://github.com/spring-projects/spring-roo/" target="_blank">Open source</a> •
            <a data-th-href="@{/accessibility}" href="../accessibility.html"><span data-th-text="${r"#{"}label_accessibility${r"}"}">Accessibility</span></a>
        </small>
        </div>
      </div>
      <!-- certified logos -->
      <div class="row">
        <div class="col-sm-6 col-sm-offset-6 text-right">

         <a title="Explanation of WCAG 2.0 Level Double-A Conformance"
            target="_blank"
            href="http://www.w3.org/WAI/WCAG2AA-Conformance">
            <img height="32" width="88"
                 src="http://www.w3.org/WAI/wcag2AA"
                 alt="Level Double-A conformance, W3C WAI Web Content
                 Accessibility Guidelines 2.0">
         </a>
         &nbsp;
         <a title="Application developed and tested with OWASP -
             Open Web Application Security Project"
            target="_blank"
            href="https://www.owasp.org">
          <img height="32" width="90"
               src="../../static/public/img/owasp_logo.png"
               alt="Application developed and tested with OWASP">
         </a>
        </div>
      </div>
  </footer>

  <!-- JavaScript
    ================================================== -->
  <!-- Placed at the end of the document so that the pages load faster -->
  <!-- JQuery -->
  <script type="text/javascript" charset="utf8"
    src="https://code.jquery.com/jquery-1.12.3.js"></script>

  <!-- Bootstrap -->
  <script type="text/javascript"
    src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.js"></script>

  <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
  <script type="text/javascript" charset="utf8"
    src="../../static/public/js/ie10-viewport-bug-workaround.js"></script>

  <!-- MomentJS -->
  <script
     src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.13.0/moment.js">
  </script>
  <script src="../../static/public/js/moment-locale-es.js">
  </script>
  <script src="../../static/public/js/moment-defaults.js">
  </script>

  <!-- Javascript fragment -->
  <div data-layout-fragment="javascript">

    <!-- Progress bar -->
    <script type="text/javascript">
      function loading() {
        jQuery('.progress').removeClass('hide');
      }
     </script>

     <!-- Datatables fragment -->
     <div data-layout-include="fragments/js/datatables :: datatables">

       <!-- Datatables scripts ONLY for HTML templates
            Content replaced by the datatables template fragment datatables.html
       -->
       <script type="text/javascript" charset="utf8"
         src="https://cdn.datatables.net/1.10.11/js/jquery.dataTables.js"
         data-th-src="@{/webjars/datatables/1.10.11/media/js/jquery.dataTables.js}"></script>
       <script type="text/javascript" charset="utf8"
         src="https://cdn.datatables.net/1.10.11/js/dataTables.bootstrap.js"
         data-th-src="@{/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.js}"></script>
        <!-- Datatables responsive plugin -->
        <script type="text/javascript" charset="utf8"
          src="https://cdn.datatables.net/responsive/2.0.2/js/dataTables.responsive.js"
          data-th-src="@{/webjars/datatables.net-responsive/2.0.2/js/dataTables.responsive.js}"></script>
        <script type="text/javascript" charset="utf8"
          src="https://cdn.datatables.net/responsive/2.0.2/js/responsive.bootstrap.js"
          data-th-src="@{/webjars/datatables.net-responsive-bs/2.0.2/js/responsive.bootstrap.js}"></script>
        <!-- Datatables buttons plugins -->
        <script type="text/javascript" charset="utf8"
          src="https://cdn.datatables.net/buttons/1.1.2/js/dataTables.buttons.js"
          data-th-src="@{/webjars/datatables.net-buttons/1.1.2/js/dataTables.buttons.js}"></script>
        <script type="text/javascript" charset="utf8"
          src="https://cdn.datatables.net/buttons/1.1.2/js/buttons.bootstrap.js"
          data-th-src="@{/webjars/datatables.net-buttons-bs/1.1.2/js/buttons.bootstrap.js}"></script>
        <script type="text/javascript" charset="utf8"
          src="https://cdn.datatables.net/buttons/1.1.2/js/buttons.colVis.js"
          data-th-src="@{/webjars/datatables.net-buttons/1.1.2/js/buttons.colVis.js}"></script>
        <script type="text/javascript" charset="utf8"
          src="https://cdn.datatables.net/buttons/1.1.2/js/buttons.flash.js"
          data-th-src="@{/webjars/datatables.net-buttons/1.1.2/js/buttons.flash.js}"></script>
        <script type="text/javascript" charset="utf8"
          src="https://cdn.datatables.net/buttons/1.1.2/js/buttons.html5.js"
          data-th-src="@{/webjars/datatables.net-buttons/1.1.2/js/buttons.html5.js}"></script>
        <!-- Datatables select plugin -->
        <script type="text/javascript" charset="utf8"
          src="https://cdn.datatables.net/select/1.1.2/js/dataTables.select.js"
          data-th-src="@{/webjars/datatables.net-select/1.1.2/js/dataTables.select.js}"></script>

    </div>


    <!-- Datatables page configs -->
    <#if entity.javascriptCode["${entity.entityItemId}-table-javascript"]??>
      ${entity.javascriptCode["${entity.entityItemId}-table-javascript"]}
    <#else>
      <script type="text/javascript" data-th-inline="javascript" id="${entity.entityItemId}-table-javascript" data-z="${entity.z}">
       var ${entityName}Table;
       jQuery(document).ready( function () {
          ${entityName}Table = jQuery('#${entity.entityItemId}-table').DataTable({
              'ajax': function (data, callback, settings) {
                    loadData(data, callback, settings, [[@{${entity.configuration.controllerPath}/}]], this);
                },
              'columns': [
                <#list fields as field>
                { 'data': '${field.fieldName}' },
                </#list>
                {
                  'data': '${entity.configuration.identifierField}',
                  'orderable': false,
                  'searchable': false,
                  'render': function ( data, type, full, meta ) {
                      var baseUrl = [[@{${entity.configuration.controllerPath}/}]];
                      return '<a role="button" class="btn-action showInfo" href="' + baseUrl + data + '" data-th-text="${r"#{label_show}"}">Show</a>' +
                      '<a role="button" class="btn-action edit" href="' + baseUrl + data + '/edit-form" data-th-text="${r"#{label_edit}"}">Edit</a>' +
                      '<a role="button" class="btn-action delete" data-th-text="${r"#{label_delete}"}" onclick="javascript:jQuery.delete${entityName}Modal(' + data + ')"/>'
                  }
                }
              ]
          });

          jQuery.extend({
             'current${entityName}Id': undefined,
             'delete${entityName}Modal' : function(${entity.configuration.identifierField}) {
                   jQuery.current${entityName}Id = ${entity.configuration.identifierField};
                   if (jQuery.current${entityName}Id) {
                     jQuery('#confirmDelete${entityName}Modal').modal('show');
                   }
             },
             'delete${entityName}': function() {
                 var baseUrl = [[@{${entity.configuration.controllerPath}/}]];
                 <#if isSecurityEnabled == true>
                  var token = jQuery("meta[name='_csrf']");
                  var header = jQuery("meta[name='_csrf_header']");
                 </#if>
                 jQuery.ajax({
                     url: baseUrl + jQuery.current${entityName}Id,
                     type: 'DELETE',
                     <#if isSecurityEnabled == true>
                     beforeSend : function(request) {
                        if(token != null && token.length > 0 && header != null && header.length > 0) {
                          request.setRequestHeader(header.attr("content"), token.attr("content"));
                        }
                     },
                     </#if>
                     success: function(result) {
                       jQuery('#confirmDelete${entityName}Modal').modal('hide');

                       /** Refresh Datatables */
                       jQuery('#${entity.entityItemId}-table').DataTable().ajax.reload();
                       jQuery('#delete${entityName}ModalBody').empty();
                       jQuery('#delete${entityName}ModalBody').append('<p data-th-text="|${r"#{info_deleted_items_number(1)}"}|" >1 removed item</p>');
                       jQuery('#delete${entityName}Modal').modal();

                     },
                     error: function(jqXHR) {
                       jQuery('#confirmDelete${entityName}Modal').modal('hide');
                       jQuery('#delete${entityName}ModalBody').empty();
                       if(jqXHR.status == 428){
                         jQuery('#delete${entityName}ModalBody').append('<p data-th-text="|${r"#{error_deleting_item_with_relationships}"}|" >To delete the selected item, must delete its related elements before.</p>');
                       } else {
                         jQuery('#delete${entityName}ModalBody').append('<p data-th-text="|${r"#{error_deleting_item}"}|" >Error deleting selected item.</p>');
                       }
                       jQuery('#delete${entityName}Modal').modal('show');
                     }
                  });
             }
           });
        });
        </script>
      </#if>

     <#if details?size != 0>
     	<#assign firstDetail = true>
         <#list details as detail>
         <#if detail.javascriptCode["${detail.entityItemId}-table-javascript"]??>
           ${detail.javascriptCode["${detail.entityItemId}-table-javascript"]}
         <#else>
           <script type="text/javascript" data-th-inline="javascript" id="${detail.entityItemId}-table-javascript" data-z="${detail.z}">
           jQuery(document).ready( function () {
             <#if firstDetail == false>
             	function initialize${detail.fieldNameCapitalized}Table() {
             </#if>
               jQuery('#${detail.entityItemId}-table').DataTable({
                 'buttons' : [
                  {
                        'extend' : 'colvis',
                        'className' : 'btn-action'
                  },
                  {
                        'extend' : 'pageLength',
                        'className' : 'btn-action'
                  }
                ],
                'columns': [
                  <#list detail.configuration.referenceFieldFields as referencedFieldField>
                  <#if referencedFieldField != entityName>
                    { 'data': '${referencedFieldField.fieldName}' },
                  </#if>
                  </#list>
                  {
                    'data': '${detail.configuration.identifierField}',
                    'orderable': false,
                    'searchable': false,
                    'render': function ( data, type, full, meta ) {
                        return '';
                    }
                  }
                ]
            });
              <#if firstDetail == false>
         }
              	jQuery('#${detail.entityItemId}-tab').on('shown.bs.tab', function (e) {
				if (jQuery.fn.DataTable.isDataTable('#${detail.entityItemId}-table') === false) {
					initialize${detail.fieldNameCapitalized}Table();
					var url${detail.fieldNameCapitalized} = jQuery.${detail.fieldNameCapitalized}BaseUrl();
					if (url${detail.fieldNameCapitalized}) {
						jQuery('#${detail.entityItemId}-table').DataTable().ajax.url(urlPruebaPets).load();
					}
				}
  			});
              </#if>
              jQuery.extend({
                'current${entityName}Id': undefined,
                '${detail.fieldNameCapitalized}BaseUrl': function() {
                  if(jQuery.current${entityName}Id) {
                    return [[@{${entity.configuration.controllerPath}/}]] + jQuery.current${entityName}Id + '${detail.configuration.controllerPath}/';
                  }
                  return undefined;
                },
                'create${detail.fieldNameCapitalized}Url': function() {
                  if(jQuery.current${entityName}Id) {
                    return jQuery.${detail.fieldNameCapitalized}BaseUrl() + jQuery.createUri + '/';
                  }
                  return undefined;
                },
                'update${detail.fieldNameCapitalized}Url': function(${detail.fieldNameCapitalized}Id) {
                  if(jQuery.current${entityName}Id) {
                    return jQuery.${detail.fieldNameCapitalized}BaseUrl() + ${detail.fieldNameCapitalized}Id + '/'+ jQuery.editUri + '/';
                  }
                  return undefined;
                },
                'delete${detail.fieldNameCapitalized}Url': function(${detail.fieldNameCapitalized}Id) {
                  if(jQuery.current${entityName}Id) {
                    return jQuery.${detail.fieldNameCapitalized}BaseUrl() + ${detail.fieldNameCapitalized}Id + '/'+ jQuery.deleteUri + '/';
                  }
                  return undefined;
                }
              });
         	<#assign firstDetail = false>
         	});
          </script>
          </#if>
         </#list>
       <#if entity.javascriptCode["${entity.entityItemId}-table-javascript-firstdetail"]??>
          ${entity.javascriptCode["${entity.entityItemId}-table-javascript-firstdetail"]}
       <#else>
         <script type="text/javascript" data-th-inline="javascript" id="${entity.entityItemId}-table-javascript-firstdetail" data-z="${entity.z}">
       jQuery(document).ready( function () {
         ${entityName}Table.on( 'select', function ( e, dt, type, indexes ) {
            if ( type === 'row' ) {
              var new${entityName}Id = ${entityName}Table.rows( indexes ).ids()[0];
              if (jQuery.current${entityName}Id != new${entityName}Id) {
                jQuery.current${entityName}Id = new${entityName}Id;
                <#list details as detail>
                  var url${detail.fieldNameCapitalized} = jQuery.${detail.fieldNameCapitalized}BaseUrl();
                  if (jQuery.fn.DataTable.isDataTable('#${detail.entityItemId}-table') === true) {
                 	jQuery('#${detail.entityItemId}-table').DataTable().ajax.url( url${detail.fieldNameCapitalized} ).load();
                }
                </#list>
              }
            }
          });
          });
        </script>
     </#if>
   </#if>

  </div>

  <!-- Application -->
  <script src="../../static/public/js/main.js"></script>

</body>
</#if>

</html>
