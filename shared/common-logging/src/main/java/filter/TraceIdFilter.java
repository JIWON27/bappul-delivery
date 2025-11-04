package filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Order(Integer.MIN_VALUE)
public class TraceIdFilter extends OncePerRequestFilter {
  private static final String header = "X-Request-Id";
  public static final String MDC_KEY = "traceId";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String requestId = request.getHeader(header);
    if (!StringUtils.hasText(requestId)) {
      requestId = "REQ-" + UUID.randomUUID().toString().replace("-", "").substring(0,16);
    }
    MDC.put(MDC_KEY, requestId);
    response.setHeader(header, requestId);
    try {
      filterChain.doFilter(request, response);
    }
    finally {
      MDC.clear();
    }
  }
}

