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

/*
 * prioritydefinition流程类别定义
 */
var PrioritydefinitionCtrl = [ '$scope', '$modal', function($scope, $modal) {

    // Config for the modal window
    var opts = {
        template:  'editor-app/configuration/properties/prioritydefinition-popup.html?version=' + Date.now(),
        scope: $scope
    };

    // Open the dialog
    $modal(opts);
}];

var PrioritydefinitionPopupCtrl = [ '$scope','$http', function($scope,$http) {

    var formData={"page":1,"total":20,list:[{"id":1,"name":"表单审核节点"},{"id":2,"name":"知会节点"}]};

    $scope.formData=formData;
    //数据验证，初始化
    if ($scope.property.value !== undefined && $scope.property.value !== null
        && $scope.property.value.prioritydefinitionInfo !== undefined
        && $scope.property.value.prioritydefinitionInfo !== null) 
    {
       
        //这里是回显的
        $scope.prioritydefinition = $scope.property.value.prioritydefinition;
        console.info();
        //这里优先级别的信息定义
        $scope.prioritydefinitionInfo = $scope.property.value.prioritydefinitionInfo;
    } else {
        $scope.prioritydefinition = {};
        $scope.prioritydefinitionInfo = {};
    }
	
    //form 初始化
    //这里不太明白 fsj1
    if ($scope.prioritydefinitionInfo.form == undefined || $scope.prioritydefinitionInfo.form.length == 0)
    {
    	$scope.prioritydefinition = "";
    	$scope.prioritydefinitionInfo.form = "";
    }
	
    $scope.save = function() {
        $scope.property.value = {};
        if($scope.prioritydefinition==""){
            $scope.property.value.prioritydefinition = "1";
            $scope.property.value.prioritydefinitionInfo = "表单审核节点";
        }else {
            $scope.property.value.prioritydefinition = $scope.prioritydefinition;
            $scope.property.value.prioritydefinitionInfo = $scope.prioritydefinitionInfo;

        }

        $scope.updatePropertyInModel($scope.property);
        $scope.close();
    };
    
    $scope.close = function() {
    	$scope.property.mode = 'read';
    	$scope.$hide();
    };
  
    $scope.find=function(){
    	var formData={"page":1,"total":20,list:[{"id":0,"name":"审核节点无人跳过"},{"id":1,"name":"审核节点无人不能跳过"},{"id":2,"name":"部门内审核节点"},{"id":3,"name":"知会节点"}]};
        //var formData = "";
        buildTable(formData);
//        });
    };
    
    var buildTable=function(data){
    	$scope.formData=data;
    }
    
    $scope.updateSelection = function($event,position,list,info){
    	console.info($event+":"+position+":"+list)
    	var checkbox = $event.target; 
    	$scope.prioritydefinition=checkbox.value
    	$scope.prioritydefinitionInfo=info
    	angular.forEach(list, function(subscription, index) {
    		console.info(position+"|"+index)
            if (position != index) {
              subscription.checked = false;
            }
          });
    }
    	
}];