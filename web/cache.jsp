<%--
  Created by IntelliJ IDEA.
  User: ki264
  Date: 2017/6/25
  Time: 下午 05:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>cache.jsp</title>
    <style type="text/css">
        body {
            font-size: 12px;
        }

        div {
            float: left;
            margin-left: 20px;
        }

        a {
            color: blue;
        }

    </style>
</head>
<body>

<%--登入選單--%>
<div id="loginDiv">
    <a href="cache.jsp" onclick="setCookie('username','helloween');">Login</a><br/>
    <a href="cache.jsp"
       onclick="setCookie('username','Admin');setCookie('role','admin');">Admin Login</a><br/>
</div>

<%--會員選單--%>
<div id="adminDiv" style="display: none;">
    <a href="cache.jsp">Member Management</a><br/>
    <a href="cache.jsp">Notice Managerment</a><br/>
</div>

<%--管理員選單--%>
<div id="controlDiv" style="display: none;">
    <a href="cache.jsp">person setting</a><br/>
    <a href="cache.jsp">change password</a><br/>
    <a href="cache.jsp" onclick="setCookie('username','');setCookie('role','');">Logout</a><br/>
</div>

<script type="text/javascript">

    /**
     * 設定Cookie
     *
     * @param name
     * @param value
     */
    function setCookie(name, value) {
        document.cookie = name + "=" + value;
    }

    /**
     * 獲得Cookie
     *
     * @param name
     * @returns {string}
     */
    function getCookie(name) {
        var search = name + "="
        if (document.cookie.length > 0) {
            offset = document.cookie.indexOf(search)
            if (offset != -1) {
                offset += search.length
                end = document.cookie.indexOf(";", offset)
                if (end = -1) {
                    end = document.cookie.length
                }
                return unescape(document.cookie.substring(offset, end))
            }
        } else return ""
    }

    if (getCookie('username')) {
        //已經登入"隱藏登入選單"
        document.getElementById("logingDiv").innerText = "Welcome， " + getCookie('username');
        //顯示登入後的操作
        document.getElementById("controlDiv").style.display = "block";
    }
    if (getCookie('role') == 'admin') {
        //為管理員，顯示管理員操作
        document.getElementById("adminDiv").style.display = "block";
    }

</script>

</body>
</html>
