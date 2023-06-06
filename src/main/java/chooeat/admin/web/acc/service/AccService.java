package chooeat.admin.web.acc.service;

import java.util.List;

import chooeat.admin.web.acc.pojo.AdminAccountVO;

public interface AccService {

	AdminAccountVO edit();
	
	List<AdminAccountVO> findAll();
	
	List<AdminAccountVO> searchBySomething(String searchType, String searchCondition);
	
}
