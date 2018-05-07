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
 * departmentname岗位设置
 */
var DepartmentNameCtrl = ['$scope', '$modal', function ($scope, $modal) {

    // Config for the modal window
    var opts = {
        template: 'editor-app/configuration/properties/departmentName-popup.html?version=' + Date.now(),
        scope: $scope
    };

    $modal(opts);
}];

var DepartmentNamePopupCtrl = ['$scope', '$http','$q', '$translate', function ($scope, $http,$q, $translate) {
    //wyp3 流程挂接的形式
    $scope.processName;
    $scope.admins = [];
    if($scope.property.value == undefined || $scope.property.value == null || $scope.property.value.admins == undefined
        || $scope.property.value.admins == null){
        $scope.admins = [];
    }else {
        $scope.admins = $scope.property.value.admins;
    }

        //分页预设值的数据
    $scope.splicePage = {
        page: 1,//当前页
        pageSize: 5,//每页显示数
        total: 0,//总共数量
        totalPage: 0//总共页数

    };
    var idPromise = $translate('PROPERTY.SPECIFICROLE.ID');
    var namePromise = $translate('PROPERTY.SPECIFICROLE.NAME');
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
        method: 'POST',
        url: KISBPM.URL.getRole(),
        params: {"page": $scope.splicePage.page, "pageSize": $scope.splicePage.pageSize, "userName": $scope.processName}
    }).success(function (response) {
        $scope.formData = response;
    })

    //数据验证，初始化
    if ($scope.property.value !== undefined && $scope.property.value !== null
        && $scope.property.value.departmentnameInfo !== undefined
        && $scope.property.value.departmentnameInfo !== null) {
        $scope.admins = [{"id": $scope.property.value.departmentname,"name":$scope.property.value.departmentnameInfo}]
        
    } else {
        $scope.departmentname = {};
        $scope.departmentnameInfo = {};
    }
    

    $scope.save = function () {
        $scope.property.value = {};
        $scope.property.value.admins = $scope.admins;
        $scope.updatePropertyInModel($scope.property);
        $scope.close();
    };

    $scope.close = function () {
        $scope.property.mode = 'read';
        $scope.$hide();
    };

    $scope.find = function () {
        var formData = "";
        $http({
            method: 'POST',
            url: KISBPM.URL.getRole(),
            data: {
                "page": $scope.splicePage.page,
                "pageSize": $scope.splicePage.pageSize,
                "userName": $scope.processName
            },
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            transformRequest: function (obj) {
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

    $scope.findUpPage = function (prePage, processName) {
        $http({
            method: 'POST',
            url: KISBPM.URL.getRole(),
            params: {"page": prePage - 1, "pageSize": $scope.splicePage.pageSize, "userName": processName}
        }).success(function (response) {
            console.info(response);
            formData = response;
            buildTable(formData)
        });
        console.info(prePage);
    }
    //下一页
    $scope.findDowPage = function (prePage, processName) {
        var formData = "";
        $http({
            method: 'POST',
            url: KISBPM.URL.getRole(),
            params: {"page": prePage + 1, "pageSize": $scope.splicePage.pageSize, "userName": processName}
        }).success(function (response) {
            console.info(response);
            formData = response;
            buildTable(formData)
        });
    }

    var buildTable = function (data) {
        $scope.formData = data;
    }
    //更新部门
    $scope.updateSelection = function (formId,formName,event) {
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