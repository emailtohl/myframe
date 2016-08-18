package com.github.emailtohl.frame.site.service;

import java.util.List;

import com.github.emailtohl.frame.dao.Pager;
import com.github.emailtohl.frame.site.dto.GoodsDto;

public interface IGoodsService {
	List<GoodsDto> queryGoods(GoodsDto goodsDto);
	
	GoodsDto getGoodsById(Long id);

	Pager<GoodsDto> queryPage(GoodsDto goodsDto);

	int updateGoods(GoodsDto goodsDto);

	int deleteGoods(GoodsDto goodsDto);

	long addGoods(GoodsDto goodsDto);
}
