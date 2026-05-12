<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>[<c:forEach var="servei" items="${serveis}" varStatus="status">
{"codi" : "${servei.codi}", "descripcio": "${servei.descripcio}"}<c:if test="${not status.last}">,</c:if></c:forEach>
]
