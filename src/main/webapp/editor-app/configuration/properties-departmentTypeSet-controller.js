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
 * departmenttypeset岗位设置
 */
var departmentTypeSetCtrl = [ '$scope', '$modal', function($scope, $modal) {

    // Config for the modal window
    var opts = {
        template:  'editor-app/configuration/properties/departmentTypeSet-popup.html?version=' + Date.now(),
        scope: $scope
    };

    $modal(opts);
}];

var departmentTypeSetPopupCtrl = [ '$scope','$http', function($scope,$http) {
    //wyp3 流程挂接的形式
    $scope.processName;
    //分页预设值的数据
    
    //数据验证，初始化
    if ($scope.property.value !== undefined && $scope.property.value !== null
        && $scope.property.value.departmenttypesetInfo !== undefined
        && $scope.property.value.departmenttypesetInfo !== null) 
    {
       
        //这里是回显的
        $scope.departmenttypeset = $scope.property.value.departmenttypeset;
        //这里优先级别的信息定义
        $scope.departmenttypesetInfo = $scope.property.value.departmenttypesetInfo;
    } else {
        $scope.departmenttypeset = "";
        $scope.departmenttypesetInfo = "";
    }
    
	
    $scope.save = function() {
        $scope.property.value = {};
        $scope.property.value.departmenttypeset = $scope.departmenttypeset;
        $scope.property.value.departmenttypesetInfo = $scope.departmenttypesetInfo;
        console.info($scope.property.value.departmenttypesetInfo);
        $scope.updatePropertyInModel($scope.property);
        $scope.close();
    };
    
    $scope.close = function() {
    	$scope.property.mode = 'read';
    	$scope.$hide();
    };
    
        $http({
            method:'POST',
            url:KISBPM.URL.getDepartmetnType(),
            data:{"userName":$scope.processName},
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            transformRequest:function (obj) {
                var str = [];
                for (var p in obj) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                }
                return str.join("&");
            }
        }).success(function (response) {
           formData = response;
            buildTable(formData);
        })
    
    var buildTable=function(data){
    	$scope.formData=data;
    }
    
    $scope.updateSelection = function($event,position,list,info,key){
    	console.info($event+":"+position+":"+list)
    	var checkbox = $event.target;
        if(checkbox.checked){
            $scope.departmenttypeset=key;
            $scope.departmenttypesetInfo=info;
        }else {
            $scope.departmenttypeset="";
            $scope.departmenttypesetInfo="";
        }
    	
    	
    }
    	
}];