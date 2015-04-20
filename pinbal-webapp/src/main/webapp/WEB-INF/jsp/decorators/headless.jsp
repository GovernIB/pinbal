<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title><decorator:title default="Benvinguts" /></title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<meta name="description" content=""/>
	<meta name="author" content=""/>

	<link href="<c:url value="/css/bootstrap.min.css"/>" rel="stylesheet">
    <link href="<c:url value="/css/bootstrap-responsive.css"/>" rel="stylesheet">
    <link href="<c:url value="/css/default.css"/>" rel="stylesheet">
	<link href="<c:url value="/css/orange-header.css"/>" rel="stylesheet">
	<!-- Llibreria per a compatibilitat amb HTML5 -->
	<!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
	<script src="<c:url value="/js/jquery.min.js"/>"></script>
	<decorator:head />
</head>
<body>
	<decorator:body />
	<script src="<c:url value="/js/bootstrap.min.js"/>"></script>
</body>
</html>
