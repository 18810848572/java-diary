package com.java1234.web;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.java1234.dao.DiaryDao;
import com.java1234.model.Diary;
import com.java1234.model.PageBean;
import com.java1234.util.DbUtil;
import com.java1234.util.PropertiesUtil;
import com.java1234.util.StringUtil;

import sun.print.PageableDoc;

public class MainServlet extends HttpServlet{
	
	DbUtil dbUtil = new DbUtil();
	DiaryDao diaryDao = new DiaryDao();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		Connection con = null;
		String page = request.getParameter("page");
		if(StringUtil.isEmpty(page)){
			page = "1";
		}
		PageBean pageBean = new PageBean(Integer.parseInt(page),Integer.parseInt(PropertiesUtil.getValue("pageSize")));
		try {
			con=dbUtil.getCon();
			List<Diary> diaryList = diaryDao.diaryList(con,pageBean);
			int total = diaryDao.diaryCount(con);
			String pageCode = this.getPagetion(total, Integer.parseInt(page), Integer.parseInt(PropertiesUtil.getValue("pageSize")));
			request.setAttribute("diaryList", diaryList);
			request.setAttribute("pageCode", pageCode);
			request.setAttribute("mainPage", "diary/diaryList.jsp");
			request.getRequestDispatcher("mainTemp.jsp").forward(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				dbUtil.closeCon(con);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	
	private String getPagetion(int totalNum,int currentPage,int pageSize){
		int totalPage = totalNum%pageSize==0?totalNum/pageSize:totalNum/pageSize+1;
		StringBuffer pageCode = new StringBuffer();
		pageCode.append("<li><a href='main?page=1'>首页</a></li>");
		if(currentPage==1){
			pageCode.append("<li class='disabled'><a href='#'>上一页</a></li>");
		}else{
			pageCode.append("<li ><a href='main?page="+(currentPage-1)+"'>上一页</a></li>");
		}
		for(int i = currentPage-2;i<=currentPage+2;i++){
			if(i<1||i>totalPage){
				continue;
			}
			if(i==currentPage){
				pageCode.append("<li class='active'><a href='#'>"+i+"</a></li>");
			}else{
				pageCode.append("<li><a href='main?page="+i+"'>"+i+"</a></li>");
			}
			
		}
		if(currentPage==totalPage){
			pageCode.append("<li class='disabled'><a href='#'>下一页</a></li>");
		}else{
			pageCode.append("<li ><a href='main?page="+(currentPage+1)+"'>下一页</a></li>");
		}
		pageCode.append("<li><a href='main?page="+totalPage+"'>尾页</a></li>");
		return pageCode.toString();
	}
	
}
