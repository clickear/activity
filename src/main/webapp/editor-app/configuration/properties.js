/*
 * Activiti Modeler component part of the Activiti project
 * Copyright 2005-2014 Alfresco Software, Ltd. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
'use strict';

var KISBPM = KISBPM || {};
KISBPM.PROPERTY_CONFIG =
{
    "string": {
        "readModeTemplateUrl": "editor-app/configuration/properties/default-value-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/string-property-write-mode-template.html"
    },
    "boolean": {
        "templateUrl": "editor-app/configuration/properties/boolean-property-template.html"
    },
    "text" : {
        "readModeTemplateUrl": "editor-app/configuration/properties/default-value-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/text-property-write-template.html"
    },
    "kisbpm-multiinstance" : {
        "readModeTemplateUrl": "editor-app/configuration/properties/multiinstance-property-write-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/multiinstance-property-write-template.html"
    },
    //test 自定义类型
    "kisbpm-multiinstance1" : {
        "readModeTemplateUrl": "editor-app/configuration/properties/nodeBelong-property-write-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/nodeBelong-property-write-template.html"
    },
    
    "oryx-formproperties-complex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/form-properties-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/form-properties-write-template.html"
    },

    //节点表单相关变量 wyp 
    "oryx-formwithvar-complex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/form-properties-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/form-properties-write-template.html"
    },
    //wyp6
    "oryx-roleset-complex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/roleSet-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/roleSet-write-template.html"
    },
 

    //部门类别设置
    "oryx-departmenttypeset-complex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/departmentTypeSet-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/departmentTypeSet-write-template.html"
    },
    //部门设置
    "oryx-departmentset-complex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/departmentSet-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/departmentSet-write-template.html"
    },


    "oryx-executionlisteners-multiplecomplex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/execution-listeners-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/execution-listeners-write-template.html"
    },
    "oryx-tasklisteners-multiplecomplex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/task-listeners-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/task-listeners-write-template.html"
    },
    "oryx-eventlisteners-multiplecomplex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/event-listeners-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/event-listeners-write-template.html"
    },
    "oryx-usertaskassignment-complex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/assignment-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/assignment-write-template.html"
    },
    "oryx-formkeydefinition-complex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/formkeydefinition-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/formkeydefinition-write-template.html"
    },
    //流程的类别 wyp1
    "oryx-prioritydefinition-complex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/prioritydefinition-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/prioritydefinition-write-template.html"
    },
    //wyp
    "oryx-setcheckperson-complex": {
    "readModeTemplateUrl": "editor-app/configuration/properties/setCheckPerson-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/setCheckPerson-write-template.html"
},
    //wyp6
    "oryx-candidategroups-complex": {
    "readModeTemplateUrl": "editor-app/configuration/properties/candidategroups-display-template.html", 
        "writeModeTemplateUrl": "editor-app/configuration/properties/candidategroups-write-template.html"
},
    //岗位设置wy
    "oryx-departmentname-complex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/departmentName-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/departmentName-write-template.html"
    },
    //specialcheckperson
    "oryx-specialcheckperson-complex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/specialcheckperson-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/specialcheckperson-write-template.html"
    },
    //process_namespace 流程类别
    "oryx-process_namespace-complex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/processtype-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/processtype-write-template.html"
    },
    //设计发起人candidateStarterUsers的校验
    "oryx-candidatestarterusers-complex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/candidatestarterusers-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/candidatestarterusers-write-template.html"
    },

    "oryx-candidatestartergroups-complex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/candidatestartergroups-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/candidatestartergroups-write-template.html"
    },
    
    
    


    //流程挂接 wyp2
    "oryx-documentation-complex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/documentation-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/documentation-write-template.html"
    },
    "oryx-servicetaskfields-complex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/fields-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/fields-write-template.html"
    },
    "oryx-callactivityinparameters-complex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/in-parameters-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/in-parameters-write-template.html"
    },
    "oryx-callactivityoutparameters-complex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/out-parameters-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/out-parameters-write-template.html"
    },
    "oryx-subprocessreference-complex": {
        "readModeTemplateUrl": "editor-app/configuration/properties/subprocess-reference-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/subprocess-reference-write-template.html"
    },
    "oryx-sequencefloworder-complex" : {
        "readModeTemplateUrl": "editor-app/configuration/properties/sequenceflow-order-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/sequenceflow-order-write-template.html"
    },


    "oryx-conditionsequenceflow-complex" : {
        "readModeTemplateUrl": "editor-app/configuration/properties/condition-expression-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/condition-expression-write-template.html"
    },
    

    
    
    //wyp 5 自定义的任务节点信息池子弹框
    "oryx-taskwithvar-complex" : {
        "readModeTemplateUrl": "editor-app/configuration/properties/taskWithVar-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/taskWithVar-write-template.html"
    },




    "oryx-signaldefinitions-multiplecomplex" : {
        "readModeTemplateUrl": "editor-app/configuration/properties/signal-definitions-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/signal-definitions-write-template.html"
    },
    "oryx-signalref-string" : {
        "readModeTemplateUrl": "editor-app/configuration/properties/default-value-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/signal-property-write-template.html"
    },
    "oryx-messagedefinitions-multiplecomplex" : {
        "readModeTemplateUrl": "editor-app/configuration/properties/message-definitions-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/message-definitions-write-template.html"
    },
    "oryx-messageref-string" : {
        "readModeTemplateUrl": "editor-app/configuration/properties/default-value-display-template.html",
        "writeModeTemplateUrl": "editor-app/configuration/properties/message-property-write-template.html"
    }
};
