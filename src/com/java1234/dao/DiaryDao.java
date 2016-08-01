package com.java1234.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.java1234.model.Diary;
import com.java1234.model.PageBean;
import com.java1234.util.DateUtil;
import com.java1234.util.StringUtil;


public class DiaryDao {
	
	public List<Diary> diaryList(Connection con,PageBean pageBean)throws Exception{
		ArrayList<Diary> diaryList = new ArrayList<Diary>();
		StringBuffer sb = new StringBuffer("select * from t_diary t1,t_diaryType t2 where t1.typeId=t2.diaryTypeId ");
		sb.append(" order by t1.releaseDate desc");
		if(pageBean!=null){
			sb.append(" limit "+pageBean.getStart()+","+pageBean.getPageSize());
		}
		PreparedStatement pstmt = con.prepareStatement(sb.toString());
		ResultSet rs = pstmt.executeQuery();
		while(rs.next()){
			Diary diary = new Diary();
			diary.setDiaryId(rs.getInt("diaryId"));
			diary.setTitle(rs.getString("title"));
			diary.setContent(rs.getString("content"));
			diary.setReleaseDate(DateUtil.formatString(rs.getString("releaseDate"), "yyyy-MM-dd HH:mm:ss"));
			diaryList.add(diary);
		}
		return diaryList;
	}
	
	public int diaryCount(Connection con)throws Exception{		
		StringBuffer sb = new StringBuffer("select count(*) as total from t_diary t1,t_diaryType t2 where t1.typeId=t2.diaryTypeId");
		PreparedStatement pstmt = con.prepareStatement(sb.toString());
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()){
			return rs.getInt("total");
		}else{
			return 0;
		}		
	}
}
