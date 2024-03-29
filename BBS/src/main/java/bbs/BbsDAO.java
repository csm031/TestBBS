package bbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BbsDAO {
	private Connection conn;
	private ResultSet rs;
	
	public BbsDAO() {
		try {
			String dbURL = "jdbc:oracle:thin:@125.138.250.16:1521:xe";
			String dbID = "c##csm";
			String dbPassword = "csm";
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(dbURL, dbID, dbPassword);
		}	catch (Exception e){
				e.printStackTrace();
		}
	}
	public String getDate() {
		String SQL = "select to_char(sysdate, 'YYYY-MM-DD HH24:MI:SS') from BBS";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}
			
		}	catch (Exception e) {
				e.printStackTrace();
		}
		return ""; //데이터베이스 오류
	}
	
	public int getNext() {
		String SQL = "select bbsID from BBS order by bbsID desc";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt (1) + 1;
			}
			return 1; // 첫번째 게시글일 경우
		}	catch (Exception e) {
				e.printStackTrace();
		}
		return -1; //데이터베이스 오류
	}
	
	public int write(String bbsTitle, String userID, String bbsContent) {
		String SQL = "insert into bbs values (?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext());			
			pstmt.setString(2, bbsTitle);
			pstmt.setString(3, userID);
			pstmt.setString(4, getDate());
			pstmt.setString(5, bbsContent);
			pstmt.setInt(6, 1);			
			return pstmt.executeUpdate();
		}	catch (Exception e) {
				e.printStackTrace();
		}
		return -1; //데이터베이스 오류
	}
	
	public ArrayList<Bbs> getList(int pageNumber) {
			String SQL = "SELECT * FROM (SELECT * FROM bbs WHERE bbsID < ? and bbsAvailable = 1 order by bbsID desc) WHERE ROWNUM <=10";
			ArrayList<Bbs> list = new ArrayList<Bbs>();
			try {
				PreparedStatement pstmt = conn.prepareStatement(SQL);
				pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					Bbs bbs = new Bbs();
					bbs.setBbsID(rs.getInt(1));
					bbs.setBbsTitle(rs.getString(2));
					bbs.setUserID(rs.getString(3));
					bbs.setBbsDate(rs.getString(4));
					bbs.setBbsContent(rs.getString(5));
					bbs.setBbsAvailable(rs.getInt(6));											
					list.add(bbs);
				}				
			}	catch (Exception e) {
					e.printStackTrace();
			}
			return list;
	}
	public boolean nextPage(int pageNumber) {
		String SQL = "SELECT * FROM (SELECT * FROM bbs WHERE bbsID < ? and bbsAvailable = 1 order by bbsID desc) WHERE ROWNUM <=10";
			try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return true;
			}
		}	catch (Exception e) {
				e.printStackTrace();
		}
		return false;
	}
	
	public Bbs getBbs(int bbsID) 	{
		String SQL = "SELECT * FROM BBS where bbsID = ?";
		try {
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setInt(1, bbsID);
		rs = pstmt.executeQuery();
		if (rs.next()) {
			Bbs bbs = new Bbs();
			bbs.setBbsID(rs.getInt(1));
			bbs.setBbsTitle(rs.getString(2));
			bbs.setUserID(rs.getString(3));
			bbs.setBbsDate(rs.getString(4));
			bbs.setBbsContent(rs.getString(5));
			bbs.setBbsAvailable(rs.getInt(6));				
			return bbs;
			}
		}	catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int update(int bbsID, String bbsTitle, String bbsContent) {
		String SQL = "update bbs set bbsTitle = ?, bbsContent = ? where bbsID =?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, bbsTitle);			
			pstmt.setString(2, bbsContent);
			pstmt.setInt(3, bbsID);;
			return pstmt.executeUpdate();
		}	catch (Exception e) {
				e.printStackTrace();
		}
		return -1; //데이터베이스 오류
	}
	public int delete(int bbsID) {
		String SQL = "update bbs set bbsAvailable = 0 where bbsID =?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, bbsID);						
			return pstmt.executeUpdate();
		}	catch (Exception e) {
				e.printStackTrace();
		}
		return -1; //데이터베이스 오류
	}
}
