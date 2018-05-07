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
 * Execution listeners
 */

var KisBpmMultiInstanceCtrl = [ '$scope', function($scope) {

    if ($scope.property.value == undefined && $scope.property.value == null)
    {
    	$scope.property.value = '';
    }
    $scope.multiInstanceTypeInfos = [{id:'',value:"请选择"},
        {id:"Parallel",value:"并行会签"},{id:"Sequential",value:"串行会签"}];
    $scope.multiInstanceTypeInfo = {};
    for(var i in $scope.multiInstanceTypeInfos){
        if($scope.multiInstanceTypeInfos[i].id==$scope.property.value){//将d是4的城市设为选中项.
            $scope.multiInstanceTypeInfo=$scope.multiInstanceTypeInfos[i];
            break;
        }else {
            $scope.multiInstanceTypeInfo=$scope.multiInstanceTypeInfos[0];
        }
    }
    $scope.multiInstanceChanged = function(m){
        $scope.property.value = $scope.multiInstanceTypeInfo.id;
        for(var i in $scope.multiInstanceTypeInfos){
            if($scope.multiInstanceTypeInfos[i].id==$scope.multiInstanceTypeInfo.id){//将d是4的城市设为选中项.
                $scope.multiInstanceTypeInfo=$scope.multiInstanceTypeInfos[i];
                break;
            }else {
                $scope.multiInstanceTypeInfo=$scope.multiInstanceTypeInfos[0];
            }
        }
        $scope.updatePropertyInModel($scope.property);
    }
}];