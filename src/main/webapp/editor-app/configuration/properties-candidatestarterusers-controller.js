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
 * setCheckPerson流程挂接的js
 */
var candidateStarterUsersCtrl = [ '$scope', '$modal', function($scope, $modal) {

    // Config for the modal window
    var opts = {
        template:  'editor-app/configuration/properties/candidatestarterusers-popup.html?version=' + Date.now(),
        scope: $scope
    };

    $modal(opts);
}];

var candidateStarterPopupCtrl = [ '$scope','$http','$q', '$translate', function($scope,$http,$q, $translate) {
    //wyp3 流程挂接的形式
    $scope.processName = "";
    $scope.admins = [];

    //分页预设值的数据
    $scope.splicePage={
        page:1,//当前页
        pageSize:5,//每页显示数
        total:0,//总共数量
        totalPage:0//总共页数

    };
    var idPromise = $translate('PROPERTY.ASSIGNMENT.ID');
    var namePromise = $translate('PROPERTY.ASSIGNMENT.NAME');
    
    $scope.labels = {};
    $scope.selectedProperties = [];

    $q.all([idPromise, namePromise]).then(function(results) {
        $scope.labels.idLabel = results[0];
        $scope.labels.nameLabel = results[1];
        $scope.translationsRetrieved = true;
        $scope.gridOptions = {
            data: 'admins',
            enableRowReordering: true,
            headerRowHeight: 28,
            multiSelect: false,
            keepLastSelected : false,
            selectedItems: $scope.selectedProperties,
            columnDefs: [{ field: 'id', displayName: $scope.labels.idLabel,width:'10%' },
                { field: 'name', displayName: $scope.labels.nameLabel}
               ]
        };
    });
    //删除
    $scope.removeProperty = function() {
        console.info($scope.selectedProperties[0]);
        if ($scope.selectedProperties.length > 0) {
            var index = $scope.admins.indexOf($scope.selectedProperties[0]);
            $scope.gridOptions.selectItem(index, false);
            $scope.admins.splice(index, 1);
            $scope.selectedProperties.length = 0;
            if (index < $scope.admins.length) {
                $scope.gridOptions.selectItem(index + 1, true);
            } else if ($scope.admins.length > 0) {
                $scope.gridOptions.selectItem(index - 1, true);
            }
        }
    };
    
    $http({
        method:'POST',
        url:KISBPM.URL.getPeople(),
        params:{"page":$scope.splicePage.page,"pageSize":$scope.splicePage.pageSize,"userName":$scope.processName}
    }).success(function (response) {
        $scope.formData = response;
    })
    
    if($scope.property.value == undefined || $scope.property.value == null || $scope.property.value.admins == undefined
        || $scope.property.value.admins == null){
        $scope.admins = [];
    }else {
        var s = $scope.property.value.admins;
        $scope.admins  =  eval(s);
    }

    $scope.save = function() {
        $scope.property.value = {};
        $scope.property.value.setCheckPerson = $scope.setCheckPerson;
        if($scope.setCheckPerson==""){
            //如果不是int类型
            $scope.property.value.setCheckPerson= $scope.setCheckPersonInfo;
        }
        //设置审核人
        $scope.property.value.setCheckPersonInfo = $scope.setCheckPersonInfo;
        $scope.property.value.admins = $scope.admins;
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
            url:KISBPM.URL.getPeople(),
            data:{"page":$scope.splicePage.page,"pageSize":$scope.splicePage.pageSize,"userName":$scope.processName},
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
        
    };

    $scope.findUpPage= function(prePage){
        console.info(prePage);
        $http({
            method:'POST',
            url:KISBPM.URL.getPeople(),
            data:{"page":prePage-1,"pageSize":$scope.splicePage.pageSize,"userName":$scope.processName},
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
    }
    //下一页
    $scope.findDowPage= function(prePage){
        var formData="";
        $http({
            method:'POST',
            url:KISBPM.URL.getPeople(),
            data:{"page":prePage+1,"pageSize":$scope.splicePage.pageSize,"userName":$scope.processName},
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
    }
    
    var buildTable=function(data){
    	$scope.formData=data;
    }
    
    $scope.updateSelection = function(formId,formName,event){
        console.info(event);
        console.info("111");
        var action = event.target;
        if(action.checked){
            //选中就添加
            console.info($scope.admins.indexOf({id:formId,name:formName}));
            var count = 0;
            angular.forEach($scope.admins,function(data){
                if(data.id == formId){
                    count++;
                }
            })
            if(count == 0){
                $scope.admins.push({id:formId,name:formName});
            }
            

        }

        
    }
    	
}];