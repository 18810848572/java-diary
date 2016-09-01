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

	public List<Diary> diaryList(Connection con, PageBean pageBean, Diary s_diary) throws Exception {
		ArrayList<Diary> diaryList = new ArrayList<Diary>();
		StringBuffer sb = new StringBuffer("select * from t_diary t1,t_diaryType t2 where t1.typeId=t2.diaryTypeId ");
		if (s_diary.getTypeId() != -1) {
			sb.append(" and t1.typeId=" + s_diary.getTypeId());
		}
		if (s_diary.getReleaseDateStr() != null) {
			sb.append(" and date_format(releaseDate,'%Y年%m月')='" + s_diary.getReleaseDateStr() + "'");
		}
		if (s_diary.getTitle() != null) {
			sb.append(" and t1.title like '%" + s_diary.getTitle() + "%'");
		}
		sb.append(" order by t1.releaseDate desc");
		if (pageBean != null) {
			sb.append(" limit " + pageBean.getStart() + "," + pageBean.getPageSize());
		}
		PreparedStatement pstmt = con.prepareStatement(sb.toString());
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			Diary diary = new Diary();
			diary.setDiaryId(rs.getInt("diaryId"));
			diary.setTitle(rs.getString("title"));
			diary.setContent(rs.getString("content"));
			diary.setReleaseDate(DateUtil.formatString(rs.getString("releaseDate"), "yyyy-MM-dd HH:mm:ss"));
			diaryList.add(diary);
		}
		return diaryList;
	}

	public int diaryCount(Connection con, Diary s_diary) throws Exception {
		StringBuffer sb = new StringBuffer(
				"select count(*) as total from t_diary t1,t_diaryType t2 where t1.typeId=t2.diaryTypeId");
		if (s_diary.getTypeId() != -1) {
			sb.append(" and t1.typeId=" + s_diary.getTypeId());
		}
		if (s_diary.getReleaseDateStr() != null) {
			sb.append(" and date_format(releaseDate,'%Y年%m月')='" + s_diary.getReleaseDateStr() + "'");
		}
		if (s_diary.getTitle() != null) {
			sb.append(" and t1.title like '%" + s_diary.getTitle() + "%'");
		}
		PreparedStatement pstmt = con.prepareStatement(sb.toString());
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			return rs.getInt("total");
		} else {
			return 0;
		}
	}

	public List<Diary> diaryCountList(Connection con) throws Exception {
		List<Diary> diaryCountList = new ArrayList<Diary>();
		String sql = "select date_format(releaseDate,'%Y年%m月') as releaseDateStr ,count(*) as diaryCount from t_diary group by date_format(releaseDate,'%Y年%m月') order by date_format(releaseDate,'%Y年%m月') desc";
		PreparedStatement pstmt = con.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			Diary diary = new Diary();
			diary.setReleaseDateStr(rs.getString("releaseDateStr"));
			diary.setDiaryCount(rs.getInt("diaryCount"));
			diaryCountList.add(diary);
		}
		return diaryCountList;
	}
	
	public Diary diaryShow(Connection con,String diaryId) throws Exception{
		String sql="select * from t_diary t1,t_diaryType t2 where t1.typeId=t2.diaryTypeId and t1.diaryId=?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, Integer.parseInt(diaryId));
		ResultSet rs = pstmt.executeQuery();
		Diary diary = new Diary();
		while(rs.next()){
			diary.setDiaryId(rs.getInt("diaryId"));
			diary.setTitle(rs.getString("title"));
			diary.setContent(rs.getString("content"));
			diary.setTypeId(rs.getInt("typeId"));
			diary.setReleaseDate(DateUtil.formatString(rs.getString("releaseDate"), "yyyy-MM-dd HH:mm:ss"));
			diary.setTypeName(rs.getString("typeName"));
		}
		return diary;
	}
	
	public int diaryAdd(Connection con,Diary diary)throws Exception{
		String sql = "insert into t_diary values(null,?,?,?,now())";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, diary.getTitle());
		pstmt.setString(2, diary.getContent());
		pstmt.setInt(3, diary.getTypeId());
		return pstmt.executeUpdate();
	}
	
	public int diaryDelete(Connection con,String diaryId)throws Exception{
		String sql = "delete from t_diary where diaryId=?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, Integer.parseInt(diaryId));
		return pstmt.executeUpdate();
	}
	
	public int diaryUpdate(Connection con,Diary diary)throws Exception{
		String sql = "update t_diary set title=?,content=?,typeId=? where diaryId=?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, diary.getTitle());
		pstmt.setString(2, diary.getContent());
		pstmt.setInt(3, diary.getTypeId());
		pstmt.setInt(4, diary.getDiaryId());
		return pstmt.executeUpdate();
	}
	
	public boolean existDiaryWithTypeId(Connection con,String typeId)throws Exception{
		String sql = "select * from t_diary where typeId=?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, Integer.parseInt(typeId));
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()){
			return true;
		}else{
			return false;
		}
	}
}
