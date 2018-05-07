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
 * fromkeydefinition
 */
var FormkeydefinitionCtrl = [ '$scope', '$modal', function($scope, $modal) {

    // Config for the modal window
    var opts = {
        template:  'editor-app/configuration/properties/formkeydefinition-popup.html?version=' + Date.now(),
        scope: $scope
    };

    // Open the dialog
    $modal(opts);
}];

var FormkeydefinitionPopupCtrl = [ '$scope','$http', function($scope,$http) {

	//数据验证，初始化
    var formData={"page":1,"total":20,list:[{"id":1,"name":"发起人节点"},{"id":2,"name":"知会节点"}]};
    $scope.formData=formData;
    console.info($scope.property.value);
    if ($scope.property.value !== undefined && $scope.property.value !== null
        && $scope.property.value.forminfo !== undefined
        && $scope.property.value.forminfo !== null) 
    {
        $scope.formkeydefinition = $scope.property.value.formkeydefinition;
        $scope.forminfo = $scope.property.value.forminfo;
    } else {
        $scope.formkeydefinition = "";
        $scope.forminfo = "";
    }
    
	
    $scope.save = function() {
        $scope.property.value = {};
        if( $scope.formkeydefinition != ""){
            $scope.property.value.formkeydefinition = $scope.formkeydefinition;
            $scope.property.value.forminfo = $scope.forminfo;
        }
        $scope.updatePropertyInModel($scope.property);
        $scope.close();
    };
    
    $scope.close = function() {
    	$scope.property.mode = 'read';
    	$scope.$hide();
    };
    var buildTable=function(data){
    	$scope.formData=data;
    }
    
    $scope.updateSelection = function($event,position,list,info){
    	console.info($event+":"+position+":"+list)
    	var checkbox = $event.target;
        if(checkbox.checked){
            $scope.formkeydefinition=checkbox.value
            $scope.forminfo=info;
            angular.forEach(list, function(subscription, index) {
                if (position != index) {
                    subscription.checked = false;
                }
            });
        }else {
            $scope.formkeydefinition= "";
            $scope.forminfo= "";
        }
       
        

    }
    	
}];