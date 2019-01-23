package Controllers.Blog;

import DAO.DAOFactory;
import DAO.Interfaces.BlogDao;
import Helper.Utile;
import Helper.pagination;
import Models.Blog;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@WebServlet(name = "BlogServlet")
public class BlogServlet extends HttpServlet {

    private BlogDao blog;
    private DAOFactory daoFactory;
    private Blog _blog;
    private int totalBlogs;
    private int id;

    @Override
    public void init() throws ServletException {
        super.init();
        daoFactory = DAOFactory.getInstance();
        blog = daoFactory.getBlogDaoImpl();
        totalBlogs = blog.CountBlogs();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        id = (request.getParameter("id") != null) ? Integer.parseInt(request.getParameter("id")) : 0;

        //PAGINATION
        if (id == 0) {
            int CurrentPage = (request.getParameter("page") != null) ? Integer.parseInt(request.getParameter("page")) : 1;
            List<Blog> blogs = blog.getBlogByPagination((CurrentPage - 1) * 6, 6);
            request.setAttribute("blogs", blogs);
            pagination pg = new pagination(totalBlogs, 6, 5, CurrentPage);
            request.setAttribute("pg", pg);
            this.getServletContext().getRequestDispatcher("/jsp/blog.jsp").forward(request, response);

         //READ ARTICLE
        } else {
            _blog = blog.fetchBlogById(id);
            if (_blog != null) {
                List<Blog> blogs = blog.getBlogByPagination( 1, 3);
                request.setAttribute("blogs", blogs);
                request.setAttribute("article", _blog);
                request.setAttribute("author", !blog.getBlogAuthor(id).isEmpty() ? blog.getBlogAuthor(id).isEmpty() : "Author deleted");
                request.setAttribute("quote", Utile.bloodDonationQuotes[new Random().nextInt(Utile.bloodDonationQuotes.length)]);
                this.getServletContext().getRequestDispatcher("/jsp/post-page.jsp").forward(request, response);
            } else {
                this.getServletContext().getRequestDispatcher("/jsp/error404.jsp").forward(request, response);
            }
        }
    }

}
