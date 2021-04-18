package web;

import domain.Paper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import service.PaperService;
import service.impl.PaperServiceImpl;
import service.impl.UniversityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

@WebServlet("/servlet/AjaxUploadPaperServlet")
public class AjaxUploadPaper extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        final String date = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
        final PrintWriter writer = resp.getWriter();

        //Instantiate the disk file list factory
        DiskFileItemFactory factory=new DiskFileItemFactory();
        // Get the file upload object from the factory
        ServletFileUpload upload=new ServletFileUpload(factory);
        // Solving encoding/decoding problem
        req.setCharacterEncoding("utf-8");

        Paper paper = new Paper(); // Paper object that will be added into database
        paper.setSubmit_date(date);
        try {
            // Obtain all elements in request
            List<FileItem> list = upload.parseRequest(req);
            // Set the attributes of uploaded paper
            for (FileItem fileItem : list) {
                switch (fileItem.getFieldName()) {
                    case "title":
                        paper.setTitle(fileItem.getString());
                        break;
                    case "author":
                        paper.setAuthor(fileItem.getString());
                        break;
                    case "email":
                        paper.setUniversity(new UniversityServiceImpl().findNameByEmail(fileItem.getString()));
                        break;
                    case "outline":
                        paper.setOutline(fileItem.getString());
                        break;
                    case "keyword":
                        paper.setKeyword(fileItem.getString());
                        break;
                    case "major":
                        paper.setMajor(fileItem.getString());
                        break;
                }
            }

        } catch (FileUploadException e) {
            e.printStackTrace();
        }

        // Add uploaded paper into database
        PaperService paperService = new PaperServiceImpl();
        if (!paperService.checkPaperMajor(paper.getMajor())) {
            writer.write("majorError");
        } else {
            int errCode = paperService.addPaper(paper);
            if (errCode == 0) {
                writer.write("reviewerError");
            } else if (errCode == 1) {
                writer.write("succeed");
            } else {
                writer.write("error");
            }
        }

        // Store uploaded paper in given directory
        // Set buffer size
        factory.setSizeThreshold(1024*1024*10);
        // Set single file size limit
        upload.setFileSizeMax(1024*1024*10);

        try {
            // Obtain all elements in request
            List<FileItem> list = upload.parseRequest(req);
            for (FileItem fileItem : list) {
                if (!fileItem.isFormField()&&fileItem.getName()!=null&&!"".equals(fileItem.getName())){
                    String filName=fileItem.getName();
                    System.out.println(filName);
                    String uuid= UUID.randomUUID().toString();
                    //获取文件后缀名
                    String suffix=filName.substring(filName.lastIndexOf("."));

                    //获取文件上传目录路径，在项目部署路径下的upload目录里。若想让浏览器不能直接访问到图片，可以放在WEB-INF下
                    String uploadPath=req.getSession().getServletContext().getRealPath("/papers");

                    File file=new File(uploadPath);
                    file.mkdirs();
                    //写入文件到磁盘，该行执行完毕后，若有该临时文件，将会自动删除
                    fileItem.write(new File(uploadPath,uuid+suffix));
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}