package com.example.riana.a500px;

import java.util.ArrayList;

/**
 * Created by riana on 2019-01-30.
 */

public class Paginator {
    public static final int ITEMS_PER_PAGE=100;
    public int numberOfPages;

    public ArrayList<String> generatePage(int currentPage)
    {
        int startItem=currentPage*ITEMS_PER_PAGE+1;
        int numOfData=ITEMS_PER_PAGE;

        ArrayList<String> pageData=new ArrayList<>();

        return pageData;
    }

    public void setPages(int pages){
        numberOfPages = pages;
    }
}
