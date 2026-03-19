package filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ErrorResponse;
import exception.CurrencyAlreadyExistsException;
import exception.CurrencyNotFoundException;
import exception.DatabaseException;
import exception.ResourceNotFoundException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class ServletFilter implements Filter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
        resp.setContentType("application/json;charset=UTF-8");
        try{
            chain.doFilter(request, response);
        }
        catch(CurrencyAlreadyExistsException e){
            sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        }
        catch (CurrencyNotFoundException | ResourceNotFoundException e){
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
        catch(DatabaseException e){
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        catch (Exception e){

        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }


    private void sendError(HttpServletResponse resp, int code, String message) throws IOException{
        resp.setStatus(code);
        ErrorResponse error = new ErrorResponse();
        error.setMessage(message);

        objectMapper.writeValue(resp.getOutputStream(), error);
    }
}