<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>流程系统</title>
    <c:set var="ctx" value="${pageContext.request.contextPath}"/>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <jsp:include page="common/header.jsp"/>
</head>
<body>
<div class="navbar navbar-default"  style="background-color:#f2f2f2;">
    <div class="container-fluid">
        <div class="navbar-header" style="margin-top: -12px">
            <a class="navbar-brand"><img src="${ctx}/images/log.png" height="40px"></a>
        </div>
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
<%--                <li>
                    <a href="${ctx}/page/indexPage" class="dropdown-toggle active"   aria-haspopup="true" aria-expanded="false" target="main"><small><strong>首页</strong></small></a>
                </li>--%>

    <c:forEach items="${rmsMenus}" var="menu0">
        <c:forEach items="${menu0.menuArray}" var="menu" varStatus="vs">
            <li>
                <a href="${pageContext.request.contextPath}/${menu.per_action}" class="dropdown-toggle active"   aria-haspopup="true" aria-expanded="false" target="main"><small><strong>${menu.menu_name}</strong></small></a>
            </li>
        </c:forEach>
    </c:forEach>

            </ul>
            <ul class="nav navbar-nav navbar-right">
                <!--<li style="top: 15px;">
                        <label class="control-label">切换岗位</label>
                            <select class="small m-wrap" tabindex="1" onchange="getRole()" id="selectRole">
                                <c:forEach items="${list }" var="item">
                                    <option value="${item.id}">${item.branchname}-${item.departmentname}-${item.specrolename}</option>
                                </c:forEach>

                            </select></li>-->
                <p class="navbar-text" style="font-weight: bold;color: #0000cc">当前登录人：${userInfo.adminname}</p>
                <li class="hvr-bounce-to-bottom"><a href="${ctx}/logOut" target="_parent">退出登录</a></li>
            </ul>
        </div>
    </div>
</div>
</body>
<jsp:include page="common/footer.jsp" />
<script>
    $(function () {
        $("ul li").click(function () {
            $(this).addClass("active");
            $(this).siblings().removeClass("active");
        });
    });
</script>
</body><%--</noframes>--%>
</html>
