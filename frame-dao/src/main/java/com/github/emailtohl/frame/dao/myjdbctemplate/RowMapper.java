package com.github.emailtohl.frame.dao.myjdbctemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *************************************************
 * 模拟Spring的接口
 * 
 * @author helei
 * @version 1.0
 * 2015年4月30日 创建文件
 *************************************************
 */
public interface RowMapper<T> {
	public T mapRow(ResultSet resultSet, int rowNum) throws SQLException;
}
