package com.financecontrol.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Tags every incoming web request with a random ID so we can track it through the messy logs if something blows up.
 */
@Component
public class RequestTraceFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String MDC_TRACE_ID_KEY = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Check if the client already sent a trace ID, otherwise generate a new one
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }

        try {
            // Put the trace ID into the MDC (Mapped Diagnostic Context)
            MDC.put(MDC_TRACE_ID_KEY, traceId);
            
            // Add the trace ID to the response headers so the client knows it
            response.setHeader(TRACE_ID_HEADER, traceId);
            
            // Continue processing the request
            filterChain.doFilter(request, response);
        } finally {
            // VERY IMPORTANT: Clear the MDC to prevent memory leaks and data pollution 
            // since threads are reused by the application server (Tomcat)
            MDC.clear();
        }
    }
}
