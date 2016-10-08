<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<%@ include file="../jsp/common/resource.jsp"%>
<%@ include file="../jsp/common/tags.jsp"%>
<c:set var="ctxStatic" value="${pageContext.request.contextPath}/static"/>
<title>PDF展示测试页面</title>

 <script type="text/javascript"  src="${ctx}/static/js/pdfobject.js"></script>
 
 <script type="text/javascript">
 
 window.onload = function (){
	    //var success = new PDFObject({ url: "${ctx}/static/test.pdf" }).embed("pdfDivId");
	    //var success = new PDFObject({ url: "${ctxStatic}/test.pdf" }).embed("pdfDivId");
	    var success = new PDFObject({ url: "${ctx}/test/getPdfStream" }).embed("pdfDivId");
	    console.log("加载pdf结果:"+success);
	};
 </script>
 
 <style type="text/css">
 
 #pdfDivId{
 width: 80%;
 height: 50%;
border: 2px;
border-bottom-color: red;
text-align: center;
 }
 </style>
 
</head>
<body>
<h1>PDF展示测试页面</h1>
<div id="pdfDivId" >

</div>



</body>
</html>