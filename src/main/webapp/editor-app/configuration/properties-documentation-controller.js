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
 * documentation流程挂接的js
 */
var DocumentationCtrl = [ '$scope', '$modal', function($scope, $modal) {

    // Config for the modal window
    var opts = {
        template:  'editor-app/configuration/properties/documentation-popup.html?version=' + Date.now(),
        scope: $scope
    };

    // Open the dialog
    $modal(opts);
}];

var DocumentationPopupCtrl = [ '$scope','$http', function($scope,$http) {
    //wyp3 流程挂接的形式
    $scope.processName = "";

    //分页预设值的数据
    $scope.splicePage={
        page:1,//当前页
        pageSize:5,//每页显示数
        total:0,//总共数量
        totalPage:0//总共页数

    };
    $http({
        method:'POST',
        url:'/hm_activity/processDefinition/queryPageAllProcess',
        params:{"page":$scope.splicePage.page,"pageSize":$scope.splicePage.pageSize,"name":$scope.processName}
    }).success(function (response) {
        $scope.formData = response;
    })
    
    //数据验证，初始化
    if ($scope.property.value !== undefined && $scope.property.value !== null
        && $scope.property.value.documentationInfo !== undefined
        && $scope.property.value.documentationInfo !== null) 
    {
       
        //这里是回显的
        $scope.documentation = $scope.property.value.documentation;
        console.info();
        //这里优先级别的信息定义
        $scope.documentationInfo = $scope.property.value.documentationInfo;
    } else {
        $scope.documentation = {};
        $scope.documentationInfo = {};
    }
	
    //form 初始化
    //这里不太明白 fsj1
    if ($scope.documentationInfo.form == undefined || $scope.documentationInfo.form.length == 0)
    {
    	$scope.documentation = "";
    	$scope.documentationInfo.form = "";
    }
	
    $scope.save = function() {
        $scope.property.value = {};
        $scope.property.value.documentation = $scope.documentation;
        
        $scope.property.value.documentationInfo = $scope.documentationInfo;
        $scope.updatePropertyInModel($scope.property);
        $scope.close();
    };
    
    $scope.close = function() {
    	$scope.property.mode = 'read';
    	$scope.$hide();
    };
  
    $scope.find=function(){
        var formData="";
        $http({
            method:'POST',
            url:'/hm_activity/processDefinition/queryPageAllProcess',
            params:{"page":$scope.splicePage.page,"pageSize":$scope.splicePage.pageSize,"name":$scope.processName}
        }).success(function (response) {
           formData = response;
            buildTable(formData);
        })
        
    };

    $scope.findUpPage= function(prePage){
        console.info(prePage);
        $http({
            method:'POST',
            url:'/hm_activity/processDefinition/queryPageAllProcess',
            params:{"page":prePage-1,"pageSize":$scope.splicePage.pageSize,"name":$scope.processName}
        }).success(function (response) {
            console.info(response);
            formData=response;
            buildTable(formData)
        });
        console.info(prePage);
    }
    //下一页
    $scope.findDowPage= function(prePage){
        var formData="";
        $http({
            method:'POST',
            url:'/hm_activity/processDefinition/queryPageAllProcess',
            params:{"page":prePage+1,"pageSize":$scope.splicePage.pageSize,"name":$scope.processName}
        }).success(function (response) {
            console.info(response);
            formData=response;
            buildTable(formData)
        });
    }
    
    var buildTable=function(data){
    	$scope.formData=data;
    }
    
    $scope.updateSelection = function($event,position,list,info,key){
    	console.info($event+":"+position+":"+list)
    	var checkbox = $event.target;
        console.info(checkbox);
        console.info($event);
        console.info(key);
    console.info(checkbox.value+"|||||||||||||")
    	$scope.documentation=key;
        console.info("测是保存日志");
        console.info($scope.documentation);
    	$scope.documentationInfo=info;
    	angular.forEach(list, function(subscription, index) {
    		console.info(position+"|"+index)
            if (position != index) {
              subscription.checked = false;
            }
          });
    }
    	
}];