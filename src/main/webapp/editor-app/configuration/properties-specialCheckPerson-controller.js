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
 * specialcheckperson流程类别定义
 */
var SpecialCheckpersonCtrl = ['$scope', '$modal', function ($scope, $modal) {

    // Config for the modal window
    var opts = {
        template: 'editor-app/configuration/properties/specialcheckperson-popup.html?version=' + Date.now(),
        scope: $scope
    };

    // Open the dialog
    $modal(opts);
}];

var SpecialCheckpersonPopupCtrl = ['$scope', '$http', function ($scope, $http) {

    var formData = {
        "page": 1,
        list: [
            {
                "id":1,
                "name":"设置前一个节点审核人的领导作为审核人"
            },
            {
                "id":2,
                "name":"发起人的部门负责人"
            },

            {
                "id":3,
                "name":"发起人"
            },
            {
                "id":4,
                "name":"部门负责人"
            },
            {
                "id":5,
                "name":"发起人一级部门负责人"
            },
            {
                "id":6,
                "name":"发起人二级部门负责人"
            },
            {
                "id":7,
                "name":"发起人三级部门负责人"
            },
            {
                "id":8,
                "name":"变动后一级部门负责人"
            },
            {
                "id":9,
                "name":"变动后二级部门负责人"
            },
            {
                "id":10,
                "name":"变动后三级部门负责人"
            }

        ]
    };
    $scope.formData = formData;
    console.info($scope.property.value);
    //数据验证，初始化
    if ($scope.property.value !== undefined && $scope.property.value !== null
        && $scope.property.value.specialcheckpersonInfo !== undefined
        && $scope.property.value.specialcheckpersonInfo !== null) {
        $scope.specialcheckperson = $scope.property.value.specialcheckperson;
        //这里优先级别的信息定义
        $scope.specialcheckpersonInfo = $scope.property.value.specialcheckpersonInfo;
    } else {
        $scope.specialcheckperson = "";
        $scope.specialcheckpersonInfo = "";
    }


    //执行保存
    $scope.save = function () {
       if($scope.specialcheckperson != ""){
           $scope.property.value = {};
           $scope.property.value.specialcheckperson = $scope.specialcheckperson;
           $scope.property.value.specialcheckpersonInfo = $scope.specialcheckpersonInfo;
       }
        $scope.updatePropertyInModel($scope.property);
        $scope.close();
    };

    $scope.close = function () {
        $scope.property.mode = 'read';
        $scope.$hide();
    };


    $scope.updateSelection = function ($event, position, list, info) {
        console.info($event + ":" + position + ":" + list)
        var checkbox = $event.target;
        if (checkbox.checked) {
            $scope.specialcheckperson = checkbox.value;
            $scope.specialcheckpersonInfo = info;
            angular.forEach(list, function (subscription, index) {
                if (position != index) {
                    subscription.checked = false;
                }
            });
        } else {
            $scope.specialcheckperson = {};
            $scope.specialcheckpersonInfo = {};
        }

    }

}];