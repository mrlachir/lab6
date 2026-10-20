package org.example.web;
import org.example.service.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.YearMonth;

@WebServlet("/rapport-ventes")
public class RapportServlet extends HttpServlet{
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws IOException{
        resp.setContentType("text/html;charset=UTF-8");
        YearMonth month=YearMonth.now().minusMonths(1);
        java.io.PrintWriter out=resp.getWriter();
        out.println("<!doctype html><html lang='fr'><meta charset='UTF-8'><title>Ventes mensuelles</title>");
        out.println("<h1>Ventes par categorie — "+month+"</h1><table><tr><th>Categorie</th><th>Chiffre d'affaires</th></tr>");
        for(VenteMensuelle row:new RapportService().rapport(month))
            out.println("<tr><td>"+escape(row.getCategorie())+"</td><td>"+row.getChiffreAffaires().toPlainString()+"</td></tr>");
        out.println("</table></html>");
    }
    private String escape(String s){return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;");}
}
