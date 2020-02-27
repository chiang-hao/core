package chianghao.core.mybatis.plugins;

import java.io.Serializable;


/**
 * 分页信息
 * @author chianghao
 * @param <T>
 *
 */
public class Paging implements Serializable{

	private static final long serialVersionUID = -2513132642456114868L;
	/**
	 * 
	 * @param pageIndex  页数
	 * @param pageSize   每页大小
	 */
	public Paging(int pageIndex,int pageSize) {
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
	}
	
	
	/** 从第几条记录开始 **/
	protected int offset;

	/** 总记录数 **/
	protected long totalRows;

	/** 每页大小 **/
	protected int pageSize = 10;
	
	/**
	 * 如果页码页码设置为小于1，那么用户需要自己计算出start的位置
	 */
	protected int pageIndex = 1;
	
	/** 总页数 **/
	protected long pageCount;
	
	
	public void setPageCount(long pageCount) {
		this.pageCount = pageCount;
	}

	public long getTotalRows() {
		return totalRows;
	}

	public int getPageSize() {
		return pageSize;
	}

	public long getPageCount() {
		return totalRows % pageSize == 0 ? totalRows / pageSize : totalRows / pageSize + 1;
	}


	public void setTotalRows(long totalRows) {
		this.totalRows = totalRows;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public int getOffset() {
		if(pageIndex>=1){
			offset = (pageIndex - 1) * pageSize;
		}
		return offset;
	}
	
	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	
}
