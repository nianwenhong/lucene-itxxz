package com.itxxz.util;

public class Pageinate {
	
	private int start = 0;
	private int end = -1;
	private int pageSize =5;//ÿҳ��ʾ��������¼
	private int pageCount = 0;//һ���ж���ҳ
	private int rowCount;//һ���ж�������¼
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public int getRowCount() {
		return rowCount;
	}
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}
	@Override
	public String toString() {
		return "Pageinate [start=" + start + ", end=" + end + ", pageSize="
				+ pageSize + ", pageCount=" + pageCount + ", rowCount="
				+ rowCount + "]";
	}
	
}
