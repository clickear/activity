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
 * processtype流程类别定义
 */
var ProcessTypeCtrl = ['$scope', '$modal', function ($scope, $modal) {

    // Config for the modal window
    var opts = {
        template: 'editor-app/configuration/properties/processtype-popup.html?version=' + Date.now(),
        scope: $scope
    };

    // Open the dialog
    $modal(opts);
}];

var ProcessTypePopupCtrl = ['$scope', '$http','$rootScope', function ($scope, $http,$rootScope) {
    console.info($rootScope.modelData.tenantId);
    var tenantId = $rootScope.modelData.tenantId;
    $http({
        method: 'POST',
        url: KISBPM.URL.processTypeAll(),
        params: {"name": $scope.processName,"systemId":tenantId}
    }).success(function (response) {
        $scope.formData = response;
    })
    //数据验证，初始化
    if ($scope.property.value !== undefined && $scope.property.value !== null
        && $scope.property.value.processtypeInfo !== undefined
        && $scope.property.value.processtypeInfo !== null) {
        //这里是回显的
        $scope.processtype = $scope.property.value.processtype;
        $scope.processtypeInfo = $scope.property.value.processtypeInfo;
    } else {
        $scope.processtype = "";
        $scope.processtypeInfo = "";
    }


    $scope.save = function () {
        $scope.property.value = {};
        if($scope.processtype != ""){
            $scope.property.value.processtype = $scope.processtype;
            $scope.property.value.processtypeInfo = $scope.processtypeInfo;
        }
        $scope.updatePropertyInModel($scope.property);
        $scope.close();
    };

    $scope.close = function () {
        $scope.property.mode = 'read';
        $scope.$hide();
    };

    $scope.find = function () {
        $http({
            method: 'POST',
            url: KISBPM.URL.processTypeAll(),
            data: {"name": $scope.processName,"systemId":tenantId},
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            transformRequest: function (obj) {
                var str = [];
                for (var p in obj) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                }
                return str.join("&");
            }
        }).success(function (response) {
            $scope.formData = response;
        })


    }


    $scope.updateSelection = function ($event, position, list, info) {
        var checkbox = $event.target;
        if (checkbox.checked) {
            $scope.processtype = checkbox.value;
            $scope.processtypeInfo = info;
            angular.forEach(list, function (subscription, index) {
                if (position != index) {
                    subscription.checked = false;
                }
            });
        } else {
            $scope.processtype = "";
            $scope.processtypeInfo = "";
        }

    }

}];