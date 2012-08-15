<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    
			<% for (int i=0; i<10; i++) { %>
			<div class="well clearfix">
				<div class="pull-left">
					<h3><%="这里是群聊标题"%></h3>
					<div class="clearfix">
						<% for (int j=0; j<4; j++) {%>
						<div class="pull-left">
							<img alt="avatar" src="img/avatar.jpg">
							<p><%="胡光辉" %></p>
						</div>
						<% } %>
					</div>
				</div>
				<div class="pull-right">
					<h3><small>群聊号：<strong><%="123456"%></strong></small></h3>
					<h3><small>
						<strong><%="08"%></strong>月<strong><%="11"%></strong>日<br>
						<%="星期日"%><br>
						<strong><%="下午"%>&nbsp;<%="17:05"%></strong>
					</small></h3>
				</div>
			</div>
			<% } %>
			<ul class="pager">
				<li class="previous"><a href=#>上一页</a></li>
				<li class="next"><a href=#>下一页</a></li>
			</ul>
