package ysq.fall.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class Pagination implements Serializable {

    private long rowTotalCnt;
    private int rowCntPerPage;
    
    private Integer currentPage;
    private String pageParamName;
    @Deprecated
    private String link;
    //----------------------
    private URL url;
    private String encode;
    
    

    public String getLink(int page) throws UnsupportedEncodingException {
        if(url!=null){
            url.putParam(pageParamName, String.valueOf(page));
            return url.toString(encode);
        }
        return link.replaceFirst(pageParamName, String.valueOf(page));
    }


    public Integer getCurrentPage() {
        if (currentPage < 1) {
            currentPage = 1;
        } else {
            int pageTotalCnt = this.getPageTotalCnt();
            if (currentPage > pageTotalCnt) {
                currentPage = pageTotalCnt;
            }
        }
        return currentPage;
    }

    public int getPageTotalCnt() {
        int pageTotalCnt = (int) (rowTotalCnt / rowCntPerPage);
        if (rowTotalCnt % rowCntPerPage != 0) {
            pageTotalCnt++;
        }
        return pageTotalCnt;
    }
    
    public int offset() {
        return (getCurrentPage() - 1) * rowCntPerPage;
    }
    
    //---------------------------------------

    public long getRowTotalCnt() {
        return rowTotalCnt;
    }

    public void setRowTotalCnt(long rowTotalCnt) {
        this.rowTotalCnt = rowTotalCnt;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public String getPageParamName() {
        return pageParamName;
    }

    public void setPageParamName(String pageParamName) {
        this.pageParamName = pageParamName;
    }

    @Deprecated
    public void setLink(String link) {
        this.link = link;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    /**
     * @return the rowCntPerPage
     */
    public int getRowCntPerPage() {
        return rowCntPerPage;
    }

    /**
     * @param rowCntPerPage the rowCntPerPage to set
     */
    public void setRowCntPerPage(int rowCntPerPage) {
        this.rowCntPerPage = rowCntPerPage;
    }

}
