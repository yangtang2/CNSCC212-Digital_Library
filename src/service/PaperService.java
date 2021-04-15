package service;

import domain.PageBean;
import domain.Paper;

import java.util.List;

public interface PaperService {
    List<Paper> findAll();

    PageBean<Paper> findPaperByPage(String currentPage, String rows);
}
