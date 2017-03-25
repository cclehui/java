package common;

public class Pager {

	/**
	 * 分页
	 * @说明：本分页采用直接获取每一页的数据
	 * @author Etwo
	 */
	
	/*
	 * 分页要素：
	 * 1、首页				first
	 * 2、最后一页			last
	 * 3、页总数				pageCount
	 * 4、总共显示的数据记录	rowCount
	 * 5、每页显示的数据记录	pageRowCount
	 * 6、当前页				curPage	
	 * 7、定义每一页的开始	start
	 * 8、定义每一页的结束	end
	 */
	
	/**
	 * 1、首页				first
	 */
	private int first;
	
	/**
	 * 2、最后一页			last
	 */
	private int last;
	
	/**
	 * 3、页总数				pageCount
	 */
	private int pageCount;
	
	/**
	 * 4、总共显示的数据记录	totalCount
	 */
	private long totalCount;
	
	/**
	 * 5、每页显示的数据记录	pageRowCount
	 */
	private int pageRowCount;
	
	/**
	 * 6、当前页				curPage	
	 */
	private int curPage;
	
	/**
	 * 7、定义每一页的开始	start
	 */
	private int start;
	
	/**
	 * 8、定义每一页的结束	end
	 */
	private int end;


	/**
	 * 计算并得出页总数
	 * @return
	 */
	public int getPageCount() {
		this.pageCount = (int)Math.ceil((double)this.getTotalCount() / (double)this.getPageRowCount());
		return pageCount;
	}

	/**
	 * 得到总共显示的数据记录
	 * @return
	 */
	public long getTotalCount() {
		return totalCount;
	}

	/**
	 * 设置总共显示的数据记录
	 * @param rowCount
	 */
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * 得到每页显示的数据记录
	 * @return
	 */
	public int getPageRowCount() {
		return pageRowCount;
	}

	/**
	 * 设置每页显示的数据记录
	 * @param pageRowCount
	 */
	public void setPageRowCount(int pageRowCount) {
		this.pageRowCount = pageRowCount;
	}

	/**
	 * 得到当前页
	 * @return
	 */
	public int getCurPage() {
		if(curPage <= 0 ) {
			curPage = 1;
		} else if(curPage >= this.getPageCount()) {
			curPage = this.getPageCount();
		}
		return curPage;
	}

	/**
	 * 设置当前页
	 * @param curPage
	 */
	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	/**
	 * 得到每页的开始记录数
	 * @return
	 */
	public int getStart() {
		this.start = (this.getCurPage() - 1) * this.getPageRowCount();
		return start;
	}

	/**
	 * 得到每页的结束记录数
	 * @return
	 */
	public int getEnd() {
		this.end = this.getPageRowCount();
		return end;
	}

	/**
	 * 得到首页
	 * @return
	 */
	public int getFirst() {
		this.first = 1;
		return this.first;
	}
	
	/**
	 * 得到最后一页
	 * @return
	 */
	public int getLast() {
		this.last = (int)this.getPageCount();
		return last;
	}
	
	/**
	 * 上一页
	 * @return
	 */
	public int previous() {
		return (this.getCurPage() > 1) ? this.getCurPage() - 1 : 1;
	}
	
	/**
	 * 下一页
	 * @return
	 */
	public int next() {
		return (this.getCurPage() < (int)this.getPageCount()) ? this.getCurPage() + 1 : (int)this.getPageCount();
	}
	
	/**
	 * 判断是否是首页
	 * @return
	 */
	public boolean isFirst() {
		return (this.getCurPage() == 1) ? true : false;
	}
	
	/**
	 * 判断是否是最后一页
	 * @return
	 */
	public boolean isLast() {
		return (this.getCurPage() == this.getPageCount()) ? true : false;
	}
	
	/**
	 * 客户端显示的工具条
	 */
	public String getToolBar(String url) {
        String str, temp;
        
        if (this.totalCount <= 0 || this.pageRowCount <= 0 || this.pageRowCount > this.totalCount) {
        	return "";
        }
        
        //用于判断url中是否存在？
        if(url.indexOf("?") == -1) {
            temp = "?";
        } else {
            temp = "&";
        }
        
        str = "<form method='post' name='frmPage' action='" + url + "'> ";
        str += "<p id='page'>";
        str += "<span style='font-size: 12px; font-weight: bold;'>";
        //判断是否是首页
        if(isFirst()) {
        	str += "首页 上一页&nbsp;";
        } else {
            str += "<a href='" + url + temp + "pg=" + this.getFirst() + "'>首页</a>&nbsp;";
            str += "<a href='" + url + temp + "pg=" + this.previous() + "'>上一页</a>&nbsp;";
        }
        //判断是否是最后一页
        if(isLast()) {
        	str += "下一页 尾页&nbsp;";
        } else {
            str += "<a href='" + url + temp + "pg=" + this.next() + "'>下一页</a>&nbsp;";
            str += "<a href='" + url + temp + "pg=" + this.getLast() + "'>尾页</a>&nbsp;";
        }
        str += "&nbsp;共<b>" + this.totalCount + "</b>条记录&nbsp;";
        str += "&nbsp;转到<select name='page' onChange=\"location='" + url + temp + "pg='+this.options[this.selectedIndex].value\">";
        
        for(int i = 1; i <= this.getPageCount(); i++) {
        	//判断是否是当前页，若是，则默认为选中当前页
            if(i == this.getCurPage())
                str += "<option value='" + i + "' selected>第" + i + "页</option>";
            else
                str += "<option value='" + i + "'>第" + i + "页</option>";
        }
        str += "</select></span></p></form>";
        return str;
	}
}
