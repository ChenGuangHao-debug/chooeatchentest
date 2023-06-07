package chooeat.admin.web.order.service;

import java.util.List;

import chooeat.admin.web.order.pojo.AdminOrderVO;

public interface OrderService {

	public List<AdminOrderVO> selectAll();

	public AdminOrderVO findByProdId(Integer orderId);
}