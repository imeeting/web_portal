<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-帮助</title>
	<jsp:include page="common/_head.jsp"></jsp:include>
  </head>

  <body>
    <div class="container">
    	<div class="row">
    		<div class="span4 offset4">
    		  <h3>帮助</h3>
		        <div class="accordion" id="accordion-help">
                    <div class="accordion-group">
                        <div class="accordion-heading">
                                <a class="accordion-toggle" data-toggle="collapse"
                                    data-parent="#accordion-help" href="#que0">
                                    智会能做什么
                                </a>
                        </div>
                        <div id="que0" class="accordion-body collapse in">
                            <div class="accordion-inner">
                               使用智会可以发起多方通话，首先从通讯录中选择需要参加多方通话的联系人；
                               进入群聊后可以对参与者进行呼叫，挂断，移除，观看视频等多种操作。
                               <a href="http://v.youku.com/v_show/id_XNDU3MDIxMjky.html">演示视频</a>
                            </div>
                        </div>
                    </div>
                    		        
		            <div class="accordion-group">
		                <div class="accordion-heading">
			                    <a class="accordion-toggle" data-toggle="collapse"
			                        data-parent="#accordion-help" href="#que1">
			                        固话号码可以加入群聊吗
			                    </a>
		                </div>
		                <div id="que1" class="accordion-body collapse in">
		                    <div class="accordion-inner">
		                        虽然智会使用手机号码注册，但是固话也可以加入由智会创建的群聊。发起人添加固
		                        话时候必须使用<strong>区号+号码</strong>的方式，如果没有区号，智会无法呼叫
		                        成功。
		                    </div>
		                </div>
		            </div>
		            
                    <div class="accordion-group">
                        <div class="accordion-heading">
                                <a class="accordion-toggle" data-toggle="collapse"
                                    data-parent="#accordion-help" href="#que2">
                                    每个群聊中最多可以有多少人同时对话
                                </a>
                        </div>
                        <div id="que2" class="accordion-body collapse in">
                            <div class="accordion-inner">
目前系统没有做出限制，但是我们推荐每个群聊中不要超过五路电话，后续版本会增加容量，让更多的人可以参与到群聊中。
                            </div>
                        </div>
                    </div>
                    
                    <div class="accordion-group">
                        <div class="accordion-heading">
                                <a class="accordion-toggle" data-toggle="collapse"
                                    data-parent="#accordion-help" href="#que3">
                                    如何收费
                                </a>
                        </div>
                        <div id="que3" class="accordion-body collapse in">
                            <div class="accordion-inner">
每分钟0.2元人民币，一次群聊的总费用就是群聊中每路电话的通话时长之和乘以0.2元。
                            </div>
                        </div>
                    </div>
                    
                    <div class="accordion-group">
                        <div class="accordion-heading">
                                <a class="accordion-toggle" data-toggle="collapse"
                                    data-parent="#accordion-help" href="#que4">
                                    如何充值
                                </a>
                        </div>
                        <div id="que4" class="accordion-body collapse in">
                            <div class="accordion-inner">
你可以使用支付宝在线充值，也可以购买智会卡进行充值。请访问我们的网站
<a href="http://www.wetalking.net">http://www.wetalking.net</a>了解更多信息。
或者联系客服咨询详情。
                            </div>
                        </div>
                    </div>    
                    
                    <div class="accordion-group">
                        <div class="accordion-heading">
                          <a class="accordion-toggle" data-toggle="collapse"
                              data-parent="#accordion-help" href="#que5">
                                    视频通话是否收费
                           </a>
                        </div>
                        <div id="que5" class="accordion-body collapse in">
                            <div class="accordion-inner">
                            如果你的手机使用Wifi上网，那么视频不产生任何费用。如果使用3G上网，那么将产生
                            一定的流量费，该费用由为你提供网络服务的运营商收取。
                            </div>
                        </div>
                    </div>
                    
                    <div class="accordion-group">
                        <div class="accordion-heading">
                          <a class="accordion-toggle" data-toggle="collapse"
                              data-parent="#accordion-help" href="#que6">
                                    如何联系我们
                           </a>
                        </div>
                        <div id="que6" class="accordion-body collapse in">
                            <div class="accordion-inner">
                <p>网站：<a href="http://www.wetalking.net">http://www.wetalking.net</a></p>
                <p>客服QQ：1622122511</p>
                <p>微信：youyun_zhihui</p>
                <p>客服电话：0551-62379996</p>
                <p>客服邮箱：zhihui@richitec.com</p>
                <p>新浪微博：<a href="http://weibo.com/u/2952506540">优云智会</a></p>
                            </div>
                        </div>
                    </div>                    
		        </div>
    		</div>
    	</div>
    </div> <!-- /container -->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="/imeeting/js/lib/jquery-1.8.0.min.js"></script>
    <script src="/imeeting/js/lib/bootstrap.min.js"></script>

  </body>
</html>
