<!-- STRUTS -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!-- END STRUTS -->

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title></title>
</head>
<body>
<h1 style="color: blue; text-align: center;">
    <span style="font-family:lucida sans unicode,lucida grande,sans-serif;"><strong>IdeaBroker</strong></span></h1>
<hr />
<p>
    &nbsp;</p>
<table align="center" border="1" cellpadding="1" cellspacing="1" style="width: 500px; background: url('<s:url
        value="images/bg_gradient_tiny.png"/>');">
    <tbody>
    <tr>
        <td>
            <h2 style="text-align: center;">
                <em>REGISTRATION</em></h2>
        </td>
    </tr>
    </tbody>
</table>
<div style="text-align: center;">
    <span  style="text-align: center;">
    <s:form action="register">

        <s:textfield name="username" label="Username" />
        <s:password  name="password" label="Password" />
        <s:textfield name="email" label="Email" />

        <s:submit value="Submit" />
    </s:form>
        Deveria estar alinhado, mas o Struts é merda e não alinha. Cagai, irmãos, cagai.<br />
        <!-- FIXME: Estou a assumir que depois do registo fazemos login automaticamente, mas isso pode-se mudar -->
        </span>
</div>
</body>
</html>
