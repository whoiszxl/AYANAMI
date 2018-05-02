package com.whoiszxl.controller.portal;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.whoiszxl.common.Const;
import com.whoiszxl.common.ResponseCode;
import com.whoiszxl.common.ServerResponse;
import com.whoiszxl.entity.User;
import com.whoiszxl.service.OrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author whoiszxl
 *
 */
@Api(value = "前台订单模块",description = "前台订单模块")
@RestController
@RequestMapping("/order/")
public class OrderController {

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	private OrderService orderService;
	
	
	@PostMapping("create")
	@ApiOperation(value = "创建订单接口")
	public ServerResponse create(HttpSession session,Integer shippingId) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		return orderService.createOrder(user.getId(),shippingId);
	}
	
	
	@PostMapping("cancel")
	@ApiOperation(value = "取消订单接口")
	public ServerResponse cancel(HttpSession session,Long orderNo) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		return orderService.cancel(user.getId(), orderNo);
	}
	
	@PostMapping("get_order_cart_product")
	@ApiOperation(value = "获取订单的商品集合,缩略图和总价接口")
    public ServerResponse getOrderCartProduct(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        return orderService.getOrderCartProduct(user.getId());
    }



    @GetMapping("detail")
    @ApiOperation(value = "获取订单详情接口")
    public ServerResponse detail(HttpSession session,Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return orderService.getOrderDetail(user.getId(),orderNo);
    }

    @GetMapping("list")
    @ApiOperation(value = "获取订单列表接口")
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return orderService.getOrderList(user.getId(),pageNum,pageSize);
    }

	
	
	
	
	
	
	
	
	@PostMapping("pay")
	@ApiOperation(value = "支付宝支付接口")
	public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		String path = request.getSession().getServletContext().getRealPath("upload");
		return orderService.pay(orderNo, user.getId(), path);
	}
	
	
	
	@PostMapping("query_order_pay_status")
	@ApiOperation(value = "查询订单支付状态接口")
	public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		ServerResponse serverResponse = orderService.queryOrderPayStatus(user.getId(), orderNo);
		if(serverResponse.isSuccess()) {
			return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
		
	}
	
}
