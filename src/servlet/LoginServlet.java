package servlet;

import dao.UserDao;
import domain.User;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/loginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.设置request编码
        request.setCharacterEncoding("utf-8");
        //2.获取参数
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String checkCode = request.getParameter("checkCode");
        //3.先获取生成的验证码
        HttpSession session = request.getSession();
        String checkCode_session = (String) session.getAttribute("checkCode_session");
        //删除session中存储的验证码
        session.removeAttribute("checkCode_session");
        //3.先判断验证码是否正确
        if (checkCode_session != null && checkCode_session.equalsIgnoreCase(checkCode)) {
            //忽略大小写比较
            //验证码正确
            //判断用户名和密码是否一致
            Map<String, String[]> map = request.getParameterMap();
            //3.创建User对象
            User loginUser = new User();
            //3.2使用BeanUtils封装
            try {
                BeanUtils.populate(loginUser, map);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }


            //4.调用UserDao的login方法
            UserDao dao = new UserDao();
            User user = dao.login(loginUser);

            if (user != null) {//登录成功
                //存储信息，用户信息
                session.setAttribute("user", username);




                    //重定向到success.jsp
                    response.sendRedirect(request.getContextPath() + "/success.jsp");


                } else {
                    //登录失败
                    //存储提示信息到request
                    request.setAttribute("login_error", "用户名或密码错误");
                    //转发到登录页面
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                }


            } else {
                //验证码不一致
                //存储提示信息到request
                request.setAttribute("cc_error", "验证码错误");
                //转发到登录页面
                request.getRequestDispatcher("/login.jsp").forward(request, response);

            }

        }

        protected void doGet (HttpServletRequest request, HttpServletResponse response) throws
        ServletException, IOException {
            this.doPost(request, response);
        }
    }
