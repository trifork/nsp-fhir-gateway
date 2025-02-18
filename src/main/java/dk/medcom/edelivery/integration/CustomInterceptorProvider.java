package dk.medcom.edelivery.integration;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.message.Message;

import java.util.ArrayList;
import java.util.List;

public class CustomInterceptorProvider implements InterceptorProvider {

    private final List<Interceptor<? extends Message>> inInterceptors = new ArrayList<>();
    private final List<Interceptor<? extends Message>> outInterceptors = new ArrayList<>();
    private final List<Interceptor<? extends Message>> inFaultInterceptors = new ArrayList<>();
    private final List<Interceptor<? extends Message>> outFaultInterceptors = new ArrayList<>();

    @Override
    public List<Interceptor<? extends Message>> getInInterceptors() {
        return inInterceptors;
    }

    @Override
    public List<Interceptor<? extends Message>> getOutInterceptors() {
        return outInterceptors;
    }

    @Override
    public List<Interceptor<? extends Message>> getInFaultInterceptors() {
        return inFaultInterceptors;
    }

    @Override
    public List<Interceptor<? extends Message>> getOutFaultInterceptors() {
        return outFaultInterceptors;
    }
}
