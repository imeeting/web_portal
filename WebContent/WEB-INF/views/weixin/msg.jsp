<%@page import="com.imeeting.mvc.controller.WeiXinController"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String fromUser = (String)request.getAttribute(WeiXinController.FromUserName);
String toUser = (String)request.getAttribute(WeiXinController.ToUserName);
String createTime = (String)request.getAttribute(WeiXinController.CreateTime);
String confId = (String)request.getAttribute(WeiXinController.ConfId);
String content = "";
String funflag = "0";
if (null == confId){
    content = "帮助：\n" +
              "1. 发送微信 kh 创建电话会议，获取会议号。\n" +
              "2. 拨打 0551-62379997 根据提示音输入会议号加入会议。\n" + 
              "3. 如果会议开始后15分钟内没有电话呼入，系统自动结束会议。\n";
} else if ("0".equals(confId)){
    content = "系统太忙，请稍后再试。";
} else {
    content = "请拨打 0551-62379997 加入电话会议，会议号：" + confId +
              "\n[转发邀请好友加入会议]";
    funflag = "1";
}
//content += "\n Android用户可以下载客户端程序，功能更强，使用更方便！下载地址：http://t.cn/zj7nxPo";
%>
   <xml>
    <ToUserName><![CDATA[<%=toUser %>]]></ToUserName>
    <FromUserName><![CDATA[<%=fromUser %>]]></FromUserName>
    <CreateTime><![CDATA[<%=createTime %>]]></CreateTime>
    <MsgType><![CDATA[text]]></MsgType>
    <Content><![CDATA[<%=content %>]]></Content>
    <FuncFlag><%=funflag %></FuncFlag>
   </xml> 