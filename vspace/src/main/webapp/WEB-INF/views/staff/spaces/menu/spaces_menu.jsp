<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script>
//# sourceURL=list.js
$( document ).ready(function() {
	$.get( "<c:url value="/staff/api/space/list" />", function( data ) {
		  elements = JSON.parse(data);
		  jQuery.each(elements, function(index, element) {
			  var li = $('<li class="nav-item"></li>');
			  var a = $('<a class="nav-link"></a>');
			  a.attr('href', element.id);
			  li.append(a);
			  a.html('<span data-feather="codepen"></span>' + element.name);
			  $("#spacelist").append(li);
		  });
		  feather.replace();
	});
});
</script>

<h6 class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted">
   <span>Spaces</span>
   <a class="d-flex align-items-center text-muted" href="<c:url value="/staff/space/add" />">
     <span data-feather="plus-circle"></span>
   </a>
 </h6>
 <ul id="spacelist" class="nav flex-column mb-2">
 </ul>